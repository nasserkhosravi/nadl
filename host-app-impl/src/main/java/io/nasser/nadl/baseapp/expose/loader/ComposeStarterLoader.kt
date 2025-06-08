package io.nasser.nadl.baseapp.expose.loader

import android.os.Bundle
import androidx.compose.runtime.Composable
import io.nasser.mylib.api.plugin.ComposeStarter

class ComposeStarterLoader(
    private val loadClassPath: Class<*>,
) : ComposeStarter {

    private val classPath by lazy { loadClassPath.getDeclaredConstructor().newInstance() }

    override fun getStarter(bundle: Bundle?): @Composable () -> Unit {
        val methodGetStarter = loadClassPath.getMethod("getStarter", Bundle::class.java)
        //it works properly
        @Suppress("UNCHECKED_CAST")
        return methodGetStarter.invoke(classPath, bundle) as (@Composable () -> Unit)
    }

}