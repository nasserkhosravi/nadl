package io.nasser.nadl.baseapp.expose

import android.content.Context
import android.content.Intent

object Router {

    private const val COMMON_PATH = "app://nadl.net"
    const val VALUE_STARTER_JETPACK_COMPOSE = "$COMMON_PATH/compose"
    const val VALUE_STARTER_XML = "$COMMON_PATH/xml"
    const val VALUE_STARTER_OLD = "$COMMON_PATH/old"
    const val KEY_ADDRESS = "address"

    fun openDynamicHostActivity(context: Context, address: String) {
        when (address) {
            VALUE_STARTER_XML, VALUE_STARTER_OLD,VALUE_STARTER_JETPACK_COMPOSE -> {
                val newIntent = Intent(context, DynamicHostActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra(KEY_ADDRESS, address)
                }
                context.startActivity(newIntent)
            }
        }

    }
}