package io.nasser.nadlsample.content

import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Resources

class OptionalContextWrapper(
    private val base: Context,
    private val resourceDelegate: Resources? = null
) : ContextWrapper(base) {

    override fun getAssets(): AssetManager {
        //todo: need support, we don't know how to load asset directory
        return super.getAssets()
    }

    override fun getResources(): Resources = resourceDelegate ?: base.resources

}
