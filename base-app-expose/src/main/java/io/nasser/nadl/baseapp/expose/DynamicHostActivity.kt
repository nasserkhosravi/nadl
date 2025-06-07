package io.nasser.nadl.baseapp.expose

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import io.nasser.baseapp.expose.R
import io.nasser.mylib.api.HostBridgeApi
import io.nasser.nadl.baseapp.expose.content.res.RuntimeResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class DynamicHostActivity : AppCompatActivity(), CoroutineScope by MainScope(), HostBridgeApi {
    private lateinit var myContext: Context

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic_host)

        val featurePlugin = getFeaturePlugin()
        launch {
            featurePlugin.loadEverythingFlow(this@DynamicHostActivity)
                .catch {
                    it.printStackTrace()
                    Toast.makeText(this@DynamicHostActivity, "Failed to load", Toast.LENGTH_SHORT).show()
                }
                .collect {
                    present(featurePlugin)
                }
        }
    }

    private fun present(featurePlugin: DynamicFeaturePlugin) {
        val starterKey = intent.getStringExtra(KEY_DEEPLINK) ?: VALUE_STARTER_XML

        val centerTextMessage = "My Message from host"
        val starterFragment: Fragment = when (starterKey) {
            VALUE_STARTER_JETPACK_COMPOSE -> {
                featurePlugin.classFactory.newJetpackComposeFragmentBasic(centerTextMessage)
            }

            VALUE_STARTER_OLD -> {
                featurePlugin.classFactory.newStarterFragmentPure(centerTextMessage)
            }

            VALUE_STARTER_XML -> {
                featurePlugin.classFactory.newStarterFragmentXml(centerTextMessage)
            }

            else -> throw IllegalArgumentException()
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
        return (resources as RuntimeResource).dlAsset?.getSupportedFont(myContext, id)!!
    }

    companion object {
        const val KEY_DEEPLINK = "path"
        private const val COMMON_PATH = "app://nadl.net"
        const val VALUE_STARTER_JETPACK_COMPOSE = "$COMMON_PATH/compose"
        const val VALUE_STARTER_XML = "$COMMON_PATH/xml"
        const val VALUE_STARTER_OLD = "$COMMON_PATH/old"
    }
}