package io.nasser.mylib.impl

import android.graphics.Typeface
import android.os.Build
import androidx.annotation.FontRes
import androidx.fragment.app.Fragment
import io.nasser.mylib.api.host.HostFont
import io.nasser.mylib.api.host.HostBridgeApi

internal fun Fragment.getFontFromHost(@FontRes id: Int): Typeface {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        requireContext().resources.getFont(id)
    } else {
        (requireContext() as HostBridgeApi).getSupportFont(id)
    }
}

internal fun Fragment.getFontFromHost(id: HostFont): Typeface {
    return (requireContext() as HostBridgeApi).getFont(id)
}