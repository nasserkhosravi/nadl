package io.nasser.nadl.baseapp.expose

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import io.nasser.baseapp.expose.databinding.ActivityMainBinding
import io.nasser.nadl.baseapp.expose.Router.VALUE_STARTER_JETPACK_COMPOSE
import io.nasser.nadl.baseapp.expose.Router.VALUE_STARTER_OLD
import io.nasser.nadl.baseapp.expose.Router.VALUE_STARTER_XML
import io.nasser.nadl.baseapp.expose.Router.openDynamicHostActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var binding: ActivityMainBinding

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val featurePlugin = getFeaturePlugin()
        val classFactory = getClassFactory()
//        binding.btnRtNativePresent.setOnClickListener {
//            featurePlugin.nativeLibLoader.loadNativeLibFromHttp()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({
//                    val theJniAccessor = SampleJNIDelegate()
//                    val jniMessage = theJniAccessor.stringFromJNI()
//                    Log.d("xosro", "stringFromJNI: $jniMessage")
//                    Toast.makeText(this, "stringFromJNI: $jniMessage", Toast.LENGTH_LONG).show()
//                }, {
//                    it.printStackTrace()
//                    Toast.makeText(this, "RT Native load failed", Toast.LENGTH_SHORT).show()
//                })
//        }

        binding.btnPrepare.setOnClickListener {
            featurePlugin.loadEverythingFlow(this@MainActivity)
                .onStart {
                    binding.progressBar.isVisible = true
                }
                .onEach {
                    binding.run {
                        btnPrepare.setBackgroundColor(Color.GREEN)
                        progressBar.isVisible = false
                    }
                }.catch {
                    it.printStackTrace()
                    binding.progressBar.isVisible = false
                    Toast.makeText(this@MainActivity, "download or resource load failed", Toast.LENGTH_SHORT).show()
                }.launchIn(this)
        }
        binding.btnPresentText.setOnClickListener {
            if (!featurePlugin.isReady()) {
                Toast.makeText(this@MainActivity, "file does not exist", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val helloWorld = classFactory.newHelloWorld()
            binding.tvText.text = StringBuilder()
                .append("getMessage result: ")
                .append(helloWorld.getMessage())
                .appendLine()
                .append("getUserJson result: ")
                .append(helloWorld.getUserJson("Nasser"))
                .toString()
        }
        binding.btnOpenProgrammingSample.setOnClickListener {
            openDynamicHostActivity(this, VALUE_STARTER_OLD)
        }

        binding.btnOpenXmlSample.setOnClickListener {
            openDynamicHostActivity(this, VALUE_STARTER_XML)
        }

        binding.btnOpenComposeSample.setOnClickListener {
            openDynamicHostActivity(this, VALUE_STARTER_JETPACK_COMPOSE)
        }
    }
}