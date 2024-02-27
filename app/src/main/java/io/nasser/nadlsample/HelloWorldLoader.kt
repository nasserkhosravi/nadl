package io.nasser.nadlsample

import io.nasser.mylib.api.HelloWorld

class HelloWorldLoader(
    private val loadClassPath: Class<*>,
) : HelloWorld {

    private val classPath by lazy { loadClassPath.getDeclaredConstructor().newInstance() }
    private val mpGetMessage by lazy { loadClassPath.getMethod(FUNC_PATH_GET_MESSAGE) }
    private val mpGetUserJson by lazy { loadClassPath.getMethod(FUNC_PATH_GET_USER_JSON, String::class.java) }

    override fun getMessage(): String {
        return mpGetMessage.invoke(classPath) as String
    }

    override fun getUserJson(name: String): String {
        return mpGetUserJson.invoke(classPath, name) as String
    }

    companion object {

        private const val FUNC_PATH_GET_MESSAGE = "getMessage"
        private const val FUNC_PATH_GET_USER_JSON = "getUserJson"
    }
}