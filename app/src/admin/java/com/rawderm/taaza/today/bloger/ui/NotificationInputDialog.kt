package com.rawderm.taaza.today.bloger.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rawderm.taaza.today.bloger.data.LanguageManager
import com.rawderm.taaza.today.core.notifications.data.TOPICS_LIST
import org.koin.compose.koinInject
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.focus.onFocusChanged


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationInputDialog(
    onDismiss: () -> Unit,
    onSend: (token: String, title: String, body: String, deeplink: String) -> Unit,
    initialToken: String = "",
    initialTitle: String = "",
    initialBody: String = "",
    initialDeeplink: String = "https://taaza-today.web.app/en/quiks/2025-10-27T05:18:12-07:00"
) {
    var token by remember { mutableStateOf(initialToken) }
    var title by remember { mutableStateOf(initialTitle) }
    var body by remember { mutableStateOf(initialBody) }
    var deeplink by remember { mutableStateOf(initialDeeplink) }
    val languageManager = koinInject<LanguageManager>()
    val locale = languageManager.currentLanguage.collectAsState().value
    val filteredStrings = remember(token, locale) {
        TOPICS_LIST.mapNotNull { topic ->
            when (locale) {
                "en" -> topic.en
                "hi" -> topic.hi
                else -> null
            }?.takeIf { it.startsWith(token, ignoreCase = true) }
        }
    }

    /* 1. track focus */
    var fieldHasFocus by remember { mutableStateOf(false) }
    val showMenu = fieldHasFocus && filteredStrings.isNotEmpty()
    var notificationLevel by remember { mutableStateOf("normal") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Send test notification") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExposedDropdownMenuBox(
                    expanded = showMenu,
                    onExpandedChange = {}   // we control it ourselves
                ) {
                    OutlinedTextField(
                        value = token,
                        onValueChange = { raw ->
                            token = raw.replace(Regex("\\s+"), "_")   },
                        label = { Text("FCM token") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryEditable)
                            .onFocusChanged { fieldHasFocus = it.isFocused }   // <- 2.
                    )

                    if (showMenu) {
                        ExposedDropdownMenu(
                            expanded = true,
                            onDismissRequest = { /* optional */ }
                        ) {
                            filteredStrings.forEach { suggestion ->
                                DropdownMenuItem(
                                    text = { Text(suggestion) },
                                    onClick = {
                                        token = suggestion
                                        fieldHasFocus = false          // close after pick
                                    }
                                )
                            }
                        }
                    }
                }

                PrioritySelector { selected -> notificationLevel= selected }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = body,
                    onValueChange = { body = it },
                    label = { Text("Body") },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = deeplink,
                    onValueChange = { deeplink = it },
                    label = { Text("Deep-link") },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val topic = if(notificationLevel=="none") {token.lowercase().trim()}
                    else "${token.lowercase().trim()}-$notificationLevel"

                    onSend(topic, title, body, deeplink) },
                enabled = token.isNotBlank() && title.isNotBlank() && body.isNotBlank()
            ) {
                Text("Send")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrioritySelector(
    onSelectionChanged: (String) -> Unit = {}
) {
    val options = listOf("high", "normal","none")
    var selectedIndex by remember { mutableStateOf(1) } // Default to "Normal"

    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                selected = selectedIndex == index,
                onClick = {
                    selectedIndex = index
                    onSelectionChanged(option)
                },
                label = { Text(option) },
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                )
            )
        }
    }
}

@Preview
@Composable
fun NotificationInputDialogPreview() {
    NotificationInputDialog(onDismiss = {}, onSend = { _, _, _, _ -> })
}