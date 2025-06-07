package io.nasser.nadl.baseapp.expose

import android.content.Context
import android.content.res.Resources
import android.content.res.loader.ResourcesLoader
import android.content.res.loader.ResourcesProvider
import android.os.Build
import android.os.ParcelFileDescriptor
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import dalvik.system.BaseDexClassLoader
import dalvik.system.PathClassLoader
import io.nasser.mylib.api.HelloWorld
import io.nasser.nadl.baseapp.expose.Const.APK_NAME
import io.nasser.nadl.baseapp.expose.content.OptionalContextWrapper
import io.nasser.nadl.baseapp.expose.content.res.ApkResourceLoader
import io.nasser.nadl.baseapp.expose.content.res.idmap.ResIdLoader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.io.File


class DynamicFeaturePlugin(
    private val appContext: Context,
    val nativeLibLoader: NativeLibLoader
) {

    private var apkFile: File? = null
    private var dexLoader: BaseDexClassLoader? = null

    private fun getClassLoader(): BaseDexClassLoader {
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

    val classFactory = ClassFactory(this)

    fun isReady(): Boolean = apkFile?.exists() ?: false

    private fun loadDexFlow(context: Context): Flow<Unit> {
        if (isReady()) {
            return flowOf(Unit)
        }
        return loadApkFromAsset(context)
//        return loadFromHttpAndStoreFileFlow(context, Const.APK_URL)

    }

    private fun loadResource(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            overrideResourcesApi30AndMore(context)
        } else {
            //TODO:
        }
    }

    fun loadEverythingFlow(context: Context): Flow<Unit> {
        return loadDexFlow(context).onEach {
            loadResource(context)
        }
    }

    private fun loadApkFromAsset(context: Context) : Flow<Unit> {
        return  flow {
            try {
                val file = getAssetAsTempFile(context,APK_NAME)
                apkFile = file
            }catch (e:Exception){
                e.stackTraceToString()
            }

            emit(Unit)
        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
    private fun overrideResourcesApi30AndMore(context: Context) {
        val loader = ResourcesLoader().also {
            val apkFd = ParcelFileDescriptor.open(apkFile, ParcelFileDescriptor.MODE_READ_ONLY)
            val provider = ResourcesProvider.loadFromApk(apkFd)
            it.addProvider(provider)
        }
        Resources.getSystem().addLoaders(loader)
        context.resources.addLoaders(loader)
    }

    private fun loadFromHttpAndStoreFileFlow(context: Context, url: String): Flow<Unit> {
        return downloadFileWithUrlCachingFlow(context,url)
            .map {
                apkFile = it
                Toast.makeText(appContext, "download completed", Toast.LENGTH_SHORT).show()
            }
    }


    class ClassFactory(private val parent: DynamicFeaturePlugin) {

        companion object {
            //TODO: move these to its api module
            private const val LIB_PATH = "io.nasser.mylib.impl"
            private const val CLASS_PATH_HelloWorld = "$LIB_PATH.HelloWorldImpl"
            private const val CLASS_PATH_StarterFragmentXml = "$LIB_PATH.StarterFragmentXml"
            private const val CLASS_PATH_StarterFragmentBasic = "$LIB_PATH.StarterFragmentBasic"
            private const val CLASS_PATH_StarterFragmentCompose = "io.nasser.composesamplemodule.ui.ComposeSampleFragment"

            private const val KEY_CENTER_MESSAGE_TEXT = "centerTextMessage"
        }

        fun newHelloWorld(): HelloWorld {
            val dexClassLoader = parent.getClassLoader()
            return HelloWorldLoader(dexClassLoader.loadClass(CLASS_PATH_HelloWorld))
        }

        fun newStarterFragmentXml(centerTextMessage: String): Fragment {
            val loadClassPath = parent.getClassLoader().loadClass(CLASS_PATH_StarterFragmentXml)
            return (loadClassPath.getDeclaredConstructor().newInstance() as Fragment).apply {
                arguments = bundleOf(KEY_CENTER_MESSAGE_TEXT to centerTextMessage)
            }
        }

        fun newStarterFragmentPure(centerTextMessage: String): Fragment {
            val loadClassPath = parent.getClassLoader().loadClass(CLASS_PATH_StarterFragmentBasic)
            return (loadClassPath.getDeclaredConstructor().newInstance() as Fragment).apply {
                arguments = bundleOf(KEY_CENTER_MESSAGE_TEXT to centerTextMessage)
            }
        }

        fun newJetpackComposeFragmentBasic(centerTextMessage: String): Fragment {
            val loadClassPath = parent.getClassLoader().loadClass(CLASS_PATH_StarterFragmentCompose)
            return (loadClassPath.getDeclaredConstructor().newInstance() as Fragment).apply {
                arguments = bundleOf(KEY_CENTER_MESSAGE_TEXT to centerTextMessage)
            }
        }
    }

    companion object {
        private const val FILE_VALUES = "Values.zip"

        fun properContext(newBase: Context): Context {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                return newBase
            }
            //todo: need caching check
//            val valueFile = loadValueFile(newBase)
            val apkFile = getAssetAsTempFile(newBase, APK_NAME)
            val newResource : Resources? = ApkResourceLoader(newBase,newBase.resources).also{
                if (it.load(apkFile.path,newBase.packageName)) {
                    it
                }
            }
//            RuntimeResource(
//                newBase.resources,
//                RuntimeAssetManager(newBase.filesDir, apkFile, valueFile, "en")
//            )
            return OptionalContextWrapper(
                newBase,
                newResource
            )
        }

        private fun loadValueFile(newBase: Context): File {
            return getAssetAsTempFile(newBase,FILE_VALUES)
        }
    }
}