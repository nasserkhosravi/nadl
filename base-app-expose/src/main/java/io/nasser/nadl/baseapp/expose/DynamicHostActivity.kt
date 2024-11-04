package io.nasser.nadl.baseapp.expose

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.nasser.baseapp.expose.R
import io.nasser.mylib.api.ResourceDirectBridge
import io.nasser.nadl.baseapp.expose.content.res.RuntimeResource

class DynamicHostActivity : AppCompatActivity(),ResourceDirectBridge {
    private lateinit var myContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic_host)

        val featurePlugin = getFeaturePlugin()
        featurePlugin.loadEverything(this)

        val starterFragment = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            featurePlugin.classFactory.newStarterFragmentXml("Message from another world")
        } else {
            featurePlugin.classFactory.newStarterFragmentBasic("Message from another world")
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_dynamic_host, starterFragment)
            .commit()
    }

    override fun attachBaseContext(newBase: Context) {
        //TODO: check if context previously initialized
        if (::myContext.isInitialized) {
            return
        }
        val properContext = DynamicFeaturePlugin.properContext(newBase)
        myContext = properContext
        super.attachBaseContext(properContext)
    }

    override fun getBaseContext(): Context {
        return myContext
    }

    override fun getResources(): Resources {
        return myContext.resources
    }

    override fun getSupportFont(id: Int): Typeface {
        return (resources as RuntimeResource).dlAsset?.getSupportedFont(myContext,id)!!
    }
}