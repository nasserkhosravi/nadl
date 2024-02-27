package io.nasser.nadlsample

import android.app.Application

class App : Application() {

    internal lateinit var featurePlugin: AppFeatureFacade

    override fun onCreate() {
        super.onCreate()
        featurePlugin = AppFeatureFacade(this.applicationContext)
    }


}