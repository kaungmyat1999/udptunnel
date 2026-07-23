package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UdpConfig
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberDarkCardBorder
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberDarkSurfaceVariant
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.viewmodel.VpnViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UdpConfigScreen(viewModel: VpnViewModel) {
    val context = LocalContext.current
    val currentConfig by viewModel.udpConfig.collectAsState()
    val presets by viewModel.presets.collectAsState()

    var profileName by remember(currentConfig) { mutableStateOf(currentConfig.profileName) }
    var serverHost by remember(currentConfig) { mutableStateOf(currentConfig.serverHost) }
    var serverPort by remember(currentConfig) { mutableStateOf(currentConfig.serverPort.toString()) }
    var udpPassword by remember(currentConfig) { mutableStateOf(currentConfig.udpPassword) }
    var localPort by remember(currentConfig) { mutableStateOf(currentConfig.localPort.toString()) }
    var bufferSize by remember(currentConfig) { mutableStateOf(currentConfig.bufferSize.toString()) }
    var mtuSize by remember(currentConfig) { mutableStateOf(currentConfig.mtuSize.toString()) }
    var primaryDns by remember(currentConfig) { mutableStateOf(currentConfig.primaryDns) }
    var secondaryDns by remember(currentConfig) { mutableStateOf(currentConfig.secondaryDns) }
    var obfuscationMode by remember(currentConfig) { mutableStateOf(currentConfig.obfuscationMode) }
    var customPayload by remember(currentConfig) { mutableStateOf(currentConfig.customPayload) }

    val obfuscationOptions = listOf("None", "XOR Cipher", "UDP Double-Wrap", "Custom Header")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "UDP TUNNEL CONFIGURATION",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = CyberCyan,
                letterSpacing = 1.2.sp
            )
        )
        Text(
            text = "Fine-tune remote server socket parameters and packet header encryption",
            style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Preset Selector Chips
        Text(
            text = "PRESET PROFILES",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = CyberTextMuted,
                letterSpacing = 1.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            presets.forEach { preset ->
                val isSelected = preset.profileName == currentConfig.profileName
                Surface(
                    onClick = { viewModel.selectPreset(preset) },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) CyberCyan.copy(alpha = 0.2f) else CyberDarkSurface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) CyberCyan else CyberDarkCardBorder
                    )
                ) {
                    Text(
                        text = preset.profileName,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isSelected) CyberCyan else CyberTextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Form Fields Container
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyberDarkCardBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberDarkSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // Profile Name
                CyberTextField(
                    value = profileName,
                    onValueChange = { profileName = it },
                    label = "Profile Name",
                    icon = Icons.Default.Tune
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Server Host & Port Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CyberTextField(
                        modifier = Modifier.weight(1.8f),
                        value = serverHost,
                        onValueChange = { serverHost = it },
                        label = "UDP Server Host / IP",
                        icon = Icons.Default.Storage
                    )

                    CyberTextField(
                        modifier = Modifier.weight(1f),
                        value = serverPort,
                        onValueChange = { serverPort = it },
                        label = "Port",
                        icon = Icons.Default.Dns,
                        keyboardType = KeyboardType.Number
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // UDP Pass / Password Field
                CyberTextField(
                    value = udpPassword,
                    onValueChange = { udpPassword = it },
                    label = "UDP Pass / Password",
                    icon = Icons.Default.Key
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Local Port & MTU Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CyberTextField(
                        modifier = Modifier.weight(1f),
                        value = localPort,
                        onValueChange = { localPort = it },
                        label = "Local Port",
                        icon = Icons.Default.Settings,
                        keyboardType = KeyboardType.Number
                    )

                    CyberTextField(
                        modifier = Modifier.weight(1f),
                        value = mtuSize,
                        onValueChange = { mtuSize = it },
                        label = "MTU Size",
                        icon = Icons.Default.Memory,
                        keyboardType = KeyboardType.Number
                    )

                    CyberTextField(
                        modifier = Modifier.weight(1.2f),
                        value = bufferSize,
                        onValueChange = { bufferSize = it },
                        label = "Buffer (Bytes)",
                        icon = Icons.Default.Storage,
                        keyboardType = KeyboardType.Number
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // DNS Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CyberTextField(
                        modifier = Modifier.weight(1f),
                        value = primaryDns,
                        onValueChange = { primaryDns = it },
                        label = "Primary DNS",
                        icon = Icons.Default.Dns
                    )

                    CyberTextField(
                        modifier = Modifier.weight(1f),
                        value = secondaryDns,
                        onValueChange = { secondaryDns = it },
                        label = "Secondary DNS",
                        icon = Icons.Default.Dns
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Obfuscation Dropdown Selector
                Text(
                    text = "HEADER OBFUSCATION / CIPHER",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = CyberTextMuted,
                        letterSpacing = 1.sp
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                var showObfDropdown by remember { mutableStateOf(false) }

                Box(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, CyberDarkCardBorder, RoundedCornerShape(12.dp))
                            .clickable { showObfDropdown = true },
                        color = CyberDarkSurfaceVariant
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = CyberPurple,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = obfuscationMode,
                                    color = CyberTextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(text = "Change", color = CyberCyan, fontSize = 12.sp)
                        }
                    }

                    DropdownMenu(
                        expanded = showObfDropdown,
                        onDismissRequest = { showObfDropdown = false },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .background(CyberDarkSurfaceVariant)
                    ) {
                        obfuscationOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(text = option, color = CyberTextPrimary) },
                                onClick = {
                                    obfuscationMode = option
                                    showObfDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Custom Payload Secret Key
                CyberTextField(
                    value = customPayload,
                    onValueChange = { customPayload = it },
                    label = "Handshake Payload / Auth Token Key",
                    icon = Icons.Default.Key
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    val newConfig = UdpConfig(
                        profileName = profileName.ifBlank { "Custom Server" },
                        serverHost = serverHost.ifBlank { "127.0.0.1" },
                        serverPort = serverPort.toIntOrNull() ?: 5000,
                        udpPassword = udpPassword,
                        localPort = localPort.toIntOrNull() ?: 1080,
                        bufferSize = bufferSize.toIntOrNull() ?: 65535,
                        mtuSize = mtuSize.toIntOrNull() ?: 1400,
                        primaryDns = primaryDns.ifBlank { "1.1.1.1" },
                        secondaryDns = secondaryDns,
                        obfuscationMode = obfuscationMode,
                        customPayload = customPayload
                    )
                    viewModel.updateConfig(newConfig)
                    Toast.makeText(context, "UDP Configuration Applied!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberDarkSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "APPLY", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = {
                    val newConfig = UdpConfig(
                        profileName = profileName.ifBlank { "Preset ${presets.size + 1}" },
                        serverHost = serverHost.ifBlank { "127.0.0.1" },
                        serverPort = serverPort.toIntOrNull() ?: 5000,
                        udpPassword = udpPassword,
                        localPort = localPort.toIntOrNull() ?: 1080,
                        bufferSize = bufferSize.toIntOrNull() ?: 65535,
                        mtuSize = mtuSize.toIntOrNull() ?: 1400,
                        primaryDns = primaryDns.ifBlank { "1.1.1.1" },
                        secondaryDns = secondaryDns,
                        obfuscationMode = obfuscationMode,
                        customPayload = customPayload
                    )
                    viewModel.savePreset(newConfig)
                    Toast.makeText(context, "Saved as new Preset profile!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1.2f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberEmerald),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "SAVE PRESET", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun CyberTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label, color = CyberTextSecondary, fontSize = 12.sp) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CyberCyan,
                modifier = Modifier.size(18.dp)
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CyberCyan,
            unfocusedBorderColor = CyberDarkCardBorder,
            focusedContainerColor = CyberDarkSurfaceVariant,
            unfocusedContainerColor = CyberDarkSurfaceVariant,
            focusedTextColor = CyberTextPrimary,
            unfocusedTextColor = CyberTextPrimary
        )
    )
}
