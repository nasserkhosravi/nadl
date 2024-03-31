package io.nasser.nadl.baseapp.expose

import android.app.Application

class App : Application() {

    internal lateinit var featurePlugin: DynamicFeaturePlugin

    override fun onCreate() {
        super.onCreate()
        val appContext = this.applicationContext
        featurePlugin = DynamicFeaturePlugin(appContext, NativeLibLoader(appContext))
    }


}