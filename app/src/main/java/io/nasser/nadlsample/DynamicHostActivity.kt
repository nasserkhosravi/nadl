package io.nasser.nadlsample

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.nasser.nadl.R

class DynamicHostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic_host)

        val featurePlugin = getFeaturePlugin()
        featurePlugin.loadEverything(this)

        val starterFragment = featurePlugin.classFactory.newDynamicStarterFragment("Message from another world")

        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_dynamic_host, starterFragment)
            .commit()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(DynamicFeaturePlugin.properContext(newBase))
    }
}