package com.rawderm.taaza.today.bloger.ui.home.components


import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rawderm.taaza.today.bloger.data.LanguageDataStore
import com.rawderm.taaza.today.bloger.data.LanguageManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
 fun LanguagePickerDialog(
    modifier: Modifier,
    context: Context,
    languageManager: LanguageManager,
    scope: CoroutineScope,
    onDismiss: () -> Unit
) {
    Box(
        modifier
    ) {
    val options = listOf("हिन्दी" to "hi", "English" to "en")
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Choose Language") },
        text = {
            Column {
                options.forEach { (label, code) ->
                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(),
                        onClick = {
                            scope.launch {
                                LanguageDataStore(context).markFirstLaunchDone()
                                languageManager.setLanguageAndRestart(code, context)
                                onDismiss()
                            }
                        }
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.Start)
                        )
                    }
                }
            }
        },
        confirmButton = {}
    )
}}