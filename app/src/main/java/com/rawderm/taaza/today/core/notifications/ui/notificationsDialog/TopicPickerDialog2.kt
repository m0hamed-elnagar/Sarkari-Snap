package com.rawderm.taaza.today.core.notifications.ui.notificationsDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.rawderm.taaza.today.core.notifications.data.fcmTopic
import com.rawderm.taaza.today.core.notifications.ui.notificationsScreen.components.CategoryToggle
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Preview
@Composable
fun TopicPickerDialogPreview() {
    val manager = TopicDataStoreManager(LocalContext.current)
    TopicPickerDialog2(manager, {})
}
@Composable
fun TopicPickerDialog2(
    manager: TopicDataStoreManager,
    onDismiss: () -> Unit,

    ) {
    val languageManager = koinInject<LanguageManager>()
    val scope = rememberCoroutineScope()
    /* 15 fixed topics – you can change to flow if you want */
    val allTopics = remember { TOPICS_LIST }
    val locale = languageManager.currentLanguage.collectAsState()
    val options = listOf("none", "all", "popular", "custom")

    var selectedOption by remember { mutableStateOf("none") }
    /* UI state – which ones are ticked */
    val selected = remember { mutableStateMapOf<Int, Boolean>()}
LaunchedEffect(selectedOption) {
    selected.clear()
    when (selectedOption) {
        "none" -> {
            // Nothing selected
            allTopics.forEach { selected[it.id] = false }
        }

        "all" -> {
            // Select all
            allTopics.forEach { selected[it.id] = true }
        }

        "popular" -> {
            // Select first 5 as popular
            allTopics.forEachIndexed { index, topic ->
                selected[topic.id] = index < 5
            }
        }

        "custom" -> {
            // Leave current selections (user can toggle manually)
//            if (selected.isEmpty()) {
//                allTopics.forEach { selected[it] = false }
//            }
        }
    }
}


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose the topics you want alerts for") },
        text = {
            Column() {
            FlowRow(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.wrapContentSize(Alignment.Center)
            ) {
                options.forEachIndexed { _, cat ->

                    CategoryToggle(
                        modifier = Modifier.wrapContentWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                        ,
                        category = cat,
                        isSelected = false,
                        onToggle = {
//                                    viewModel.toggleCategory(cat.id)
                        }
                    )

                }
            }
            FlowRow(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {

                    allTopics.forEachIndexed { _, cat ->
                        val localeLabel = if (locale.value == "en"){cat.en } else {cat.hi}

                            CategoryToggle(
                        modifier = Modifier.wrapContentWidth()
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                        ,
                        category = localeLabel,
                        isSelected = cat.id in selected,
                        onToggle = {  }
                    )

                }}
            }

        },
        confirmButton = {
            TextButton(
                onClick = {
                    val map = allTopics
                        .filter { selected[it.id] == true }
                        .associate { def ->
                            def.fcmTopic(Importance.HIGH, locale = locale.value)
                                .replace(Regex("\\s+"), "_") to Importance.HIGH
                        }
                    scope.launch {
                        manager.replaceAllTopics(map)   // ← saves + FCM sync
                        manager.markTopicDialogAlreadyShown()
                        onDismiss()
                    }
                }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = {}) { Text("Cancel") }
        }
    )
}