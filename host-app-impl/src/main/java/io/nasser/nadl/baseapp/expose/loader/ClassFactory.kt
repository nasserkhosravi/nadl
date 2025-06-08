package io.nasser.nadl.baseapp.expose.loader

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import io.nasser.mylib.api.plugin.HelloWorld
import io.nasser.nadl.baseapp.expose.sdk.DynamicFeaturePlugin

class ClassFactory(private val parent: DynamicFeaturePlugin) {

    companion object {
        //TODO: move these to its api module
        private const val LIB_PATH = "io.nasser.mylib.impl"
        private const val CLASS_PATH_HelloWorld = "$LIB_PATH.HelloWorldImpl"
        private const val CLASS_PATH_StarterFragmentXml = "$LIB_PATH.StarterFragmentXml"
        private const val CLASS_PATH_StarterFragmentBasic = "$LIB_PATH.StarterFragmentBasic"
        private const val CLASS_PATH_StarterFragmentCompose = "io.nasser.compose.ui.ComposeStarterImpl"

        const val KEY_CENTER_MESSAGE_TEXT = "centerTextMessage"
    }

    fun newHelloWorld(): HelloWorld {
        val dexClassLoader = parent.getClassLoader()
        return HelloWorldLoader(dexClassLoader.loadClass(CLASS_PATH_HelloWorld))
    }

    fun newStarterFragmentXml(centerTextMessage: String): Fragment {
        val loadClassPath = parent.getClassLoader().loadClass(CLASS_PATH_StarterFragmentXml)
        return (loadClassPath.getDeclaredConstructor().newInstance() as Fragment).apply {
            arguments = bundleOf(KEY_CENTER_MESSAGE_TEXT to centerTextMessage)
        }
    }

    fun newStarterFragmentPure(centerTextMessage: String): Fragment {
        val loadClassPath = parent.getClassLoader().loadClass(CLASS_PATH_StarterFragmentBasic)
        return (loadClassPath.getDeclaredConstructor().newInstance() as Fragment).apply {
            arguments = bundleOf(KEY_CENTER_MESSAGE_TEXT to centerTextMessage)
        }
    }

    fun newJetComposeStarter(): ComposeStarterLoader {
        val clazz = parent.getClassLoader().loadClass(CLASS_PATH_StarterFragmentCompose)
        val starter = ComposeStarterLoader(clazz)
        return starter
    }
}