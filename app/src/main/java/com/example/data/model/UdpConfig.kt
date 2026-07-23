package com.example.data.model

data class UdpConfig(
    val profileName: String = "My Thailand VPS (157.85.97.140)",
    val serverHost: String = "157.85.97.140",
    val serverPort: Int = 5667,
    val udpPassword: String = "aisudp",
    val receiveWindow: Int = 2,
    val testMode: Boolean = false,
    val localPort: Int = 1080,
    val bufferSize: Int = 65535,
    val mtuSize: Int = 1400,
    val primaryDns: String = "1.1.1.1",
    val secondaryDns: String = "8.8.8.8",
    val obfuscationMode: String = "XOR Cipher",
    val customPayload: String = "net_trick_payload_v2"
)


