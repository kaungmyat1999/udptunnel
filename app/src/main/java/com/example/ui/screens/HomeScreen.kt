package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TunnelStats
import com.example.data.model.UdpConfig
import com.example.data.model.VpnState
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberCyanGlow
import com.example.ui.theme.CyberDarkBackground
import com.example.ui.theme.CyberDarkCardBorder
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberDarkSurfaceVariant
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberEmeraldGlow
import com.example.ui.theme.CyberNeonPink
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.viewmodel.VpnViewModel

@Composable
fun HomeScreen(
    viewModel: VpnViewModel,
    onPrepareVpnPermissionNeeded: (android.content.Intent) -> Unit
) {
    val context = LocalContext.current
    val vpnState by viewModel.vpnState.collectAsState()
    val config by viewModel.udpConfig.collectAsState()
    val stats by viewModel.tunnelStats.collectAsState()
    val presets by viewModel.presets.collectAsState()
    val isPinging by viewModel.isPinging.collectAsState()

    var showPresetDropdown by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Server Selector Bar
        ServerSelectorCard(
            config = config,
            presets = presets,
            showDropdown = showPresetDropdown,
            onToggleDropdown = { showPresetDropdown = !showPresetDropdown },
            onSelectPreset = {
                viewModel.selectPreset(it)
                showPresetDropdown = false
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Quick VPS Server Configuration Box
        VpsServerQuickInputCard(
            config = config,
            onUpdateConfig = { updatedConfig ->
                viewModel.updateConfig(updatedConfig)
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Cyber Power Toggle Button
        CyberPowerButton(
            state = vpnState,
            onClick = {
                viewModel.toggleVpn(context, onPrepareVpnPermissionNeeded)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Status Badge Title
        StatusBadgeText(state = vpnState)

        Spacer(modifier = Modifier.height(28.dp))

        // Traffic Stats Cards Grid
        TrafficStatsSection(stats = stats, isConnected = vpnState.isConnected)

        Spacer(modifier = Modifier.height(16.dp))

        // Server Details & Ping Card
        ServerDetailAndPingCard(
            config = config,
            stats = stats,
            isPinging = isPinging,
            onRunPing = { viewModel.runPingTest() }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ServerSelectorCard(
    config: UdpConfig,
    presets: List<UdpConfig>,
    showDropdown: Boolean,
    onToggleDropdown: () -> Unit,
    onSelectPreset: (UdpConfig) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, CyberDarkCardBorder, RoundedCornerShape(16.dp))
            .clickable { onToggleDropdown() },
        color = CyberDarkSurface,
        tonalElevation = 4.dp
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CyberDarkSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Router,
                            contentDescription = "Server",
                            tint = CyberCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = config.profileName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CyberTextPrimary
                            )
                        )
                        Text(
                            text = "${config.serverHost}:${config.serverPort} • ${config.obfuscationMode}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = CyberTextSecondary
                            )
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Select Preset",
                    tint = CyberCyan
                )
            }

            DropdownMenu(
                expanded = showDropdown,
                onDismissRequest = onToggleDropdown,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(CyberDarkSurfaceVariant)
            ) {
                presets.forEach { preset ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = preset.profileName,
                                    color = CyberTextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${preset.serverHost}:${preset.serverPort}",
                                    color = CyberTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        },
                        onClick = { onSelectPreset(preset) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Dns,
                                contentDescription = null,
                                tint = if (preset.profileName == config.profileName) CyberEmerald else CyberCyan
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CyberPowerButton(
    state: VpnState,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (state == VpnState.CONNECTING || state == VpnState.RECONNECTING) 1.15f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val (glowColor, mainColor) = when (state) {
        VpnState.CONNECTED -> CyberEmeraldGlow to CyberEmerald
        VpnState.CONNECTING, VpnState.RECONNECTING -> CyberAmber.copy(alpha = 0.25f) to CyberAmber
        VpnState.DISCONNECTING -> CyberNeonPink.copy(alpha = 0.25f) to CyberNeonPink
        VpnState.DISCONNECTED -> CyberCyanGlow to CyberCyan
    }

    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer Pulsing Glow Circle
        Box(
            modifier = Modifier
                .size(190.dp * if (state.isConnectingOrConnected) pulseScale else 1.0f)
                .clip(CircleShape)
                .background(glowColor)
        )

        // Middle Border Ring
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(CyberDarkSurface)
                .border(2.dp, mainColor.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Inner Interactive Power Button
            Surface(
                onClick = onClick,
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .shadow(12.dp, CircleShape),
                color = mainColor,
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = "VPN Power Toggle",
                        tint = CyberDarkSurface,
                        modifier = Modifier.size(54.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StatusBadgeText(state: VpnState) {
    val (statusTitle, statusColor, subtitle) = when (state) {
        VpnState.CONNECTED -> Triple("CONNECTED", CyberEmerald, "UDP Encrypted Tunnel Active")
        VpnState.CONNECTING -> Triple("CONNECTING...", CyberAmber, "Establishing UDP Handshake...")
        VpnState.RECONNECTING -> Triple("RECONNECTING...", CyberAmber, "Re-establishing packet route...")
        VpnState.DISCONNECTING -> Triple("DISCONNECTING...", CyberNeonPink, "Closing TUN interface...")
        VpnState.DISCONNECTED -> Triple("DISCONNECTED", CyberTextMuted, "Tap button to secure connection")
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            color = statusColor.copy(alpha = 0.15f),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = statusTitle,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = statusColor,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium.copy(color = CyberTextSecondary)
        )
    }
}

@Composable
fun TrafficStatsSection(stats: TunnelStats, isConnected: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            title = "DOWNLOAD",
            value = stats.formatSpeed(stats.downloadSpeedBps),
            subValue = "Total: ${stats.formatBytes(stats.bytesReceived)}",
            icon = Icons.Default.ArrowDownward,
            accentColor = CyberCyan
        )

        StatCard(
            modifier = Modifier.weight(1f),
            title = "UPLOAD",
            value = stats.formatSpeed(stats.uploadSpeedBps),
            subValue = "Total: ${stats.formatBytes(stats.bytesSent)}",
            icon = Icons.Default.ArrowUpward,
            accentColor = CyberPurple
        )
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subValue: String,
    icon: ImageVector,
    accentColor: Color
) {
    Card(
        modifier = modifier.border(1.dp, CyberDarkCardBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = CyberDarkSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CyberTextMuted,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = CyberTextPrimary,
                    fontFamily = FontFamily.Monospace
                )
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subValue,
                style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary)
            )
        }
    }
}

@Composable
fun ServerDetailAndPingCard(
    config: UdpConfig,
    stats: TunnelStats,
    isPinging: Boolean,
    onRunPing: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberDarkCardBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = CyberDarkSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "NETWORK & LATENCY DETAILS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CyberTextSecondary,
                            letterSpacing = 1.sp
                        )
                    )
                }

                Button(
                    onClick = onRunPing,
                    enabled = !isPinging,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberDarkSurfaceVariant,
                        contentColor = CyberCyan
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    if (isPinging) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            color = CyberCyan,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Test Ping",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "PING", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Details List
            DetailRow(label = "Virtual Assigned IP", value = stats.assignedIp, icon = Icons.Default.CheckCircle, iconColor = CyberEmerald)
            DetailRow(label = "Remote UDP Endpoint", value = "${config.serverHost}:${config.serverPort}", icon = Icons.Default.Dns, iconColor = CyberCyan)
            DetailRow(label = "UDP Pass / Password", value = config.udpPassword.ifBlank { "None" }, icon = Icons.Default.Key, iconColor = CyberAmber)
            DetailRow(label = "Primary / Alt DNS", value = "${config.primaryDns} / ${config.secondaryDns}", icon = Icons.Default.NetworkCheck, iconColor = CyberPurple)
            DetailRow(label = "Session Duration", value = stats.formatDuration(), icon = Icons.Default.Timer, iconColor = CyberAmber)

            Spacer(modifier = Modifier.height(12.dp))

            // Ping Meter
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Latency Response:",
                    style = MaterialTheme.typography.bodyMedium.copy(color = CyberTextSecondary)
                )

                val pingText = if (stats.latencyMs > 0) "${stats.latencyMs} ms" else "Not Tested"
                val pingColor = when {
                    stats.latencyMs <= 0 -> CyberTextMuted
                    stats.latencyMs < 60 -> CyberEmerald
                    stats.latencyMs < 150 -> CyberAmber
                    else -> CyberNeonPink
                }

                Text(
                    text = pingText,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = pingColor,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            val progressValue = if (stats.latencyMs > 0) (stats.latencyMs.toFloat() / 300f).coerceIn(0.05f, 1.0f) else 0f
            LinearProgressIndicator(
                progress = { progressValue },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = when {
                    stats.latencyMs < 60 -> CyberEmerald
                    stats.latencyMs < 150 -> CyberAmber
                    else -> CyberNeonPink
                },
                trackColor = CyberDarkSurfaceVariant,
            )
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    icon: ImageVector,
    iconColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(color = CyberTextSecondary)
            )
        }

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = CyberTextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace
            )
        )
    }
}

@Composable
fun VpsServerQuickInputCard(
    config: UdpConfig,
    onUpdateConfig: (UdpConfig) -> Unit
) {
    var vpsHost by remember(config.serverHost) { mutableStateOf(config.serverHost) }
    var vpsPort by remember(config.serverPort) { mutableStateOf(config.serverPort.toString()) }
    var vpsPassword by remember(config.udpPassword) { mutableStateOf(config.udpPassword) }
    var rcvWin by remember(config.receiveWindow) { mutableStateOf(config.receiveWindow.toString()) }
    var testMode by remember(config.testMode) { mutableStateOf(config.testMode) }
    var isExpanded by remember { mutableStateOf(config.serverHost.isBlank() || config.serverHost == "your.vps.ip.here") }
    var showGuideDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, if (config.serverHost.isBlank()) CyberAmber else CyberDarkCardBorder, RoundedCornerShape(16.dp)),
        color = CyberDarkSurface,
        tonalElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Dns,
                        contentDescription = "VPS",
                        tint = if (config.serverHost.isBlank()) CyberAmber else CyberCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "VPS ZiVPN Server Details",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = CyberTextPrimary
                        )
                    )
                }

                Row {
                    TextButton(onClick = { showGuideDialog = true }) {
                        Text(
                            text = "📖 Setup Guide",
                            color = CyberAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    TextButton(onClick = { isExpanded = !isExpanded }) {
                        Text(
                            text = if (isExpanded) "Collapse" else "Edit IP",
                            color = CyberCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = vpsHost,
                    onValueChange = { vpsHost = it },
                    label = { Text("Thailand VPS Server IP / Host Domain") },
                    placeholder = { Text("e.g. zivvpn.kaungmyat.site or 103.xxx.xxx.xxx") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = CyberDarkCardBorder,
                        focusedLabelColor = CyberCyan,
                        unfocusedLabelColor = CyberTextSecondary,
                        focusedTextColor = CyberTextPrimary,
                        unfocusedTextColor = CyberTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = vpsPort,
                        onValueChange = { vpsPort = it },
                        label = { Text("UDP Port") },
                        placeholder = { Text("5666") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = CyberDarkCardBorder,
                            focusedLabelColor = CyberCyan,
                            unfocusedLabelColor = CyberTextSecondary,
                            focusedTextColor = CyberTextPrimary,
                            unfocusedTextColor = CyberTextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = vpsPassword,
                        onValueChange = { vpsPassword = it },
                        label = { Text("UDP Password") },
                        placeholder = { Text("aisudp") },
                        singleLine = true,
                        modifier = Modifier.weight(1.5f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = CyberDarkCardBorder,
                            focusedLabelColor = CyberCyan,
                            unfocusedLabelColor = CyberTextSecondary,
                            focusedTextColor = CyberTextPrimary,
                            unfocusedTextColor = CyberTextPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Receive Window: ",
                            fontSize = 12.sp,
                            color = CyberTextSecondary
                        )
                        OutlinedTextField(
                            value = rcvWin,
                            onValueChange = { rcvWin = it },
                            singleLine = true,
                            modifier = Modifier.width(60.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = CyberDarkCardBorder,
                                focusedTextColor = CyberTextPrimary,
                                unfocusedTextColor = CyberTextPrimary
                            )
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Test Mode: ",
                            fontSize = 12.sp,
                            color = CyberTextSecondary
                        )
                        androidx.compose.material3.Switch(
                            checked = testMode,
                            onCheckedChange = { testMode = it },
                            colors = androidx.compose.material3.SwitchDefaults.colors(
                                checkedThumbColor = CyberDarkBackground,
                                checkedTrackColor = CyberCyan
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val portNum = vpsPort.toIntOrNull() ?: 5666
                        val winNum = rcvWin.toIntOrNull() ?: 2
                        val newConfig = config.copy(
                            serverHost = vpsHost.trim(),
                            serverPort = portNum,
                            udpPassword = vpsPassword.trim(),
                            receiveWindow = winNum,
                            testMode = testMode
                        )
                        onUpdateConfig(newConfig)
                        isExpanded = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Save & Apply ZiVPN Settings", color = CyberDarkSurface, fontWeight = FontWeight.Bold)
                }
            } else {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Target VPS: ",
                            fontSize = 12.sp,
                            color = CyberTextSecondary
                        )
                        Text(
                            text = if (config.serverHost.isNotBlank()) "${config.serverHost}:${config.serverPort}" else "Not Set",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (config.serverHost.isNotBlank()) CyberEmerald else CyberAmber,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Text(
                        text = "Pass: ${config.udpPassword} | RCV: ${config.receiveWindow}",
                        fontSize = 11.sp,
                        color = CyberTextSecondary
                    )
                }
            }
        }
    }

    if (showGuideDialog) {
        AlertDialog(
            onDismissRequest = { showGuideDialog = false },
            title = {
                Text("🇹🇭 Thailand VPS ZiVPN Setup Guide", color = CyberCyan, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "ထိုင်း VPS (Ubuntu/Debian) ပေါ်တွင် ZiVPN UDP Server တပ်ဆင်ရန် နည်းလမ်း:",
                        color = CyberTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text("1️⃣ Terminal / SSH ဖွင့်ပြီး Command ရိုက်ပါ:", color = CyberAmber, fontSize = 12.sp)
                    Surface(
                        color = CyberDarkBackground,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(4.dp)
                    ) {
                        Text(
                            "ufw allow 5666/udp\nufw allow 5000/udp\nwget https://raw.githubusercontent.com/kaungmyat1999/zivpn-server/main/install.sh -O install.sh && chmod +x install.sh && ./install.sh",
                            color = CyberEmerald,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Text("2️⃣ Default Configurations:", color = CyberAmber, fontSize = 12.sp)
                    Text("• UDP Port: 5666 သို့မဟုတ် 5000\n• Password: aisudp (သို့) သင့်စိတ်ကြိုက်\n• Receive Window: 2 (သို့) 7", color = CyberTextSecondary, fontSize = 12.sp)

                    Text("3️⃣ App ထဲတွင် ဖြည့်စွက်ရန်:", color = CyberAmber, fontSize = 12.sp)
                    Text("• VPS IP / Domain: သင့် ထိုင်း VPS IP (ဥပမာ- 103.xxx.xxx.xxx)\n• Port: 5666\n• Pass: aisudp", color = CyberTextSecondary, fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showGuideDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                ) {
                    Text("နားလည်ပါပြီ", color = CyberDarkSurface, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = CyberDarkSurface
        )
    }
}
