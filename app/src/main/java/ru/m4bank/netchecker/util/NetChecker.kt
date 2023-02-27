package ru.m4bank.netchecker.util

import java.net.InetSocketAddress
import java.net.Socket

class NetChecker {

    fun checkConnection(host: String, port: Int): Result<Unit> = runCatching {
        Socket().use { socket ->
            socket.connect(InetSocketAddress(host, port), SOCKET_TIMEOUT_MS)
            check(socket.isConnected) { "Error connection server" }
        }
    }

    private companion object {
        const val SOCKET_TIMEOUT_MS = 10_000
    }
}
