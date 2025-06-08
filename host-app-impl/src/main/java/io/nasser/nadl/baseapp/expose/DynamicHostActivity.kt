package io.nasser.nadl.baseapp.expose

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import io.nasser.baseapp.expose.R
import io.nasser.mylib.api.host.HostFont
import io.nasser.mylib.api.host.HostBridgeApi
import io.nasser.mylib.api.host.HostDeeplinkValues
import io.nasser.nadl.baseapp.expose.Router.KEY_ADDRESS
import io.nasser.nadl.baseapp.expose.Router.VALUE_STARTER_JETPACK_COMPOSE
import io.nasser.nadl.baseapp.expose.Router.VALUE_STARTER_OLD
import io.nasser.nadl.baseapp.expose.Router.VALUE_STARTER_XML
import io.nasser.nadl.baseapp.expose.loader.ClassFactory
import io.nasser.nadl.baseapp.expose.loader.ClassFactory.Companion.KEY_CENTER_MESSAGE_TEXT
import io.nasser.nadl.baseapp.expose.sdk.DynamicFeaturePlugin
import io.nasser.nadl.baseapp.expose.sdk.res.RuntimeResource
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

        val progressView = findViewById<View>(R.id.host_progress_bar)

        val featurePlugin = getFeaturePlugin()
        launch {
            progressView.isVisible = true
            featurePlugin.loadEverythingFlow(this@DynamicHostActivity)
                .catch {
                    progressView.isVisible = false
                    it.printStackTrace()
                    Toast.makeText(this@DynamicHostActivity, "Failed to load", Toast.LENGTH_SHORT).show()
                }
                .collect {
                    progressView.isVisible = false
                    present(getClassFactory())
                }
        }
    }

    private fun present(classFactory: ClassFactory) {
        val starterKey = intent.getStringExtra(KEY_ADDRESS) ?: VALUE_STARTER_XML
        val centerTextMessage = "My Message from host"
        when (starterKey) {

            VALUE_STARTER_OLD -> {
                val starterFragment = classFactory.newStarterFragmentPure(centerTextMessage)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_dynamic_host, starterFragment)
                    .commit()
            }

            VALUE_STARTER_XML -> {
                val starterFragment = classFactory.newStarterFragmentXml(centerTextMessage)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_dynamic_host, starterFragment)
                    .commit()
            }

            VALUE_STARTER_JETPACK_COMPOSE -> {
                val composeView = findViewById<ComposeView>(R.id.compose_host)
                composeView.isVisible = true
                val starter = classFactory.newJetComposeStarter()
                composeView.setContent {
                    val bundle = bundleOf(KEY_CENTER_MESSAGE_TEXT to centerTextMessage)
                    starter.getStarter(bundle).invoke()
                }
            }

            else -> throw IllegalArgumentException()
        }


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

    override fun getSupportFont(fontResId: Int): Typeface {
        return (resources as RuntimeResource).dlAsset?.getSupportedFont(myContext, fontResId)!!
    }

    override fun getFont(id: HostFont): Typeface {
        return when (id) {
            HostFont.InterFamily -> ResourcesCompat.getFont(this, R.font.inter_family)
            HostFont.InterMedium -> ResourcesCompat.getFont(this, R.font.inter_medium)
            HostFont.InterRegular -> ResourcesCompat.getFont(this, R.font.inter_regular)
        }!!
    }

    override fun handleDeeplink(context: Context, deeplink: String) {
        when (deeplink) {
            HostDeeplinkValues.finishActivity() -> {
                finish()
            }
        }
    }

}