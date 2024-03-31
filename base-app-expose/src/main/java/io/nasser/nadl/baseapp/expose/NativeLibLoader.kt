package io.nasser.nadl.baseapp.expose

import android.annotation.SuppressLint
import android.content.Context
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File

class NativeLibLoader(
    private val appContext: Context,
) {

    private var isNativeLibLoaded = false

    @SuppressLint("CheckResult")
    fun loadNativeLibFromAsset(): Observable<File> {
        val fileSo = File(appContext.filesDir, NATIVE_LIB_NAME)
        return rxPrepareFile(fileSo)
            .rxLoadLib()
            .subscribeOn(Schedulers.io())
    }

    @SuppressLint("CheckResult")
    fun loadNativeLibFromHttp(): Observable<File> {
        val fileSo = File(appContext.filesDir, NATIVE_LIB_NAME)
        return observableDownloadAndWriteFile("https://tmpfiles.org/4581069/libndksample.txt", fileSo)
            .rxLoadLib()
            .subscribeOn(Schedulers.io())
    }

    private fun rxPrepareFile(fileSo: File): Observable<File> {
        return Observable.just(fileSo).map {
            if (!it.exists()) {
                writeNativeLibFileFromAsset(fileSo)
            }
            it
        }
    }

    private fun Observable<File>.rxLoadLib(): Observable<File> {
        return map {
            if (!isNativeLibLoaded) {
                isNativeLibLoaded = true
                System.load(it.absolutePath)
            }
            it
        }
    }

    private fun writeNativeLibFileFromAsset(fileSo: File) {
        val assetLibFile = appContext.assets.open("arm64-v8a/${NATIVE_LIB_NAME}")
        val bytes = assetLibFile.readBytes()
        fileSo.writeBytes(bytes)
        assetLibFile.close()
    }


    companion object {
        private const val NATIVE_LIB_NAME = "libndksample.so"

    }
}