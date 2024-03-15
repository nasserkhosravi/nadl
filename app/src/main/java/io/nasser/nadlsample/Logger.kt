package io.nasser.nadlsample

import android.util.Log

private const val GLOBAL_TAG_NAME = "xosro"

internal fun Any.nevisD(message: String) {
    val fMessage = this::class.simpleName.plus(": $message")
    Log.d(GLOBAL_TAG_NAME, fMessage)
}