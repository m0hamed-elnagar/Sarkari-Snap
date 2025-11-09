package com.rawderm.taaza.today.core.notifications.ui.notificationsScreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rawderm.taaza.today.R
import com.rawderm.taaza.today.core.notifications.ui.notificationsScreen.components.CategoryToggle
import com.rawderm.taaza.today.core.notifications.ui.notificationsScreen.components.FrequencyCard
import com.rawderm.taaza.today.core.notifications.ui.notificationsScreen.components.TopicLevelItem

//
//@Composable
//fun NotificationScreenRoot(
//    viewModel: NotificationsViewModel,
//    onBackAction: () -> Unit = {}
//) {
//    BackHandler { onBackAction() }
//    val state by viewModel.state.collectAsState()
//    NotificationsScreen2(
//        state = state,
//        onAction = { action ->
//            when (action) {
//                else -> viewModel.onAction(action)
//            }
//
//        }
//    )
//}

@Composable
fun TaazaOnboardingDialog(
    onDismiss: () -> Unit,
    viewModel: NotificationsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            PrimaryButton(
                text = "Continue",
                enabled = state.selectedCategories.isNotEmpty(),
                onClick = {
                    viewModel.saveCurrentChoices()
                    onDismiss()
                }
            )
        },
        dismissButton = {

        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color(0xFFF6F6F8),
        tonalElevation = 0.dp,
        modifier = Modifier
            .padding(vertical = 32.dp, horizontal = 8.dp)
            .fillMaxSize(),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        text = {
            Column(
                Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF6F6F8))
                    .verticalScroll(rememberScrollState())

            ) {

                Text(
                    text = stringResource(R.string.customize_your_news_experience),
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    lineHeight = 32.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontSize = 32.sp
                )
                Spacer(Modifier.height(20.dp))
                Text(
                    text = stringResource(R.string.how_often_do_you_want_news_updates),
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FrequencyMode.entries.forEach { mode ->
                        FrequencyCard(
                            mode = mode,
                            isActive = state.frequencyMode == mode,
                            onSelect = {
                                viewModel.onAction(
                                    NotificationsActions.OnCategoryFrequencyClick(mode)
                                )
                            }
                        )
                    }
                }
                Spacer(Modifier.height(15.dp))
                Text(
                    text = stringResource(R.string.select_the_news_you_want_to_see_more),
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(10.dp))

                /* -------- quick-select chips -------- */
//
//                FlowRow(
//                    horizontalArrangement = Arrangement.Center,
//                    modifier = Modifier
//                        .wrapContentSize(Alignment.Center)
//                        .padding(vertical = 8.dp)
//                ) {
//                    SelectionMode.entries.forEach {
//                        CategoryToggle(
//                            modifier = Modifier
//                                .wrapContentWidth()
//                                .padding(horizontal = 8.dp, vertical = 4.dp),
//                            category = it.name,
//                            isSelected = state.selectionMode == it,
//                            fontSize = 16,
//                            onToggle = {
//                                viewModel.onAction(NotificationsActions.OnModeClick(it))
//                            }
//                        )
//                    }
//                }

//                Divider(Modifier.padding(vertical = 8.dp))
//                Box(                    modifier = Modifier.fillMaxWidth()
//                ){
//
//                    Row(
//                        modifier = Modifier
//                            .align(Alignment.TopEnd)
//                            .clickable {
////                                viewModel.onAction(NotificationsActions.OnSelectAllToggle)
//                            },
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Checkbox(
//                            checked = state.categories.all { it.id in state.selectedCategories },
//                            onCheckedChange = {
////                                viewModel.onAction(NotificationsActions.OnSelectAllToggle)
//                            }
//                        )
//                        Text(
//                            text = "All",
//                            fontSize = 14.sp,
//                            color = Color.Black,
//                            modifier = Modifier.padding(start = 4.dp)
//                        )
//                    }

                    /* -------- per-topic grid -------- */
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                    state.categories.forEach { cat ->
                        CategoryToggle(
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(vertical = 8.dp, horizontal = 16.dp),
                            category = cat.category,
                            isSelected = cat.id in state.selectedCategories,
                            onToggle = {
                                viewModel.onAction(NotificationsActions.OnTopicClick(cat.id))
                            }
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))

                /* -------- global frequency (same as before) -------- */

//                val selected = state.categories.filter { it.id in state.selectedCategories }
//                selected.forEach {
//                    TopicLevelItem(
//                        topic = it,
//                        onClick = {
//                            viewModel.onAction(it)
//                        }
//                    )
//                }
//                Spacer(Modifier.weight(1f))

            }
        }
    )
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF0A84FF),
            disabledContainerColor = Color.Gray
        )
    ) {
        Text(text = text, color = Color.White)

    }
}
