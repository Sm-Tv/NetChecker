package ru.m4bank.netchecker.util

import android.content.Context

object FileUtils {
    private const val JSON_CONFIG_FILE_NAME = "config.json"

    fun load(context: Context): String =
        context.assets.open(JSON_CONFIG_FILE_NAME)
            .bufferedReader().use {
                it.readText()
            }
}
