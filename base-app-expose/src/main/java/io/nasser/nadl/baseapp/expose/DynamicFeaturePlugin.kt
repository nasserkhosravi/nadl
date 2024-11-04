package io.nasser.nadl.baseapp.expose

import android.annotation.SuppressLint
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
import io.nasser.nadl.baseapp.expose.content.OptionalContextWrapper
import io.nasser.nadl.baseapp.expose.content.res.RuntimeResource
import io.nasser.nadl.baseapp.expose.content.res.RuntimeAssetManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
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
        apkFile = File(context.cacheDir, System.currentTimeMillis().toString().plus("asset_adl3.apk")).also {
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

    @SuppressLint("CheckResult")
    private fun loadFromHttpAndStoreFile(context: Context, url: String) {
        val cacheApkFile = File(context.cacheDir, "adl.apk")
        observableDownloadAndWriteFile(url, cacheApkFile)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                apkFile = it
                Toast.makeText(appContext, "download completed", Toast.LENGTH_SHORT).show()
            }, {
                Toast.makeText(appContext, "download failed", Toast.LENGTH_SHORT).show()
            })
    }


    class ClassFactory(private val parent: DynamicFeaturePlugin) {

        companion object {
            //TODO: move these to its api module
            private const val LIB_PATH = "io.nasser.mylib.impl"
            private const val CLASS_PATH_HelloWorld = "$LIB_PATH.HelloWorldImpl"
            private const val CLASS_PATH_StarterFragmentXml = "$LIB_PATH.StarterFragmentXml"
            private const val CLASS_PATH_StarterFragmentBasic = "$LIB_PATH.StarterFragmentBasic"
        }

        fun newHelloWorld(): HelloWorld {
            val dexClassLoader = parent.getClassLoader()
            return HelloWorldLoader(dexClassLoader.loadClass(CLASS_PATH_HelloWorld))
        }

        fun newStarterFragmentXml(centerTextMessage: String): Fragment {
            val loadClassPath = parent.getClassLoader().loadClass(CLASS_PATH_StarterFragmentXml)
            return (loadClassPath.getDeclaredConstructor().newInstance() as Fragment).apply {
                arguments = bundleOf("centerTextMessage" to centerTextMessage)
            }
        }

        fun newStarterFragmentBasic(centerTextMessage: String): Fragment {
            val loadClassPath = parent.getClassLoader().loadClass(CLASS_PATH_StarterFragmentBasic)
            return (loadClassPath.getDeclaredConstructor().newInstance() as Fragment).apply {
                arguments = bundleOf("centerTextMessage" to centerTextMessage)
            }
        }
    }

    companion object {
        private const val APK_NAME = "dynamic-lib-apk-exporter-debug.apk"
        private const val FILE_VALUES = "Values.zip"

        fun properContext(newBase: Context): Context {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                return newBase
            }
            //todo: need caching check
            val valueFile = loadValueFile(newBase)
            val apkFile = loadApkFromAsset(newBase)

            return OptionalContextWrapper(
                newBase,
                RuntimeResource(
                    newBase.resources,
                    RuntimeAssetManager(newBase.filesDir, apkFile, valueFile, "en")
                )
            )
        }

        private fun loadValueFile(newBase: Context): File {
            val readBytes = newBase.assets.open(FILE_VALUES).readBytes()
            val rtFile = File(newBase.filesDir, "Values.zip").also {
                it.writeBytes(readBytes)
            }
            return rtFile
        }

        private fun loadApkFromAsset(originalContext: Context): File {
            val readBytes = originalContext.assets.open(APK_NAME).readBytes()
            return File(originalContext.cacheDir, "asset_adl3.apk").also {
                it.writeBytes(readBytes)
            }
        }
    }
}