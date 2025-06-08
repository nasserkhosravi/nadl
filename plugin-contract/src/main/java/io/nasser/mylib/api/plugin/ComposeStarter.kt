package io.nasser.mylib.api.plugin

import android.os.Bundle
import androidx.compose.runtime.Composable

interface ComposeStarter {

    fun getStarter(bundle: Bundle?): @Composable () -> Unit

}