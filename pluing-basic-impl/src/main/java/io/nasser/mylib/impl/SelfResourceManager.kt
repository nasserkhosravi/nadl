package io.nasser.mylib.impl

import android.view.View
import androidx.core.graphics.toColorInt


object ViewIds {
    val tvText = View.generateViewId()
    val btnCallback = View.generateViewId()
}

object Colors {
    val zzBackColor get() = ColorId("#673AB7")
    val white get() = ColorId("#ffffff")
    val black get() = ColorId("#000000")
}

object StringIds {
    const val libName = 1
}

@JvmInline
value class ColorId(val id: String)

//@JvmInline
//value class FontId(val id:String)

class SelfResourceManager {
    var locale: String = "en"

    private val libName: String
        get() {
            return when (locale) {
                "en" -> "dynamic-module"
                else -> "پویا ماژول"
            }
        }

    fun getString(id: Int): String {
        return when (id) {
            StringIds.libName -> libName
            else -> throw IllegalArgumentException()
        }
    }

    fun getColor(id: ColorId): Int {
        return id.id.toColorInt()
    }
}


