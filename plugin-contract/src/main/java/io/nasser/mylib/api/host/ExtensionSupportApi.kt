package io.nasser.mylib.api.host

import android.content.Context
import android.graphics.Typeface

interface HostBridgeApi {

    fun getSupportFont(fontResId: Int): Typeface
    fun getFont(id: HostFont): Typeface

    fun handleDeeplink(context: Context, deeplink: String)
}

object HostDeeplinkValues {
    fun finishActivity() = "app://nadl.net/?action=finishActivity"
}