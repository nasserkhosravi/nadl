package io.nasser.nadl.baseapp.expose

import android.content.Context
import android.util.Log
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.DataInputStream
import java.io.File
import java.net.URL
import java.net.URLConnection
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * Get dependency accessor
 */
internal fun Context.getDA(): App {
    return applicationContext as App
}

internal fun Context.getFeaturePlugin(): DynamicFeaturePlugin = getDA().featurePlugin

fun observableDownloadAndWriteFile(url: String, targetWriteFile: File): Observable<File> {
    return Observable.just(url).map {
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
        targetWriteFile.writeBytes(buffer)
        Log.d("xosro", "Success of download to buffer")
        targetWriteFile
    }.subscribeOn(Schedulers.io())
}

data class ZipIO(val entry: ZipEntry, val output: File)

fun File.unzip(unzipLocationRoot: File? = null) {

    val rootFolder = unzipLocationRoot ?: File(parentFile.absolutePath + File.separator + nameWithoutExtension)
    if (!rootFolder.exists()) {
        rootFolder.mkdirs()
    }

    ZipFile(this).use { zip ->
        zip
            .entries()
            .asSequence()
            .map {
                val outputFile = File(rootFolder.absolutePath + File.separator + it.name)
                ZipIO(it, outputFile)
            }
            .map {
                it.output.parentFile?.run{
                    if (!exists()) mkdirs()
                }
                it
            }
            .filter { !it.entry.isDirectory }
            .forEach { (entry, output) ->
                zip.getInputStream(entry).use { input ->
                    output.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
    }

}