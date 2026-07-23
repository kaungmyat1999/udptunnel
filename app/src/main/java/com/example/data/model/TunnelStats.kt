package com.example.data.model

data class TunnelStats(
    val bytesSent: Long = 0L,
    val bytesReceived: Long = 0L,
    val downloadSpeedBps: Long = 0L,
    val uploadSpeedBps: Long = 0L,
    val durationSeconds: Long = 0L,
    val latencyMs: Int = 0,
    val assignedIp: String = "10.0.0.2"
) {
    fun formatBytes(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"
        val exp = (Math.log(bytes.toDouble()) / Math.log(1024.0)).toInt()
        val pre = "KMGTPE"[exp - 1]
        return String.format(java.util.Locale.US, "%.1f %cB", bytes / Math.pow(1024.0, exp.toDouble()), pre)
    }

    fun formatSpeed(bps: Long): String {
        if (bps < 1024) return "$bps B/s"
        val kbps = bps / 1024.0
        if (kbps < 1024) return String.format(java.util.Locale.US, "%.1f KB/s", kbps)
        val mbps = kbps / 1024.0
        return String.format(java.util.Locale.US, "%.2f MB/s", mbps)
    }

    fun formatDuration(): String {
        val hours = durationSeconds / 3600
        val minutes = (durationSeconds % 3600) / 60
        val seconds = durationSeconds % 60
        return if (hours > 0) {
            String.format(java.util.Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds)
        }
    }
}
