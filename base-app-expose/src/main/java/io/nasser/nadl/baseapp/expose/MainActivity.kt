package io.nasser.nadl.baseapp.expose

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.nasser.baseapp.expose.databinding.ActivityMainBinding
import io.nasser.ndksample.SampleJNIDelegate
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val featurePlugin = getFeaturePlugin()
        binding.btnRtNativePresent.setOnClickListener {
            featurePlugin.nativeLibLoader.loadNativeLibFromAsset()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val theJniAccessor = SampleJNIDelegate()
                    val jniMessage = theJniAccessor.stringFromJNI()
                    Log.d("xosro", "stringFromJNI: $jniMessage")
                    Toast.makeText(this, "stringFromJNI: $jniMessage", Toast.LENGTH_LONG).show()
                }, {
                    it.printStackTrace()
                    Toast.makeText(this, "RT Native load failed", Toast.LENGTH_SHORT).show()
                })
        }

        binding.btnDownload.setOnClickListener {
            featurePlugin.loadEverything(this@MainActivity)
        }
        binding.btnPresentText.setOnClickListener {
            if (!featurePlugin.isReady()) {
                Toast.makeText(this@MainActivity, "file does not exist", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val helloWorld = featurePlugin.classFactory.newHelloWorld()
            binding.tvText.text = StringBuilder()
                .append("getMessage result: ")
                .append(helloWorld.getMessage())
                .appendLine()
                .append("getUserJson result: ")
                .append(helloWorld.getUserJson("Nasser"))
                .toString()
        }

        binding.btnOpenActivity.setOnClickListener {
            startActivity(Intent(this@MainActivity, DynamicHostActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
    }
}