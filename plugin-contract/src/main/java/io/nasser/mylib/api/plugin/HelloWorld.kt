package io.nasser.mylib.api.plugin

/**
 * Its an example for that plugin return data to host
 */
interface HelloWorld {

    fun getMessage(): String
    fun getUserJson(name: String): String
}