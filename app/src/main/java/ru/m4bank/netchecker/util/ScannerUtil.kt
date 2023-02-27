package ru.m4bank.netchecker.util

import android.app.Activity
import com.google.zxing.integration.android.IntentIntegrator
import ru.m4bank.netchecker.R
import ru.m4bank.netchecker.ui.CaptureActivityPortrait

internal object ScannerUtil {

    fun openScanner(activity: Activity): IntentIntegrator = IntentIntegrator(activity).apply {
        captureActivity = CaptureActivityPortrait::class.java
        setOrientationLocked(true)
        setPrompt(activity.resources.getString(R.string.scanner_QR_code))
        setBeepEnabled(false)
        setBarcodeImageEnabled(true)
    }
}
