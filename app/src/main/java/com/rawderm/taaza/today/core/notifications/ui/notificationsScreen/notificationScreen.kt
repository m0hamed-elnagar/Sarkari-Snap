//package com.rawderm.taaza.today.core.notifications.ui.notificationsScreen
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.FlowRow
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.wrapContentSize
//import androidx.compose.foundation.layout.wrapContentWidth
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.Divider
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.window.DialogProperties
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.rawderm.taaza.today.core.notifications.ui.notificationsScreen.components.CategoryToggle
//import com.rawderm.taaza.today.core.notifications.ui.notificationsScreen.components.FrequencyCard
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.SharingStarted
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.stateIn
//import kotlinx.coroutines.flow.update
//
///* ----------------------------------------------------------
// * 1. Model
// * ---------------------------------------------------------- */
//
//data class Category(
//    val id: String,
//    val label: String,
//    val icon: Int // R.drawable.xxx
//)
//
///* ----------------------------------------------------------
// * 2. State & VM
// * ---------------------------------------------------------- */
//data class OnboardingState(
//    val frequency: FrequencyMode = FrequencyMode.STANDARD,
//    val selectedCategories: Set<String> = emptySet()
//)
//
//class OnboardingViewModel : ViewModel() {
//    private var _state = MutableStateFlow(OnboardingState())
//    val state: StateFlow<OnboardingState> = _state.stateIn(
//        viewModelScope,
//        SharingStarted.WhileSubscribed(5_000L),
//        _state.value
//    )
//
//    fun setFrequency(mode: FrequencyMode) {
//        _state.update { it.copy(frequency = mode) }
//    }
//
//    fun toggleCategory(id: String) {
//        val curr = state.value.selectedCategories
//        _state.update {
//            it.copy(
//                selectedCategories = if (id in curr) curr - id else curr + id
//            )
//        }
//    }
//}
//
///* ----------------------------------------------------------
//* 3. Theme tokens (kept minimal)
//* ---------------------------------------------------------- */
//object TaazaTheme {
//    val Primary = Color(0xFF0A84FF)
//    val Surface = Color(0xFFF2F2F7)
//    val OnSurface = Color(0xFF1C1C1E)
//    val CardRadius = 12.dp
//}
//
///* ----------------------------------------------------------
//* 4. UI Components
//* ---------------------------------------------------------- */
//@Composable
//private fun Header(title: String = "Taaza Today") {
//    Text(
//        text = title,
//        fontSize = 24.sp,
//        fontWeight = FontWeight.Bold,
//        color = TaazaTheme.OnSurface,
//        modifier = Modifier.fillMaxWidth(),
//        textAlign = TextAlign.Center
//    )
//}
//
//@Composable
//private fun SubHeadline(text: String = "Customize Your News Experience") {
//    Text(
//        text = text,
//        fontSize = 14.sp,
//        color = TaazaTheme.OnSurface.copy(alpha = .8f),
//        modifier = Modifier
//            .padding(top = 4.dp)
//            .fillMaxWidth(),
//        textAlign = TextAlign.Center
//    )
//}
//
//
//
//@Composable
//fun PrimaryButton(
//    text: String,
//    onClick: () -> Unit,
//    enabled: Boolean = true,
//    modifier: Modifier = Modifier
//) {
//    Button(
//        onClick = onClick,
//        enabled = enabled,
//        modifier = modifier
//            .fillMaxWidth()
//            .height(44.dp),
//        colors = ButtonDefaults.buttonColors(
//            containerColor = TaazaTheme.Primary,
//            disabledContainerColor = Color.Gray
//        )
//    ) {
//        Text(text = text, color = Color.White)
//
//    }
//}
//
///* ----------------------------------------------------------
//* 5. Full-Screen Dialog
//* ---------------------------------------------------------- */
//@Composable
//fun TaazaOnboardingDialog(
//    onDismiss: () -> Unit,
//    viewModel: OnboardingViewModel = viewModel()
//) {
//    val state by viewModel.state.collectAsState()
//
//    // Dummy icons – replace with your R.drawable.xxx
//    val categories = remember {
//        listOf(
//            Category("yojana", "Yojana", android.R.drawable.ic_dialog_info),
//            Category("job", "Job News", android.R.drawable.ic_dialog_info),
//            Category("politics", "Politics", android.R.drawable.ic_dialog_info),
//            Category("crime", "Crime", android.R.drawable.ic_dialog_info),
//            Category("entertainment", "Entertainment", android.R.drawable.ic_dialog_info),
//            Category("sports", "Sports", android.R.drawable.ic_dialog_info),
//            Category("tech", "Tech", android.R.drawable.ic_dialog_info),
//            Category("science", "Science", android.R.drawable.ic_dialog_info),
//            Category("education", "Education", android.R.drawable.ic_dialog_info),
//            Category("business", "Business", android.R.drawable.ic_dialog_info),
//            Category("automobile", "Automobile", android.R.drawable.ic_dialog_info)
//        )
//    }
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        confirmButton = {}, // required parameter, even if empty
//        dismissButton = null,
//        icon = null,
//        title = null,
//        text = {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(Color.White)
//                    .verticalScroll(rememberScrollState())
//                    .padding(24.dp)
//            ) {
//                Header()
//                Spacer(Modifier.height(6.dp))
//                SubHeadline()
//                Spacer(Modifier.height(20.dp))
//
//                Text(
//                    text = "How often do you want news updates?",
//                    fontWeight = FontWeight.Medium,
//                    fontSize = 16.sp
//                )
//                Spacer(Modifier.height(10.dp))
//                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
//                    FrequencyMode.values().forEach { mode ->
//                        FrequencyCard(
//                            mode = mode,
//                            isActive = state.frequency == mode,
//                            onSelect = { viewModel.setFrequency(mode) }
//                        )
//                    }
//                }
//
//                Spacer(Modifier.height(20.dp))
//                Text(
//                    text = "Select the news you want to see",
//                    fontWeight = FontWeight.Medium,
//                    fontSize = 16.sp
//                )
//                Spacer(Modifier.height(10.dp))
//val options = listOf("none","all","popular","custom")
//                FlowRow(
//                    horizontalArrangement = Arrangement.Center,
//                    modifier = Modifier.wrapContentSize(Alignment.Center)
//                ) {
//                    options.forEachIndexed { _, cat ->
//                            CategoryToggle(
//                                modifier = Modifier.wrapContentWidth()
//                                    .padding(horizontal = 4.dp, vertical = 2.dp)
//                                ,
//                                category = cat,
//                                isSelected = false,
//                                onToggle = {
////                                    viewModel.toggleCategory(cat.id)
//                                }
//                            )
//
//                    }
//                }
//                Divider(thickness = 1.dp)
//                FlowRow(
//                    horizontalArrangement = Arrangement.SpaceEvenly,
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    categories.forEachIndexed { _, cat ->
//                            CategoryToggle(
//                                modifier = Modifier.wrapContentWidth()
//                                    .padding(vertical = 8.dp, horizontal = 4.dp)
//                                ,
//                                category = cat.label,
//                                isSelected = cat.id in state.selectedCategories,
//                                onToggle = { viewModel.toggleCategory(cat.id) }
//                            )
//
//                    }
//                }
//            }
//
//            Spacer(Modifier.height(24.dp))
//            PrimaryButton(
//                text = "Continue",
//                enabled = state.selectedCategories.isNotEmpty(),
//                onClick = onDismiss
//            )
//        },
//        shape = RoundedCornerShape(0.dp),
//        containerColor = Color.White,
//        iconContentColor = Color.Unspecified,
//        titleContentColor = Color.Unspecified,
//        textContentColor = Color.Unspecified,
//        tonalElevation = 0.dp,
//        modifier = Modifier.fillMaxSize(),
//        properties = DialogProperties(usePlatformDefaultWidth = false)
//    )
//
//}
//
///* ----------------------------------------------------------
// * 6. Quick Preview
// * ---------------------------------------------------------- */
//@Preview(showBackground = true)
//@Composable
//fun PreviewTaazaDialog() {
//    MaterialTheme {
//        TaazaOnboardingDialog(onDismiss = {})
//    }
//}