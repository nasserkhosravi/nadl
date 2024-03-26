package io.nasser.nadl.baseapp.expose.content.res

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.util.TypedValue
import androidx.core.content.res.ResourcesCompat
import io.nasser.nadl.baseapp.expose.content.res.idmap.ValueRes
import io.nasser.nadl.baseapp.expose.nevisD
import io.nasser.nadl.baseapp.expose.unzip
import org.w3c.dom.Node
import java.io.File
import java.io.FileFilter
import java.lang.IllegalArgumentException
import javax.xml.parsers.DocumentBuilderFactory

//maybe its not bad idea to move parser to library client
//its better to read all from apkFile but currently we cant read resources.arsc
class RuntimeAssetManager(

    internalFileDir: File,
    val apkFile: File,
    valuesFileMyBundleZip: File,
    localId: String,
) {

    private val resIdTableHolder = HashMap<Int, ValueRes>()
    private val valuesValueLookup = HashMap<Int, String>()//id,value
    private var mLocalId: String = localId

    private val unzipApk by lazy {
        val unzipLocation = File(internalFileDir, "unzipApk")
        apkFile.unzip(unzipLocation)
        unzipLocation
    }

    init {
        initialLoad(internalFileDir, valuesFileMyBundleZip)
    }

    //we iterate and load variant resources based on app resource configuration.
    private fun initialLoad(internalFileDir: File, valuesFileMyBundleZip: File) {
        nevisD("iterateDumbResTable")
        val unzipLocation = File(internalFileDir, "unzipResValues")
        valuesFileMyBundleZip.unzip(unzipLocation)
        val publicResFile = File(unzipLocation, RESOURCE_TABLE_FILE_NAME)
        loadResTable(publicResFile)
        unzipLocation.list() ?: return // just check if is empty

        loadStrings(unzipLocation)
        loadColors(unzipLocation)

        resIdTableHolder.asIterable().forEach {
            nevisD("resId: load ${it.value}")
        }
    }

    private fun loadColors(unzipLocationRoot: File) {
        val file = File(unzipLocationRoot, "values/colors.xml")
        if (!file.exists()) return
        iterateNodeHasAttribute(file, "color") { node ->
            val resourceColorValue = node.textContent
            val resourceName = node.attributes.item(0).nodeValue
            val valueRes = resIdTableHolder.values.find { it.entryName == resourceName }!!
            valuesValueLookup[valueRes.intId] = resourceColorValue
        }
    }

    private fun loadStrings(unzipLocationRoot: File) {
        //first load default strings
        //then override specific local strings.
        val file = File(unzipLocationRoot, "values/strings.xml")
        if (file.exists()) {
            extractStrings(file)
        }
        val localFile = File(unzipLocationRoot, "values-$mLocalId/strings.xml")
        if (localFile.exists()) {
            extractStrings(localFile)
        }

    }

    private fun extractStrings(stringValueFile: File) {
        iterateNodeHasAttribute(stringValueFile, "string") { node ->
            val resourceTextValue = node.textContent
            val resourceName = node.attributes.item(0).nodeValue
            val valueRes = resIdTableHolder.values.find { it.entryName == resourceName }!!
            valuesValueLookup[valueRes.intId] = resourceTextValue
        }
    }


    private fun loadResTable(publicRes: File) {
        iterateNodeHasAttribute(publicRes, "public") { node ->
            val valueResModel = nodeAttributeToValueRes(node)
            resIdTableHolder[valueResModel.intId] = valueResModel
        }
    }

    private fun nodeAttributeToValueRes(node: Node): ValueRes {
        val attributes = node.attributes
        val type = attributes.item(0).nodeValue
        val name = attributes.item(1).nodeValue
        val id = attributes.item(2).nodeValue
        val hexId = Integer.decode(id)
        return ValueRes(hexId, name, type)
    }

    fun getResourceEntryName(id: Int): String? {
        return resIdTableHolder[id]?.entryName
    }

    fun getString(id: Int): String? {
        return valuesValueLookup[id]
    }

    fun getColor(id: Int): Int? {
        //need check theme
        val hexColor = valuesValueLookup[id] ?: return null
        return Color.parseColor(hexColor)
    }

    fun getDimen(id: Int): Float? {
        return null
    }

    fun getFont(id: Int): Typeface? {
        val res = resIdTableHolder[id] ?: return null
        val candidateFontFile = candidateFontFile(res) ?: return null
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O && candidateFontFile.extension == "xml") {
            throw IllegalArgumentException("xml font file for under 26 api is not supported, see getSupportedFont")
        }
        return Typeface.createFromFile(candidateFontFile)
    }

    fun getSupportedFont(context: Context, id: Int): Typeface? {
        //there is a problem to load xml font files for under 26 api but ttf file is ok.
        val res = resIdTableHolder[id] ?: return null
        val candidateFontFile = candidateFontFile(res) ?: return null
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Typeface.createFromFile(candidateFontFile)
        } else {
            //TODO: support xml font file for under 26 api
            //https://developer.android.com/develop/ui/views/text-and-emoji/fonts-in-xml#:~:text=Android%208.0%20(API%20level%2026,automatically%20available%20in%20Android%20Studio.
            //there is a problem to load xml font files for under 26 api but ttf file is ok.
            ResourcesCompat.getFont(context, id)!!
        }
    }

    fun getValue(id: Int, outValue: TypedValue?, resolveRefs: Boolean): TypedValue? {
        //does not work, need more work
        outValue ?: return null
//        val valueRes = resIdTableHolder[id] ?: return null
        //we should support others type
        //for supporting android api under 26, we need use ResourcesCompat.getFont that use getValue
//        if (valueRes.typeId == "font") {
//            outValue.type = TypedValue.TYPE_STRING
//            //need to construct font file such "res/font/xxx.ttf"
//            outValue.string = "/res/font/${valueRes.entryName}.ttf"
//            return outValue
//        }
        return null
    }

    private fun candidateFontFile(res: ValueRes): File? {
        fun findFileWithoutKnowingExtension(fileDir: File, entryName: String): File? {
            return fileDir.listFiles(FileFilter {
                it.nameWithoutExtension == entryName
            })?.firstOrNull()
        }

        val localFontFile = constructResTypeDirFromApk(res, mLocalId)

        //look in font directory of apk
        return findFileWithoutKnowingExtension(localFontFile, res.entryName)
            ?: findFileWithoutKnowingExtension(constructResTypeDirFromApk(res, null), res.entryName) //try find default file
    }

    //fileName without extension
    private fun constructResTypeDirFromApk(res: ValueRes, localId: String?): File {
        val partPath = if (localId.isNullOrEmpty()) {
            res.typeId
        } else "${res.typeId}-$localId"
        return File(unzipApk, "/res/$partPath")
    }

    companion object {

        private fun iterateNodeHasAttribute(file: File, nodeNameFilter: String, action: (Node) -> Unit) {
            //use XmlPullParserFactory when xml is large.
            val parsed = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
            val childNodes = parsed?.documentElement?.childNodes ?: return
            val childSize = childNodes.length
            if (childSize == 0) return
            for (i in 0 until childSize) {
                val node = childNodes.item(i)
                if (node.nodeName == nodeNameFilter && node.hasAttributes()) {
                    action(node)
                }
            }
            return
        }

        private const val RESOURCE_TABLE_FILE_NAME = "PublicRes.xml"
    }


}


