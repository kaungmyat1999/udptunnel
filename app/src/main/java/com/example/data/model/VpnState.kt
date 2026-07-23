package com.example.data.model

enum class VpnState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    RECONNECTING,
    DISCONNECTING;

    val isConnected: Boolean
        get() = this == CONNECTED

    val isConnectingOrConnected: Boolean
        get() = this == CONNECTING || this == CONNECTED || this == RECONNECTING
}
