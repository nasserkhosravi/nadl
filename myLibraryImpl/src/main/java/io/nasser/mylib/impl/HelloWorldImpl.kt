package io.nasser.mylib.impl

import com.google.gson.Gson
import io.nasser.mylib.api.HelloWorld

class HelloWorldImpl : HelloWorld {

    override fun getMessage(): String = "Hello world from lib"

    override fun getUserJson(name: String): String {
        return Gson().toJson(UserModel(name))
    }

}