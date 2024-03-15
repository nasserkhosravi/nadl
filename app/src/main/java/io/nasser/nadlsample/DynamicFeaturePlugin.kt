package io.nasser.nadlsample

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
import dalvik.system.DexClassLoader
import io.nasser.mylib.api.HelloWorld
import io.nasser.nadlsample.content.OptionalContextWrapper
import io.nasser.nadlsample.content.res.RuntimeAssetManager
import io.nasser.nadlsample.content.res.RuntimeResource
import java.io.File


class DynamicFeaturePlugin(
    private val appContext: Context
) {

    private var apkFile: File? = null
    private var dexLoader: DexClassLoader? = null

    private fun getDexClassLoader(): DexClassLoader {
        if (dexLoader == null) {
            if (!isReady()) {
                throw IllegalStateException("The dex file is not available")
            }
            dexLoader = DexClassLoader(apkFile!!.absolutePath, null, null, appContext.classLoader)
        }
        return dexLoader!!
    }

    val classFactory = ClassFactory(this)

    fun isReady(): Boolean = apkFile?.exists() ?: false

    private fun loadDex(context: Context) {
        if (isReady()) return
        loadApkFromAsset(context)
    }

    private fun loadResource(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            overrideResourcesApi30AndMore(context)
        } else {
            //TODO:
        }
    }

    fun loadEverything(context: Context) {
        loadDex(context)
        loadResource(context)
    }

    private fun loadApkFromAsset(context: Context) {
        val readBytes = context.assets.open(APK_NAME).readBytes()
        apkFile = File(context.filesDir, "asset_adl3.apk").also {
            it.writeBytes(readBytes)
        }
        Toast.makeText(context, "Ready", Toast.LENGTH_SHORT).show()
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

    private fun loadFromHttpAndStoreFile(context: Context, url: String) {
        loadFromHttpAndStoreFile(context, url) {
            this.apkFile = it
        }
    }

    class ClassFactory(private val parent: DynamicFeaturePlugin) {

        companion object {
            private const val LIB_PATH = "io.nasser.mylib.impl"
            private const val CLASS_PATH_HelloWorld = "${LIB_PATH}.HelloWorldImpl"
            private const val CLASS_PATH_DynamicStarterFragment = "${LIB_PATH}.DynamicStarterFragment"
        }

        fun newHelloWorld(): HelloWorld {
            val dexClassLoader = parent.getDexClassLoader()
            return HelloWorldLoader(dexClassLoader.loadClass(CLASS_PATH_HelloWorld))
        }

        fun newDynamicStarterFragment(centerTextMessage: String): Fragment {
            val loadClassPath = parent.getDexClassLoader().loadClass(CLASS_PATH_DynamicStarterFragment)
            return (loadClassPath.getDeclaredConstructor().newInstance() as Fragment).apply {
                arguments = bundleOf("centerTextMessage" to centerTextMessage)
            }
        }
    }

    companion object {
        private const val APK_NAME = "AppExporter-debug.apk"
        private const val RESOURCE_TABLE_FILE_NAME = "rTable.txt"

        fun properContext(newBase: Context): Context {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                return newBase
            }
            val readBytes = newBase.assets.open(RESOURCE_TABLE_FILE_NAME).readBytes()
            val rtFile = File(newBase.filesDir, "asset_rTable.txt").also {
                it.writeBytes(readBytes)
            }

            return OptionalContextWrapper(
                newBase,
                RuntimeResource(newBase.resources, RuntimeAssetManager(rtFile))
            )
        }
    }
}