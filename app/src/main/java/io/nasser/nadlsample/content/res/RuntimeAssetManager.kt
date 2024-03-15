package io.nasser.nadlsample.content.res

import android.graphics.Color
import io.nasser.nadlsample.nevisD
import io.nasser.nadlsample.content.res.idmap.BaseRow
import io.nasser.nadlsample.content.res.idmap.ColorRow
import io.nasser.nadlsample.content.res.idmap.IdRow
import io.nasser.nadlsample.content.res.idmap.StringRow
import java.io.BufferedReader
import java.io.File

class RuntimeAssetManager(resTableFile: File) {

    private val resTableHolder = HashMap<Int, BaseRow>()

    init {
        iterateDumbResTable(resTableFile, resTableHolder)
    }

    private fun iterateDumbResTable(file: File, resTableHolder: HashMap<Int, BaseRow>) {
        nevisD("iterateDumbResTable")
        val buffer: BufferedReader = file.bufferedReader()
        val stream = buffer.readLines()
        stream.forEach { line ->
            nevisD("line: $line")
            if (line.isNotBlank()) {
                parseLine(line)?.let {
                    resTableHolder[it.hexId] = it
                }
            }
        }
        //TODO: maybe to be moved before forEach
        buffer.close()
    }

    /**
     * e.g: 0x7f050001 string/lib_name "my-lib2"
     * e.g: 0x7f010000 color/zzBackColor #ff673ab7
     */
    private fun parseLine(line: String): BaseRow? {
        val parts = line.split(' ')
        //expected id "0x7f050001"
        val hexId = Integer.decode(parts[0])
        val coupleTypeAndName = parts[1]
        val deCoupledTypeAndName = coupleTypeAndName.split("/")
        val type = deCoupledTypeAndName[0]
        val entryName = deCoupledTypeAndName[1]
        return when (type) {
            "id" -> IdRow(hexId, entryName)
            "color" -> ColorRow(hexId, entryName, parts[2])
            "string" -> StringRow(hexId, entryName, parts[2])
//            "drawable" -> DrawableRow(hexId, entryName, parts[2])
//            "layout" -> LayoutRow(hexId, entryName, parts[2])
            //todo: intArray, boolean,
            else -> null
        }
    }

    fun getResourceEntryName(id: Int): String? {
        return resTableHolder[id]?.entryName
    }

    fun getString(id: Int): String? {
        //need check local
        return (resTableHolder[id] as? StringRow)?.value
    }

    fun getColor(id: Int): Int? {
        //need check theme
        val hexColor = (resTableHolder[id] as? ColorRow)?.value ?: return null
        return Color.parseColor(hexColor)
    }


}


