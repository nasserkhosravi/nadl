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
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.security.MessageDigest

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
        targetWriteFile.setReadable(true)
        targetWriteFile
    }.subscribeOn(Schedulers.io())
}


/**
 * Downloads file from given [fileUrl] and caches it internally.
 * If called again with the same URL, returns cached file immediately.
 * If URL changes, downloads new file and caches it.
 */
fun downloadFileWithUrlCachingFlow(
    context: Context,
    fileUrl: String,
): Flow<File> = flow {
    // Create a unique filename based on a hash of the URL
    val filename = hashUrlToFilename(fileUrl)
    val file = File(context.filesDir, filename)

    if (file.exists()) {
        // Cached file exists for this URL - emit immediately
        emit(file)
        return@flow
    }

    // Cached file doesn't exist - download it
    val url = URL(fileUrl)
    val connection = url.openConnection() as HttpURLConnection
    connection.connectTimeout = 15_000
    connection.readTimeout = 15_000
    connection.instanceFollowRedirects = true
    connection.requestMethod = "GET"
    Log.d("Downloader","Connecting")
    connection.connect()
    Log.d("Downloader","Connecting finished")

    if (connection.responseCode != HttpURLConnection.HTTP_OK) {
        throw Exception("Failed to download file from $fileUrl: HTTP ${connection.responseCode}")
    }

    Log.d("Downloader","start to write")
    connection.inputStream.use { input ->
        FileOutputStream(file).use { output ->
            input.copyTo(output)
        }
    }

    Log.d("Downloader","finish writing")
    emit(file)
}.flowOn(Dispatchers.IO)


/** Helper: returns a hex string filename hashing the URL */
private fun hashUrlToFilename(url: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val hashBytes = md.digest(url.toByteArray(Charsets.UTF_8))
    return hashBytes.joinToString("") { "%02x".format(it) }
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

fun getFileNameFromUrl(url: String): String? {
    return try {
        val uri = url.toUri()
        val path = uri.path
        path?.substringAfterLast('/')
    } catch (e: Exception) {
        null
    }
}

@Throws(IOException::class)
fun getAssetAsTempFile(context: Context, assetName: String): File {
    val tempFile = File.createTempFile("asset_", null, context.cacheDir)
    tempFile.deleteOnExit()

    context.assets.open(assetName).use { input ->
        FileOutputStream(tempFile).use { output ->
            input.copyTo(output)
        }
    }
    return tempFile
}
