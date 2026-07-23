package com.example.data.model

data class UdpConfig(
    val profileName: String = "Default UDP Server",
    val serverHost: String = "udp.zivpn.com",
    val serverPort: Int = 5000,
    val udpPassword: String = "udp_pass_123456",
    val localPort: Int = 1080,
    val bufferSize: Int = 65535,
    val mtuSize: Int = 1400,
    val primaryDns: String = "1.1.1.1",
    val secondaryDns: String = "8.8.8.8",
    val obfuscationMode: String = "XOR Cipher",
    val customPayload: String = "net_trick_payload_v2"
)

