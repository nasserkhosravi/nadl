package io.nasser.nadlsample.content.res

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
    private val dlAsset: RuntimeAssetManager?,
) : Resources(res.assets, res.displayMetrics, res.configuration) {

    override fun getString(id: Int): String {
        return dlAsset?.getString(id) ?: super.getString(id)
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
        return super.getDimension(id)
    }


    override fun getFont(id: Int): Typeface {
        //need loadXmlResourceParser, maybe we can directly load them by Typeface.createFromFile()
        //require for jetpack compose
        return super.getFont(id)
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
        //use mResourcesImpl.getAssets().retrieveAttributes.
        return super.obtainAttributes(set, attrs)
    }

    override fun getValue(id: Int, outValue: TypedValue?, resolveRefs: Boolean) {
        super.getValue(id, outValue, resolveRefs)
        //need work
    }

    override fun openRawResource(id: Int): InputStream {
        return super.openRawResource(id)
    }
}