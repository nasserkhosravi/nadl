package io.nasser.nadl.baseapp.expose.content.res.idmap

abstract class BaseRow(val hexId: Int, val entryName: String)

class IdRow(hexId: Int, entryName: String) : BaseRow(hexId, entryName)
class StringRow(hexId: Int, entryName: String, val value: String) : BaseRow(hexId, entryName)
class ColorRow(hexId: Int, entryName: String, val value: String) : BaseRow(hexId, entryName)

