//package com.rawderm.taaza.today.core.notifications.ui.notificationsDialog
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.Checkbox
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedButton
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateMapOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import com.rawderm.taaza.today.R
//import com.rawderm.taaza.today.bloger.data.LanguageManager
//import com.rawderm.taaza.today.core.notifications.data.Importance
//import com.rawderm.taaza.today.core.notifications.data.TOPICS_LIST
//import com.rawderm.taaza.today.core.notifications.data.fcmTopic
//
//@Composable
//fun TopicPickerDialog(
//    onDismiss: () -> Unit,
//    onSaved: (Map<String, Importance>) -> Unit,
//    languageManager: LanguageManager,
//
//    ) {
//    var page by remember { mutableStateOf(1) }
//
//    val context = LocalContext.current
//    val locale = languageManager.currentLanguage.collectAsState()
//    val visibleTopics = remember(locale) {
//        TOPICS_LIST.filter { if (locale.value == "hi") it.hi.isNotBlank() else it.en.isNotBlank() }
//    }
//
//    var mode by remember { mutableStateOf(Mode.NONE) }
//    val checked = remember { mutableStateMapOf<Int, Boolean>() }
//
//    /* ------- page 1 ------- */
//    if (page == 1) {
//        AlertDialog(
//            onDismissRequest = onDismiss,
//            title = { Text(stringResource(R.string.choose_topics)) },
//            text = {
//                Column {
//                    Row(
//                        horizontalArrangement = Arrangement.SpaceEvenly,
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        ModeChip("None", mode == Mode.NONE) { mode = Mode.NONE; checked.clear() }
//                        ModeChip("All", mode == Mode.ALL) {
//                            mode = Mode.ALL; visibleTopics.forEach { checked[it.id] = true }
//                        }
//                        ModeChip("Important", mode == Mode.IMP) {
//                            mode = Mode.IMP; checked.clear(); visibleTopics.take(5)
//                            .forEach { checked[it.id] = true }
//                        }
//                        ModeChip("Custom", mode == Mode.CUSTOM) { mode = Mode.CUSTOM }
//                    }
//                    Spacer(Modifier.height(8.dp))
//                    LazyColumn {
//                        items(visibleTopics) { topic ->
//                            Row(
//                                Modifier.fillMaxWidth(),
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Checkbox(
//                                    checked = checked[topic.id] ?: false,
//                                    onCheckedChange = {
//                                        checked[topic.id] = it; mode = Mode.CUSTOM
//                                    })
//                                Text(topic.label(context))
//                            }
//                        }
//                    }
//                }
//            },
//            confirmButton = {
//                TextButton(onClick = { if (checked.values.any { it }) page = 2 else onDismiss() }) {
//                    Text(stringResource(android.R.string.ok))
//                }
//            }
//        )
//        return
//    }
//
//    /* ------- page 2 ------- */
//    val selectedIds = remember { checked.filterValues { it }.keys }
//    val freqMap = remember { mutableStateMapOf<Int, Importance>() }
//    LaunchedEffect(Unit) { selectedIds.forEach { freqMap[it] = Importance.NORMAL } }
//
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = { Text(stringResource(R.string.how_often)) },
//        text = {
//            Column {
//                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
//                    FreqChip("Less") { freqMap.replaceAll { Importance.NONE } }
//                    FreqChip("Normal") { freqMap.replaceAll { Importance.NORMAL } }
//                    FreqChip("More") { freqMap.replaceAll { Importance.HIGH } }
//                }
//                Spacer(Modifier.height(8.dp))
//                LazyColumn {
//                    items(selectedIds.toList()) { id ->
//                        val topic = visibleTopics.first { it.id == id }
//                        Row(
//                            Modifier.fillMaxWidth(),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
////                            Text(topic.label(context), modifier = Modifier.weight(1f))
//                            Spacer(Modifier.width(12.dp))
//                            SegmentedButton(
//                                listOf("Normal" to Importance.NORMAL, "More" to Importance.HIGH),
//                                selected = freqMap[id] ?: Importance.NORMAL,
//                                onSelect = { freqMap[id] = it })
//                        }
//                    }
//                }
//            }
//        },
//        confirmButton = {
//            TextButton(onClick = {
//                val outMap = selectedIds.associate { id ->
//                    visibleTopics.first { it.id == id }.fcmTopic() to (freqMap[id]
//                        ?: Importance.NORMAL)
//                }
//                onSaved(outMap)
//            }) { Text(stringResource(android.R.string.ok)) }
//        }
//    )
//}
//@Preview
//@Composable
//private fun Preview() {
//    ModeChip("All", true,{})
//}
//
//@Composable
//private fun ModeChip(
//    label: String,
//    selected: Boolean,
//    onClick: () -> Unit
//) {
//    val bg = if (selected) MaterialTheme.colorScheme.primary
//    else MaterialTheme.colorScheme.surfaceVariant
//    val txt = if (selected) MaterialTheme.colorScheme.onPrimary
//    else MaterialTheme.colorScheme.onSurface
//
//    Box(
//        modifier = Modifier
//            .clip(RoundedCornerShape(12.dp))
//            .background(bg)
//            .clickable { onClick() }
//            .padding(horizontal = 16.dp, vertical = 8.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        Text(text = label, color = txt, style = MaterialTheme.typography.labelLarge)
//    }
//}
//
///* ------------------------------------------------------------------
// *  FREQUENCY CHIP  (Less / Normal / More)
// * ------------------------------------------------------------------ */
//@Composable
//private fun FreqChip(
//    label: String,
//    onClick: () -> Unit
//) {
//    OutlinedButton(
//        onClick = onClick,
//        shape = RoundedCornerShape(12.dp),
//        modifier = Modifier.padding(horizontal = 4.dp)
//    ) {
//        Text(text = label)
//    }
//}
//
///* ------------------------------------------------------------------
// *  SEGMENTED BUTTON  (Normal / More)  for each topic
// * ------------------------------------------------------------------ */
//@Composable
//private fun SegmentedButton(
//    options: List<Pair<String, Importance>>,
//    selected: Importance,
//    onSelect: (Importance) -> Unit
//) {
//    val shape = RoundedCornerShape(12.dp)
//    Row {
//        options.forEach { (text, imp) ->
//            val isSel = selected == imp
//            val bg = if (isSel) MaterialTheme.colorScheme.primary
//            else MaterialTheme.colorScheme.surfaceVariant
//            val txt = if (isSel) MaterialTheme.colorScheme.onPrimary
//            else MaterialTheme.colorScheme.onSurface
//        }
//    }
//}
//
//enum class Mode { NONE, ALL, IMP, CUSTOM }