package io.nasser.nadlsample

import android.content.Context
import android.content.res.Resources
import android.content.res.loader.ResourcesLoader
import android.content.res.loader.ResourcesProvider
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.ParcelFileDescriptor
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import dalvik.system.DexClassLoader
import io.nasser.mylib.api.HelloWorld
import java.io.DataInputStream
import java.io.File
import java.net.URL
import java.net.URLConnection


internal class AppFeatureFacade(
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

    fun loadDex(context: Context) {
        if (isReady()) return
        loadCodesFromAsset(context)
    }

    fun loadResource(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            overrideResourcesApi30(context)
        }
    }

    private fun loadCodesFromAsset(context: Context) {
        val readBytes = context.assets.open("AppExporter-debug.apk").readBytes()
        apkFile = File(context.filesDir, "asset_adl.apk").also {
            it.writeBytes(readBytes)
        }
        Toast.makeText(context, "Ready", Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun overrideResourcesApi30(context: Context) {
        val loader = ResourcesLoader().also {
            val apkFd = ParcelFileDescriptor.open(apkFile, ParcelFileDescriptor.MODE_READ_ONLY)
            val provider = ResourcesProvider.loadFromApk(apkFd)
            it.addProvider(provider)
        }
        Resources.getSystem().addLoaders(loader)
        context.resources.addLoaders(loader)
    }

    private fun loadFromHttpAndStoreFile(context: Context, url: String) {
        val dexFile = File(appContext.cacheDir, "adl.apk")
        val thread = Thread {
            try {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "DL Start", Toast.LENGTH_SHORT).show()
                }
                val u = URL(url)
                val conn: URLConnection = u.openConnection()
                conn.readTimeout = 3000
                conn.connectTimeout = 3000
                Log.d("xosro", "Before content ")
                val contentLength: Int = conn.contentLength
                Log.d("xosro", "Start download")
                val stream = DataInputStream(u.openStream())
                val buffer = ByteArray(contentLength)
                stream.readFully(buffer)
                stream.close()
                Log.d("xosro", "Success of download to buffer")
                this.apkFile = dexFile.also {
                    it.setReadOnly()
                    it.writeBytes(buffer)
                }
                Log.d("xosro", "file wrote")
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "DL Finished", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "DL Failed, Ready", Toast.LENGTH_SHORT).show()
                }
                Log.e("xosro", e.message!!)
            }
        }
        thread.start()
    }

    class ClassFactory(private val parent: AppFeatureFacade) {

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
}