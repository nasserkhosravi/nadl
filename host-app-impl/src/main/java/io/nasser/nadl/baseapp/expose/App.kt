package io.nasser.nadl.baseapp.expose

import android.app.Application
import io.nasser.nadl.baseapp.expose.loader.ClassFactory
import io.nasser.nadl.baseapp.expose.sdk.DynamicFeaturePlugin

class App : Application() {

    internal lateinit var featurePlugin: DynamicFeaturePlugin
    internal lateinit var classFactory: ClassFactory

    override fun onCreate() {
        super.onCreate()
        val appContext = this.applicationContext
        featurePlugin = DynamicFeaturePlugin(appContext)
        classFactory = ClassFactory(featurePlugin)
    }


}