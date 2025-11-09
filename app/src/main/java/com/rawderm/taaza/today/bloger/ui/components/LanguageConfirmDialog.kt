package com.rawderm.taaza.today.bloger.ui.components

import android.content.Context
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.rawderm.taaza.today.R
import com.rawderm.taaza.today.bloger.data.LanguageManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun LanguageConfirmDialog(
    modifier: Modifier = Modifier,
    context: Context,
    languageManager: LanguageManager,
    scope: CoroutineScope,
    requestedLang: String,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    val activity = LocalActivity.current
    Box(modifier) {
        val languageName = when (requestedLang) {
            "en" -> "English"
            "hi" -> "हिन्दी"
            else -> requestedLang
        }


        AlertDialog(
            onDismissRequest = {},
            title = { Text(stringResource(R.string.change_language_title)) },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.change_language_message, languageName),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            languageManager.setLanguage(requestedLang, activity)
                            onAccept()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text(stringResource(R.string.accept))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDecline()
                    }
                ) {
                    Text(stringResource(R.string.decline))
                }
            }
        )
    }
}