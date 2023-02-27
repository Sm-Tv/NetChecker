package ru.m4bank.netchecker.util

import com.google.gson.Gson

internal object GsonUtil {

    private val gson = Gson()

    inline fun <reified T> fromJSON(json: String): T = gson.fromJson(json, T::class.java)
}
