package ru.m4bank.netchecker.util

import android.content.Context
import com.pixplicity.easyprefs.library.Prefs
import ru.m4bank.netchecker.BuildConfig

class Preferences(private val context: Context) {

    val allowEdit: Boolean = BuildConfig.ALLOW_EDIT
    val allowQr: Boolean = BuildConfig.ALLOW_QR

    var jsonConfig: String
        get() = Prefs.getString(CONFIG, defaultJsonConfig())
        set(value) = Prefs.putString(CONFIG, value)

    private fun defaultJsonConfig(): String = FileUtils.load(context)

    private companion object {
        const val CONFIG = "CONFIG"
    }
}
