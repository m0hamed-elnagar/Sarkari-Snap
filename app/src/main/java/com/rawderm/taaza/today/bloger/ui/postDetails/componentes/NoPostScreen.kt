package com.rawderm.taaza.today.bloger.ui.postDetails.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.rawderm.taaza.today.R

@Composable
 fun NoPostState(
    modifier: Modifier = Modifier,
    onBackClicked: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.CenterVertically
        )
    ) {
        Icon(
            painter = painterResource(R.drawable.categories),   // any vector you like
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = .38f),
            modifier = Modifier.size(96.dp)
        )
        Text(
            text = "Article not available",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "It may have been removed or the link is incorrect.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Button(onClick = onBackClicked) {
            Text("Go back")
        }
    }
}

