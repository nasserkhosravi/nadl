package io.nasser.nadl.baseapp.expose.sdk.res

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import androidx.annotation.RequiresApi
import java.lang.reflect.Method

//TODO: separate resource loading of plugins from host.
//host should load
class ApkResourceLoader(
    private val context: Context,
    res: Resources
) : Resources(res.assets, res.displayMetrics, res.configuration) {

    private var pluginResources: Resources? = null
    private var pluginPackage: String? = null

    // Load APK resources from the given path and package name
    fun load(apkPath: String, packageName: String): Boolean {
        return try {
            val assetManager = AssetManager::class.java.newInstance()
            val addAssetPath: Method = AssetManager::class.java.getMethod("addAssetPath", String::class.java)
            addAssetPath.invoke(assetManager, apkPath)

            val baseResources = context.resources
            pluginResources = Resources(
                assetManager,
                baseResources.displayMetrics,
                baseResources.configuration
            )
            pluginPackage = packageName
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun getColor(id: Int): Int {
        return pluginResources?.getColor(id) ?: super.getColor(id)
    }

    override fun getColor(id: Int, theme: Theme?): Int {
        return pluginResources?.getColor(id, theme) ?: super.getColor(id, theme)
    }

    override fun getString(id: Int): String {
        return pluginResources?.getString(id) ?: super.getString(id)
    }

    override fun getString(id: Int, vararg formatArgs: Any?): String {
        return pluginResources?.getString(id, formatArgs) ?: super.getString(id, *formatArgs)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getFont(id: Int): Typeface {
        return pluginResources?.getFont(id) ?: super.getFont(id)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun getDrawable(id: Int, theme: Theme?): Drawable {
        return pluginResources?.getDrawable(id, theme) ?: super.getDrawable(id, theme)
    }

    override fun getDrawable(id: Int): Drawable {
        return pluginResources?.getDrawable(id) ?: super.getDrawable(id)
    }

    override fun getDrawableForDensity(id: Int, density: Int, theme: Theme?): Drawable? {
        return pluginResources?.getDrawableForDensity(id, density, theme) ?: super.getDrawableForDensity(id, density, theme)
    }

    override fun getDrawableForDensity(id: Int, density: Int): Drawable? {
        return pluginResources?.getDrawableForDensity(id, density) ?: super.getDrawableForDensity(id, density)
    }

    override fun getLayout(id: Int): XmlResourceParser {
        return try {
            //TODO: does not work
            val enName = pluginResources?.getResourceEntryName(id)
            if (enName.isNullOrEmpty()) {
                super.getLayout(id)
            }else {
                pluginResources?.getLayout(id)!!
            }
        }catch (e:Exception){
            super.getLayout(id)
        }
//        val name = pluginResources?.getLayout(id) ?: return super.getLayout(id)
//        return loadLayoutXmlParser(name)!!
    }

    override fun getResourceEntryName(resid: Int): String {
        return pluginResources?.getResourceEntryName(resid) ?: super.getResourceEntryName(resid)
    }

    override fun getIdentifier(name: String?, defType: String?, defPackage: String?): Int {
        return pluginResources?.getIdentifier(name, defType, defPackage) ?: super.getIdentifier(name, defType, defPackage)
    }

    override fun getXml(id: Int): XmlResourceParser {
        return pluginResources?.getXml(id) ?: super.getXml(id)
    }

    override fun getValue(id: Int, outValue: TypedValue?, resolveRefs: Boolean) {
        pluginResources?.getValue(id, outValue, resolveRefs)
        super.getValue(id, outValue, resolveRefs)
    }

    override fun getValue(name: String?, outValue: TypedValue?, resolveRefs: Boolean) {
        pluginResources?.getValue(name, outValue, resolveRefs)
        super.getValue(name, outValue, resolveRefs)
    }

    override fun getValueForDensity(id: Int, density: Int, outValue: TypedValue?, resolveRefs: Boolean) {
        //TODO: check it
        pluginResources?.getValueForDensity(id, density, outValue, resolveRefs)
        super.getValueForDensity(id, density, outValue, resolveRefs)
    }

    override fun getDimension(id: Int): Float {
        return pluginResources?.getDimension(id) ?: super.getDimension(id)
    }

    fun loadLayoutXmlParser(layoutName: String): XmlResourceParser? {
        try {
            val res = pluginResources ?: return null
            val am = res.assets
            val layoutId = res.getIdentifier(layoutName, "layout", pluginPackage)

            if (layoutId == 0) {
                throw Resources.NotFoundException("Layout $layoutName not found in package $pluginPackage")
            }

            // Get the file name (e.g. "res/layout/my_layout.xml")
            val typedValue = TypedValue()
            res.getValue(layoutId, typedValue, true)

            if (typedValue.string == null) {
                throw Resources.NotFoundException("Could not resolve path for layout: $layoutName")
            }

            val filePath = typedValue.string.toString()
            return am.openXmlResourceParser(filePath)

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

}