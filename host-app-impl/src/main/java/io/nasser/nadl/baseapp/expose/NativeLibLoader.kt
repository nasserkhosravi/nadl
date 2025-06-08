package io.nasser.nadl.baseapp.expose
//
//import android.annotation.SuppressLint
//import android.content.Context
//import io.reactivex.rxjava3.core.Observable
//import io.reactivex.rxjava3.core.Scheduler
//import io.reactivex.rxjava3.schedulers.Schedulers
//import java.io.File
//
////does not tested
//class NativeLibLoader(
//    private val appContext: Context,
//) {
//
//    private var isNativeLibLoaded = false
//
//    @SuppressLint("CheckResult")
//    fun loadNativeLibFromAsset(): Observable<File> {
//        val fileSo = File(appContext.filesDir, NATIVE_LIB_NAME)
//        return rxPrepareFile(fileSo)
//            .rxLoadLib()
//            .subscribeOn(Schedulers.io())
//    }
//
//    @SuppressLint("CheckResult")
//    fun loadNativeLibFromHttp(): Observable<File> {
//        val fileSo = File(appContext.filesDir, NATIVE_LIB_NAME)
//        return if (!isNativeLibLoaded){
//            if (fileSo.exists()){
//                Observable.just(fileSo)
//                    .rxLoadLib()
//                    .subscribeOn(Schedulers.io())
//            }else {
//                observableDownloadAndWriteFile(NATIVE_LIB_URL, fileSo)
//                    .rxLoadLib()
//                    .subscribeOn(Schedulers.io())
//            }
//        }else {
//            Observable.just(fileSo)
//        }
//    }
//
//    private fun rxPrepareFile(fileSo: File): Observable<File> {
//        return Observable.just(fileSo).map {
//            if (!it.exists()) {
//                writeNativeLibFileFromAsset(fileSo)
//            }
//            it
//        }
//    }
//
//    private fun Observable<File>.rxLoadLib(): Observable<File> {
//        return map {
//            if (!isNativeLibLoaded) {
//                isNativeLibLoaded = true
//                System.load(it.absolutePath)
//            }
//            it
//        }
//    }
//
//    private fun writeNativeLibFileFromAsset(fileSo: File) {
//        val assetLibFile = appContext.assets.open("arm64-v8a/${NATIVE_LIB_NAME}")
//        val bytes = assetLibFile.readBytes()
//        fileSo.writeBytes(bytes)
//        assetLibFile.close()
//    }
//
//
//    companion object {
////        private const val NATIVE_LIB_URL = "https://android-apk-minio-android-storage-prod.apps.private.okd4.teh-1.snappcloud.io/upload/nasserkhosravi/libndksample.so?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=NCB4VC1AHO6BWJ4X3933%2F20250602%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250602T140911Z&X-Amz-Expires=604800&X-Amz-Security-Token=eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJhY2Nlc3NLZXkiOiJOQ0I0VkMxQUhPNkJXSjRYMzkzMyIsImV4cCI6MTc0ODg3NjkyNiwicGFyZW50IjoiZ3Vlc3QifQ.NfBfJgokbPOUt_4cH8CGJ6vhnTzaJkWHy5JrkAGhASpGRM5TqXEGs7j3TnMwisJbfd6JxJ27svxxusTlkzujcg&X-Amz-SignedHeaders=host&versionId=null&X-Amz-Signature=4cbd1fb4e1d17e454f42ce0a7acfc8dd5d5395ae15f8e4d622e86aabb9d0e9ea"
//        private const val NATIVE_LIB_URL = "https://android-apk-minio-android-storage-prod.apps.private.okd4.teh-1.snappcloud.io/upload/nasserkhosravi/_libndksample.so?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=NCB4VC1AHO6BWJ4X3933%2F20250602%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250602T142233Z&X-Amz-Expires=604800&X-Amz-Security-Token=eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJhY2Nlc3NLZXkiOiJOQ0I0VkMxQUhPNkJXSjRYMzkzMyIsImV4cCI6MTc0ODg3NjkyNiwicGFyZW50IjoiZ3Vlc3QifQ.NfBfJgokbPOUt_4cH8CGJ6vhnTzaJkWHy5JrkAGhASpGRM5TqXEGs7j3TnMwisJbfd6JxJ27svxxusTlkzujcg&X-Amz-SignedHeaders=host&versionId=null&X-Amz-Signature=a6e218a68b7dedacc1fa3406acbb9478f89f59a1d89b9dbf902f1209ab138d5c"
//        private const val NATIVE_LIB_NAME = "libndksample.so"
//
//    }
//}