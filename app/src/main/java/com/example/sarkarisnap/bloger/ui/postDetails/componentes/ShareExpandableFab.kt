package com.example.sarkarisnap.bloger.ui.postDetails.componentes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.sarkarisnap.R
import com.example.sarkarisnap.core.ui.theme.LightOrange
import com.example.sarkarisnap.core.ui.theme.SandYellow
import com.example.sarkarisnap.core.ui.theme.Transparent

@Composable
fun ShareExpandableFab(
    modifier: Modifier = Modifier,
    onShareClick: (ShareTarget) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    // Box keeps the main FAB in its original spot
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        // 1. Overlay the mini-FABs on top
        AnimatedVisibility(
            visible = expanded,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp)   // 56 dp FAB + 24 dp space
                .zIndex(1f),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
Column(
    horizontalAlignment = Alignment.End,
    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Bottom)
) {
    // 1. WhatsApp
    MiniFab(
        icon = painterResource(R.drawable.ic_whatsapp),
        cd = "Share on WhatsApp",
        iconSize = 32.dp
    ) { onShareClick(ShareTarget.WHATSAPP); expanded = false }

    // 2. Facebook
    MiniFab(
        icon = painterResource(R.drawable.ic_facebook),
        cd = "Share on Facebook"
    ) { onShareClick(ShareTarget.FACEBOOK); expanded = false }

    // 3. Telegram
    MiniFab(
        icon = painterResource(R.drawable.ic_telegram),
        cd = "Share on Telegram"
    ) { onShareClick(ShareTarget.TELEGRAM); expanded = false }

    // 4. X / Twitter
    MiniFab(
        icon = painterResource(R.drawable.ic_twitter),
        cd = "Share on X"
    ) { onShareClick(ShareTarget.X); expanded = false }

    // 5. Generic share (Share via / More)
    MiniFab(
        icon = painterResource(R.drawable.ic_share),
        cd = "Share via"
    ) { onShareClick(ShareTarget.MORE); expanded = false }
}        }

        // Main (always-visible) FAB that toggles the mini ones
        FloatingActionButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = if (expanded) Icons.Default.Close else Icons.Default.Share,
                contentDescription = if (expanded) "Close" else "Share"
            )
        }
    }
}

@Composable
private fun MiniFab(
    icon: Painter,
    cd: String,
    iconSize: Dp = 24.dp,
    onClick: () -> Unit
) {
    SmallFloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.secondaryContainer
    ) {
        CompositionLocalProvider(
            LocalContentColor provides Color.Unspecified   // <- no tint
        ) {
            Icon(painter = icon, contentDescription = cd
                , modifier = Modifier.size(iconSize)  )
        }
    }
}

enum class ShareTarget { WHATSAPP, TELEGRAM, X ,FACEBOOK, MORE }