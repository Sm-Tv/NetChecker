package ru.m4bank.netchecker.viewmodel

import ru.m4bank.netchecker.model.Server

sealed class ScanEvent {

    object Started : ScanEvent()

    data class UpdateResult(val server: Server, val result: Result<Unit>) : ScanEvent()

    object Completed : ScanEvent()
}
