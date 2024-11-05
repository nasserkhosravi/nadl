package io.nasser.nadl.baseapp.expose

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import io.nasser.baseapp.expose.R
import io.nasser.mylib.api.HostBridgeApi
import io.nasser.nadl.baseapp.expose.content.res.RuntimeResource
import java.lang.IllegalArgumentException

class DynamicHostActivity : AppCompatActivity(),HostBridgeApi {
    private lateinit var myContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic_host)

        val featurePlugin = getFeaturePlugin()
        featurePlugin.loadEverything(this)
        val techMode = intent.getStringExtra(TECH_MODE) ?: MODE_OLD

        val centerTextMessage = "Message from host"
        val starterFragment : Fragment = when(techMode){
            MODE_JETPACK_COMPOSE->{
                featurePlugin.classFactory.newStarterFragmentBasic(centerTextMessage)
            }

            MODE_OLD->{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    featurePlugin.classFactory.newStarterFragmentXml(centerTextMessage)
                } else {
                    featurePlugin.classFactory.newStarterFragmentBasic(centerTextMessage)
                }
            }

            else-> throw IllegalArgumentException()
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

    companion object{
        const val TECH_MODE = "tech_mode"
        const val MODE_JETPACK_COMPOSE = "mode_jetpack_compose"
        const val MODE_OLD = "mode_programmer"
    }
}