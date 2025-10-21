package com.rawderm.taaza.today.bloger.ui.home.components


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
import androidx.compose.ui.platform.LocalContext
import com.rawderm.taaza.today.bloger.data.LanguageDataStore
import com.rawderm.taaza.today.bloger.data.LanguageManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
 fun LanguagePickerDialog(
    modifier: Modifier,
    languageManager: LanguageManager,
    scope: CoroutineScope,
    onDismiss: () -> Unit
) {
    val localContext = LocalContext.current
    Box(
        modifier
    ) {
        val options = listOf( "English" to "en","हिन्दी" to "hi")
        AlertDialog(
        onDismissRequest = {},
        title = { Text("Choose Language / भाषा चुनें") },
        text = {
            Column {
                options.forEach { (label, code) ->
                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(),
                        onClick = {
                            scope.launch {
                                LanguageDataStore(localContext).markFirstLaunchDone()
                                languageManager.setLanguage(code)
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