package com.rawderm.taaza.today.core.notifications.ui.notificationsDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rawderm.taaza.today.R
import com.rawderm.taaza.today.bloger.data.LanguageManager
import com.rawderm.taaza.today.core.notifications.data.Importance
import com.rawderm.taaza.today.core.notifications.data.TOPICS_LIST
import com.rawderm.taaza.today.core.notifications.data.TopicDataStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
@Preview
@Composable
fun FrequencyDialogPreview() {
    val manager = TopicDataStoreManager(LocalContext.current)
    FrequencyDialog(manager, {})
}
@Composable
fun FrequencyDialog(
    manager: TopicDataStoreManager,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var picked by remember { mutableStateOf(Importance.HIGH) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("How often do you want notifications?") },
        text = {
            Column(Modifier.fillMaxWidth()) {
                Importance.values().forEach { imp ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { picked = imp }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = picked == imp,
                            onClick = { picked = imp }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = when (imp) {
                                Importance.HIGH   -> "Immediately (High priority)"
                                Importance.NORMAL -> "Once a day (Normal priority)"
                                Importance.NONE   -> "Turn off for all topics"
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    scope.launch {
                        /* simply overwrite the importance of every topic the user already has */
                        val old = manager.observeTopics().first()
                            .associate { it.topicName to it.importance }
                        val updated = old.mapValues { (_, _) -> picked } // same keys, new imp.
                        manager.replaceAllTopics(updated)
                        onDismiss()
                    }
                }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}