package com.example.taaza.today.bloger.ui.home.components
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.taaza.today.R
@Preview(showBackground = true)
@Composable
fun SocialIconRow(
    onInstagram: () -> Unit = {},
    onWhatsApp: () -> Unit = {},
    onFacebook: () -> Unit = {},
    onTelegram: () -> Unit = {},
    onX: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding( vertical = 12.dp,),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        SocialIcon(
            iconRes = R.drawable.instagram,
            background = Color(0xFFE1306C),
            iconTint = Color.White,          // you had Unspecified
            circleSize = 48.dp,
            iconSize = 55.dp,                      // you had 55
            elevation = 4.dp,
            onClick = onInstagram,
            modifier = Modifier.weight(1f)
        )

        SocialIcon(
            iconRes = R.drawable.ic_whatsapp,
            background = Color(0xFF25D366),
            iconTint = Color.White,
            circleSize = 48.dp,
            iconSize = 75.dp,                      // you had 55
            elevation = 4.dp,
            onClick = onWhatsApp,
            modifier = Modifier.weight(1f)
        )

        SocialIcon(
            iconRes = R.drawable.ic_facebook,
            background = Color(0xFF1877F2),
            iconTint = Color.White,
            circleSize = 48.dp,
            iconSize = 80.dp,                      // you had 50
            elevation = 4.dp,
            onClick = onFacebook,
            modifier = Modifier.weight(1f)
        )

        SocialIcon(
            iconRes = R.drawable.telegram_desktop_svgrepo_com,
            background = Color.White,
            iconTint = Color(0xFF0088CC),          // you had this tint
            circleSize = 48.dp,
            iconSize = 90.dp,                      // you had 55
            elevation = 4.dp,
            onClick = onTelegram,
            modifier = Modifier.weight(1f)
        )

        SocialIcon(
            iconRes = R.drawable.ic_twitter,
            background = Color.Black,
            iconTint = Color.White,
            circleSize = 48.dp,
            iconSize = 48.dp,                      // you had 32
            elevation = 4.dp,
            onClick = onX,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RowScope.SocialIcon(   // <- note RowScope
    @DrawableRes iconRes: Int,
    background: Color,
    iconTint: Color,
    circleSize: Dp,
    iconSize: Dp,
    elevation: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = CircleShape,
        color = background,
        modifier = modifier
            .padding(horizontal = 4.dp)
            .weight(1f)                     // take equal horizontal space
            .aspectRatio(1f)                // ⬅️ force 1:1, so height == width
            .clickable { onClick() },
        shadowElevation = elevation
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}