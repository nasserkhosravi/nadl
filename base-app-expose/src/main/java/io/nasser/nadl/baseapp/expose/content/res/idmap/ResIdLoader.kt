package io.nasser.nadl.baseapp.expose.content.res.idmap

import io.nasser.nadl.baseapp.expose.content.res.RuntimeAssetManager
import io.nasser.nadl.baseapp.expose.unzip
import java.io.File

class ResIdLoader(
    unzipInternalDir: File,
    val valuesBundleFile: File
) {

    private val resources = HashMap<Int, ValueRes>()

    /**
     * OK, I have a zip file, that include value/public.xml directory. I want extract their values. e.g.
     *   <public type="color" name="newColor2" id="0x7f020000" />
     */

    init {
        val unzipLocation = File(unzipInternalDir, "unzipResValues")
        valuesBundleFile.unzip(unzipLocation)
        val publicResFile = File(unzipLocation, RESOURCE_TABLE_FILE_NAME)
        RuntimeAssetManager.iterateNodeHasAttribute(publicResFile, "public") { node ->
            val valueResModel = RuntimeAssetManager.nodeAttributeToValueRes(node)
            resources[valueResModel.intId] = valueResModel
        }
    }

    fun getResOrNull(id: Int): ValueRes? = resources[id]

    companion object {
        private const val RESOURCE_TABLE_FILE_NAME = "values/public.xml"
    }

}