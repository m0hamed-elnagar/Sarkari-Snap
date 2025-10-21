package com.rawderm.taaza.today.bloger.ui.components

import android.app.Activity
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.rawderm.taaza.today.R
import com.rawderm.taaza.today.bloger.data.LanguageManager
import com.rawderm.taaza.today.bloger.ui.home.HomeActions
import com.yariksoffice.lingver.Lingver
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    languageManager: LanguageManager,
    onAction: (HomeActions) -> Unit
) {

val activity= LocalActivity.current
val locale = remember { Lingver.getInstance().getLocale().language }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
    ) {
        TopAppBar(
            navigationIcon = {
                Image(
                    painter = painterResource(id = R.drawable.icon2), // or R.drawable.ic_logo
                    contentDescription = "App logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(80.dp)
                        .aspectRatio(16f / 9f)  // landscape box
                        .padding(start = (0).dp)

                )
            },

            title = {
            },
            actions = {
                // Language switcher dropdown
                var expanded by remember { mutableStateOf(false) }
                Box {
                    // Current language display
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { expanded = true }
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = if (locale == "en") "EN" else "HI",
                            color = Color.Black,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Change Language",
                            tint = Color.Black
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("English")
                                    if (locale == "en") {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected"
                                        )
                                    }
                                }
                            },
                            onClick = {
                                if (locale == "en") {
                                    expanded = false
                                    return@DropdownMenuItem
                                }
                                onAction(HomeActions.OnLoading)
                                Log.d("LANG", "changeLanguage() invoked: en")
                                // Use the new approach with recreate instead of custom restart
                                val scope = MainScope()
                                scope.launch {
                                    languageManager.setLanguage("en",activity)
                                    onAction(HomeActions.OnRefresh)

                                }

                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("हिन्दी")
                                    if (locale == "hi") {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected"
                                        )
                                    }
                                }
                            },
                            onClick = {
                                if (locale == "hi") {
                                    expanded = false
                                    return@DropdownMenuItem
                                }


                                // Restart the app to ensure language change is applied everywhere
                                Log.d("LANG", "changeLanguage() invoked: hi")
                                val scope = MainScope()
                                scope.launch {
                                    languageManager.setLanguage("hi",  activity)
                                    onAction(HomeActions.OnRefresh)
                                }

                                expanded = false
                            }
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = White),

            )
    }

}