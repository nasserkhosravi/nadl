package io.nasser.mylib.api

import android.graphics.Typeface

interface ResourceDirectBridge {

    fun getSupportFont(id: Int) : Typeface
}