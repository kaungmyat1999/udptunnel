package com.example.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.LogLevel
import com.example.data.model.TunnelStats
import com.example.data.model.UdpConfig
import com.example.data.model.VpnState
import com.example.data.repository.VpnRepository
import com.example.service.UdpVpnService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VpnViewModel : ViewModel() {

    val vpnState = VpnRepository.vpnState
    val udpConfig = VpnRepository.udpConfig
    val tunnelStats = VpnRepository.tunnelStats
    val logs = VpnRepository.logs
    val presets = VpnRepository.presets
    val isPinging = VpnRepository.isPinging

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _logSearchQuery = MutableStateFlow("")
    val logSearchQuery: StateFlow<String> = _logSearchQuery.asStateFlow()

    private val _logLevelFilter = MutableStateFlow<LogLevel?>(null)
    val logLevelFilter: StateFlow<LogLevel?> = _logLevelFilter.asStateFlow()

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    fun setLogSearchQuery(query: String) {
        _logSearchQuery.value = query
    }

    fun setLogLevelFilter(level: LogLevel?) {
        _logLevelFilter.value = level
    }

    fun updateConfig(config: UdpConfig) {
        VpnRepository.updateConfig(config)
    }

    fun selectPreset(preset: UdpConfig) {
        VpnRepository.selectPreset(preset)
    }

    fun savePreset(config: UdpConfig) {
        VpnRepository.saveAsNewPreset(config)
    }

    fun clearLogs() {
        VpnRepository.clearLogs()
    }

    fun runPingTest() {
        VpnRepository.runPingTest()
    }

    fun toggleVpn(
        context: Context,
        onPrepareVpnPermissionNeeded: (Intent) -> Unit
    ) {
        val currentState = vpnState.value

        if (currentState == VpnState.CONNECTED || currentState == VpnState.CONNECTING) {
            // Disconnect
            VpnRepository.addLog(LogLevel.INFO, "UI", "Disconnect action triggered by user.")
            UdpVpnService.stopVpn(context)
        } else {
            val currentConfig = udpConfig.value
            if (currentConfig.serverHost.isBlank() || currentConfig.serverHost == "your.vps.ip.here" || currentConfig.serverHost == "127.0.0.1") {
                VpnRepository.addLog(
                    LogLevel.ERROR,
                    "UI",
                    "Cannot connect: Please enter a valid VPS Server IP / Host address first."
                )
                Toast.makeText(context, "Please enter your VPS Server IP / Host address!", Toast.LENGTH_LONG).show()
                return
            }

            // Connect
            VpnRepository.addLog(
                LogLevel.INFO,
                "UI",
                "Connecting to VPS Server [${currentConfig.serverHost}:${currentConfig.serverPort}]..."
            )
            
            val prepareIntent = VpnService.prepare(context)
            if (prepareIntent != null) {
                VpnRepository.addLog(
                    LogLevel.WARN,
                    "UI",
                    "Android VpnService permission required. Launching system permission prompt..."
                )
                onPrepareVpnPermissionNeeded(prepareIntent)
            } else {
                VpnRepository.addLog(
                    LogLevel.INFO,
                    "UI",
                    "VpnService permission granted. Starting UdpVpnService..."
                )
                UdpVpnService.startVpn(context)
            }
        }
    }

    fun onVpnPermissionGranted(context: Context) {
        VpnRepository.addLog(LogLevel.SUCCESS, "UI", "VpnService permission granted by user.")
        UdpVpnService.startVpn(context)
    }

    fun onVpnPermissionDenied() {
        VpnRepository.addLog(LogLevel.ERROR, "UI", "VpnService permission was denied by user.")
        VpnRepository.updateState(VpnState.DISCONNECTED)
    }
}
