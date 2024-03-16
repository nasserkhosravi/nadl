package io.nasser.nadl.baseapp.expose

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.nasser.baseapp.expose.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val featurePlugin = getFeaturePlugin()

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