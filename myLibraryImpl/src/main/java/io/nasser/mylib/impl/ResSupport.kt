package io.nasser.mylib.impl

import android.graphics.Typeface
import android.os.Build
import androidx.fragment.app.Fragment
import io.nasser.mylib.api.ResourceDirectBridge

internal fun Fragment.getFont(id: Int): Typeface {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        requireContext().resources.getFont(id)
    } else {
        (requireContext() as ResourceDirectBridge).getSupportFont(id)
    }
}
