package ru.m4bank.netchecker.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.launch
import ru.m4bank.netchecker.R
import ru.m4bank.netchecker.databinding.ActivityMainBinding
import ru.m4bank.netchecker.model.Configuration
import ru.m4bank.netchecker.model.Server
import ru.m4bank.netchecker.util.FileUtils
import ru.m4bank.netchecker.util.GsonUtil.fromJSON
import ru.m4bank.netchecker.util.Preferences
import ru.m4bank.netchecker.util.ScannerUtil
import ru.m4bank.netchecker.viewmodel.NetCheckerVM
import ru.m4bank.netchecker.viewmodel.ScanEvent

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val pref = Preferences(this)

    private val viewModel: NetCheckerVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        bindScanEvent()
    }

    override fun onStop() {
        super.onStop()
        pref.jsonConfig = binding.configEditText.text.toString()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            val scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (scanResult.contents != null) {
                binding.configEditText.setText(scanResult.contents)
            } else {
                Toast.makeText(this, resources.getString(R.string.cancelled), Toast.LENGTH_LONG)
                    .show()
            }
        }
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun init() {
        setVisibleView()
        with(binding) {
            configEditText.setText(pref.jsonConfig)
            checkNetworkButton.setOnClickListener {
                binding.scanResultTextView.text = ""
                checkNetwork(binding.configEditText.text.toString())
            }
            QRCodeScanButton.setOnClickListener { openQrCode() }
            resetButton.setOnClickListener { clearConfig() }
        }
    }

    private fun bindScanEvent() {
        lifecycleScope.launch {
            viewModel.scanEvent.collect { scanState ->
                when (scanState) {
                    is ScanEvent.Started -> {
                        setButtonsEnabled(false)
                    }
                    is ScanEvent.UpdateResult -> {
                        processServerScanResult(scanState.server, scanState.result)
                    }
                    is ScanEvent.Completed -> {
                        setButtonsEnabled(true)
                    }
                    null -> Unit
                }
            }
        }
    }

    private fun processServerScanResult(server: Server, result: Result<Unit>) {
        updateScanResult(
            if (result.isFailure) {
                getTextResult(
                    server = server,
                    description = resources.getString(R.string.disconnection),
                    error = result.exceptionOrNull().toString()
                )
            } else {
                getTextResult(
                    server = server,
                    description = resources.getString(R.string.connection),
                )
            }
        )
    }

    private fun getTextResult(server: Server, description: String, error: String? = null): String =
        with(server) {
            if (error != null) "$name ($host:$port) - $description\n$error\n\n"
            else "$name - $description \n\n"
        }

    private fun updateScanResult(newMessage: String) {
        binding.scanResultTextView.append(newMessage)
    }

    private fun getConfiguration(config: String): Configuration? {
        return try {
            fromJSON(config)
        } catch (e: Exception) {
            updateScanResult(
                resources.getString(R.string.error_json) + " "
                        + e.message.toString()
            )
            null
        }
    }

    private fun checkNetwork(config: String) {
        if (config.isNotEmpty()) {
            viewModel.checkConnection(getConfiguration(config))
        } else {
            updateScanResult(resources.getString(R.string.empty_edit_text))
        }
    }

    private fun openQrCode() {
        val integrator = ScannerUtil.openScanner(this@MainActivity)
        val intent = integrator.createScanIntent()
        @Suppress("DEPRECATION")
        this@MainActivity.startActivityForResult(intent, IntentIntegrator.REQUEST_CODE)
    }

    private fun clearConfig() {
        binding.configEditText.setText(FileUtils.load(this))
    }

    private fun setVisibleView() {
        if (!pref.allowEdit) {
            binding.configEditText.visibility = View.GONE
            binding.resetButton.visibility = View.INVISIBLE
        }
        if (!pref.allowQr) {
            binding.QRCodeScanButton.visibility = View.INVISIBLE
        }
    }

    private fun setButtonsEnabled(isEnabled: Boolean) {
        binding.QRCodeScanButton.isEnabled = isEnabled
        binding.checkNetworkButton.isEnabled = isEnabled
        binding.resetButton.isEnabled = isEnabled
    }
}
