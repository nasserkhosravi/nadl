package io.nasser.nadlsample

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import java.io.DataInputStream
import java.io.File
import java.net.URL
import java.net.URLConnection

/**
 * Get dependency accessor
 */
internal fun Context.getDA(): App {
    return applicationContext as App
}

internal fun Context.getFeaturePlugin(): DynamicFeaturePlugin = getDA().featurePlugin


fun loadFromHttpAndStoreFile(context: Context, url: String, onWriteFileSetFile:(File)->Unit) {
    val dexFile = File(context.cacheDir, "adl.apk")
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
            onWriteFileSetFile(dexFile.also {
                it.setReadOnly()
                it.writeBytes(buffer)
            })
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
