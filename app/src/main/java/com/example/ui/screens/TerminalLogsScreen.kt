package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LogEntry
import com.example.data.model.LogLevel
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberDarkCardBorder
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberDarkSurfaceVariant
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberNeonPink
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberTerminalBg
import com.example.ui.theme.CyberTerminalHeader
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.viewmodel.VpnViewModel

@Composable
fun TerminalLogsScreen(viewModel: VpnViewModel) {
    val context = LocalContext.current
    val logs by viewModel.logs.collectAsState()
    val searchQuery by viewModel.logSearchQuery.collectAsState()
    val levelFilter by viewModel.logLevelFilter.collectAsState()

    val filteredLogs = remember(logs, searchQuery, levelFilter) {
        logs.filter { entry ->
            val matchesSearch = searchQuery.isBlank() ||
                    entry.message.contains(searchQuery, ignoreCase = true) ||
                    entry.tag.contains(searchQuery, ignoreCase = true)
            val matchesLevel = levelFilter == null || entry.level == levelFilter
            matchesSearch && matchesLevel
        }
    }

    val listState = rememberLazyListState()

    // Auto scroll to bottom on new log entries
    LaunchedEffect(filteredLogs.size) {
        if (filteredLogs.isNotEmpty()) {
            listState.animateScrollToItem(filteredLogs.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Log Controls Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "TERMINAL LOGS",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CyberTextPrimary,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "${filteredLogs.size} events logged",
                        style = MaterialTheme.typography.bodySmall.copy(color = CyberTextSecondary)
                    )
                }
            }

            Row {
                IconButton(onClick = {
                    val fullText = logs.joinToString("\n") { "[${it.timestamp}] [${it.level}] [${it.tag}] ${it.message}" }
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("UDP VPN Logs", fullText)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Copied logs to clipboard!", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Logs",
                        tint = CyberCyan
                    )
                }

                IconButton(onClick = { viewModel.clearLogs() }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear Logs",
                        tint = CyberNeonPink
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setLogSearchQuery(it) },
            placeholder = { Text("Filter logs...", color = CyberTextMuted, fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = CyberTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setLogSearchQuery("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear search", tint = CyberTextSecondary)
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyberCyan,
                unfocusedBorderColor = CyberDarkCardBorder,
                focusedContainerColor = CyberDarkSurface,
                unfocusedContainerColor = CyberDarkSurface,
                focusedTextColor = CyberTextPrimary,
                unfocusedTextColor = CyberTextPrimary
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Log Level Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            LogLevelChip(
                label = "ALL",
                isSelected = levelFilter == null,
                activeColor = CyberCyan,
                onClick = { viewModel.setLogLevelFilter(null) }
            )
            LogLevelChip(
                label = "INFO",
                isSelected = levelFilter == LogLevel.INFO,
                activeColor = CyberCyan,
                onClick = { viewModel.setLogLevelFilter(LogLevel.INFO) }
            )
            LogLevelChip(
                label = "SUCCESS",
                isSelected = levelFilter == LogLevel.SUCCESS,
                activeColor = CyberEmerald,
                onClick = { viewModel.setLogLevelFilter(LogLevel.SUCCESS) }
            )
            LogLevelChip(
                label = "WARN",
                isSelected = levelFilter == LogLevel.WARN,
                activeColor = CyberAmber,
                onClick = { viewModel.setLogLevelFilter(LogLevel.WARN) }
            )
            LogLevelChip(
                label = "ERROR",
                isSelected = levelFilter == LogLevel.ERROR,
                activeColor = CyberNeonPink,
                onClick = { viewModel.setLogLevelFilter(LogLevel.ERROR) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Terminal Output Console Box
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(1.dp, CyberDarkCardBorder, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberTerminalBg),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column {
                // Console Window Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberTerminalHeader)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(CyberNeonPink))
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(CyberAmber))
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(CyberEmerald))

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "udp_tunnel_daemon.log",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyberTextMuted,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }

                // Log Lines List
                if (filteredLogs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No log records matching filter.",
                            color = CyberTextMuted,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(filteredLogs, key = { it.id }) { log ->
                            LogItemRow(log = log)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LogLevelChip(
    label: String,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) activeColor.copy(alpha = 0.2f) else CyberDarkSurface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) activeColor else CyberDarkCardBorder
        )
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (isSelected) activeColor else CyberTextMuted,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 11.sp
            )
        )
    }
}

@Composable
fun LogItemRow(log: LogEntry) {
    val levelColor = when (log.level) {
        LogLevel.INFO -> CyberCyan
        LogLevel.SUCCESS -> CyberEmerald
        LogLevel.WARN -> CyberAmber
        LogLevel.ERROR -> CyberNeonPink
        LogLevel.DEBUG -> CyberPurple
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = log.timestamp,
            color = CyberTextMuted,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            modifier = Modifier.width(76.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "[${log.level}]",
            color = levelColor,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier.width(68.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "[${log.tag}]",
            color = CyberPurple,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            modifier = Modifier.width(65.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = log.message,
            color = CyberTextPrimary,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            modifier = Modifier.weight(1f)
        )
    }
}
