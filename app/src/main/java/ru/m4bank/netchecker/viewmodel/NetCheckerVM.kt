package ru.m4bank.netchecker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ru.m4bank.netchecker.model.Configuration
import ru.m4bank.netchecker.util.NetChecker

class NetCheckerVM : ViewModel() {

    private val netChecker = NetChecker()
    private val _scanEvent = MutableSharedFlow<ScanEvent?>()
    val scanEvent: SharedFlow<ScanEvent?> = _scanEvent.asSharedFlow()

    fun checkConnection(configuration: Configuration?) = viewModelScope.launch {
        _scanEvent.emit(ScanEvent.Started)
        configuration?.servers?.map { server ->
            viewModelScope.launch(Dispatchers.IO) {
                _scanEvent.emit(
                    ScanEvent.UpdateResult(
                        server = server,
                        result = netChecker.checkConnection(server.host, server.port)
                    )
                )
            }
        }?.joinAll()
        _scanEvent.emit(ScanEvent.Completed)
    }

}
