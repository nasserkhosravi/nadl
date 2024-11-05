package io.nasser.mylib.api

import android.graphics.Typeface

interface HostBridgeApi {

    fun getSupportFont(id: Int) : Typeface
}