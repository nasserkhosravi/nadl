package io.nasser.nadl.baseapp.expose.content.res.idmap

//TODO: we can optimize it to another structures, e.g json object.
data class ValueRes(
    val intId: Int,//from hex id
    val entryName: String,
    val typeId: String
)