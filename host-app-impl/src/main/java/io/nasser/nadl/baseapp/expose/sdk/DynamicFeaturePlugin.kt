package io.nasser.nadl.baseapp.expose.sdk

import android.content.Context
import android.content.res.Resources
import android.content.res.loader.ResourcesLoader
import android.content.res.loader.ResourcesProvider
import android.os.Build
import android.os.ParcelFileDescriptor
import android.widget.Toast
import androidx.annotation.RequiresApi
import dalvik.system.BaseDexClassLoader
import dalvik.system.DexClassLoader
import dalvik.system.PathClassLoader
import io.nasser.nadl.baseapp.expose.Const.APK_NAME
import io.nasser.nadl.baseapp.expose.sdk.res.RuntimeAssetManager
import io.nasser.nadl.baseapp.expose.sdk.res.RuntimeResource
import io.nasser.nadl.baseapp.expose.downloadFileWithUrlCachingFlow
import io.nasser.nadl.baseapp.expose.getAssetAsTempFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.io.File

class DynamicFeaturePlugin(
    private val appContext: Context,
) {

    private var apkFile: File? = null
    private var dexLoader: BaseDexClassLoader? = null
    private var resourcesLoader: ResourcesLoader? = null
    private var inCleaning = false

    internal fun getClassLoader(): BaseDexClassLoader {
        if (dexLoader == null) {
            if (!isReady()) {
                throw IllegalStateException("The dex file is not available")
            }
            val absolutePath = apkFile!!.absolutePath
            val classLoader = appContext.classLoader
            dexLoader = PathClassLoader(absolutePath, classLoader)
        }
        return dexLoader!!
    }

    fun isReady(): Boolean = apkFile?.exists() ?: false

    private fun loadDexFlow(context: Context): Flow<Unit> {
        if (isReady()) {
            return flowOf(Unit)
        }
        return loadApkFromAsset(context)
    }

    private fun loadResource(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //TODO, It create problem, If R conflict solving in exporting does not work properly.
//            overrideResourcesApi30AndMore(context)
        }
    }

    fun loadEverythingFlow(context: Context): Flow<Unit> {
        return loadDexFlow(context).onEach {
            loadResource(context)
        }
    }

    private fun loadApkFromAsset(context: Context): Flow<Unit> {
        return flow {
            try {
                val file = getAssetAsTempFile(context, APK_NAME)
                apkFile = file
            } catch (e: Exception) {
                e.stackTraceToString()
            }

            emit(Unit)
        }
    }

    fun clear(context: Context) {
        inCleaning = true
        apkFile = null
        dexLoader = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            resourcesLoader?.let {
                context.resources.removeLoaders(it)
            }
            Resources.getSystem().addLoaders(resourcesLoader)
            resourcesLoader = null

        }
        inCleaning = false
    }


    @RequiresApi(Build.VERSION_CODES.R)
    private fun overrideResourcesApi30AndMore(context: Context) {
        resourcesLoader = ResourcesLoader().also {
            val apkFd = ParcelFileDescriptor.open(apkFile, ParcelFileDescriptor.MODE_READ_ONLY)
            val provider = ResourcesProvider.loadFromApk(apkFd)
            it.addProvider(provider)
        }
        Resources.getSystem().addLoaders(resourcesLoader)
        context.resources.addLoaders(resourcesLoader)
    }

    private fun loadFromHttpAndStoreFileFlow(context: Context, url: String): Flow<Unit> {
        return downloadFileWithUrlCachingFlow(context, url)
            .map {
                apkFile = it
                Toast.makeText(appContext, "download completed", Toast.LENGTH_SHORT).show()
            }
    }


    companion object {
        private const val FILE_VALUES = "Values.zip"

        fun properContext(newBase: Context): Context {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                return newBase
            }
            return newBase
            //todo: need caching check
            val valueFile = loadValueFile(newBase)
            val apkFile = getAssetAsTempFile(newBase, APK_NAME)

            //does not work
            val newResource = RuntimeResource(
                newBase.resources,
                RuntimeAssetManager(newBase.filesDir, apkFile, valueFile, "en")
            )
            return OptionalContextWrapper(
                newBase,
                newResource
            )
        }

        private fun loadValueFile(newBase: Context): File {
            return getAssetAsTempFile(newBase, FILE_VALUES)
        }
    }
}