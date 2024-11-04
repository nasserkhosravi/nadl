package io.nasser.nadl.baseapp.expose.content.res

import android.content.res.Resources
import android.content.res.TypedArray
import android.content.res.XmlResourceParser
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import java.io.InputStream

class RuntimeResource(
    res: Resources,
    val dlAsset: RuntimeAssetManager?,
) : Resources(res.assets, res.displayMetrics, res.configuration) {

    override fun getString(id: Int): String {
        return dlAsset?.getString(id) ?: super.getString(id)
    }

    override fun getString(id: Int, vararg formatArgs: Any?): String {
        return super.getString(id, *formatArgs)
    }

    override fun getResourceEntryName(resid: Int): String {
        return dlAsset?.getResourceEntryName(resid) ?: super.getResourceEntryName(resid)
    }

    override fun getColor(id: Int, theme: Theme?): Int {
        // process theme in dlAsset
        return dlAsset?.getColor(id) ?: super.getColor(id, theme)
    }

    override fun getDimension(id: Int): Float {
//        final TypedValue value = obtainTempTypedValue();
//        try {
//            final ResourcesImpl impl = mResourcesImpl;
//            impl.getValue(id, value, true);
//            if (value.type == TypedValue.TYPE_DIMENSION) {
//                return TypedValue.complexToDimension(value.data, impl.getDisplayMetrics());
//            }
//            throw new NotFoundException("Resource ID #0x" + Integer.toHexString(id)
//                    + " type #0x" + Integer.toHexString(value.type) + " is not valid");
//        } finally {
//            releaseTempTypedValue(value);
//        }
        return dlAsset?.getDimen(id) ?: super.getDimension(id)
    }


    override fun getFont(id: Int): Typeface {
        //ResourcesCompat.getFont used resource.getValue
        return dlAsset?.getFont(id) ?: super.getFont(id)
    }

    override fun getXml(id: Int): XmlResourceParser {
        //use mResourcesImpl.getValue and mResourcesImpl.loadXmlResourceParser
        return super.getXml(id)
    }

    override fun getAnimation(id: Int): XmlResourceParser {
        //mResourcesImpl.getValue and mResourcesImpl.loadXmlResourceParser
        return super.getAnimation(id)
    }

    override fun getLayout(id: Int): XmlResourceParser {
        //need mResourcesImpl.loadXmlResourceParser
        return super.getLayout(id)
    }

    override fun getDrawable(id: Int, theme: Theme?): Drawable {
        //need mResourcesImpl.loadDrawable, its provide a xml-parser and finally parse it by DrawableInflater.inflateFromTag
        //it seems good to use reflection
        return super.getDrawable(id, theme)
    }

    override fun obtainAttributes(set: AttributeSet?, attrs: IntArray?): TypedArray {
        //used mResourcesImpl.getAssets().retrieveAttributes.
        return super.obtainAttributes(set, attrs)
    }

    override fun getValue(id: Int, outValue: TypedValue?, resolveRefs: Boolean) {
        val handledObject = dlAsset?.getValue(id, outValue, resolveRefs)
        handledObject ?: super.getValue(id, outValue, resolveRefs)
    }


    override fun openRawResource(id: Int): InputStream {
        return super.openRawResource(id)
    }

    override fun openRawResource(id: Int, value: TypedValue?): InputStream {
        return super.openRawResource(id, value)
    }

    override fun getInteger(id: Int): Int {
        return super.getInteger(id)
    }

    override fun getBoolean(id: Int): Boolean {
        return super.getBoolean(id)
    }
}