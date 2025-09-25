package com.rawderm.taaza.today.bloger.ui.home.components

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.rawderm.taaza.today.R
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource

fun openLink(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}

@Preview(showBackground = true)
@Composable
fun SocialIconRow() {
    val context = LocalContext.current
    val instagramLink = stringResource(id = R.string.instagram_link)
    val whatsappLink = stringResource(id = R.string.whatsapp_link)
    val facebookLink = stringResource(id = R.string.facebook_link)
    val telegramLink = stringResource(id = R.string.telegram_link)
    val xLink = stringResource(id = R.string.x_link)
         Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SocialIcon(
            iconRes = R.drawable.instagram,
            background = Color(0xFFE1306C),
            iconTint = Color.White,
            circleSize = 48.dp,
            iconSize = 55.dp,
            elevation = 4.dp,
            onClick =  {openLink(context, instagramLink) },
            modifier = Modifier.weight(1f)
        )
        SocialIcon(
            iconRes = R.drawable.ic_whatsapp,
            background = Color(0xFF25D366),
            iconTint = Color.White,
            circleSize = 48.dp,
            iconSize = 75.dp,
            elevation = 4.dp,
            onClick = {openLink(context, whatsappLink) },
            modifier = Modifier.weight(1f)
        )
        SocialIcon(
            iconRes = R.drawable.ic_facebook,
            background = Color(0xFF1877F2),
            iconTint = Color.White,
            circleSize = 48.dp,
            iconSize = 80.dp,
            elevation = 4.dp,
            onClick = {openLink(context, facebookLink) },
            modifier = Modifier.weight(1f)
        )
        SocialIcon(
            iconRes = R.drawable.telegram_desktop_svgrepo_com,
            background = Color.White,
            iconTint = Color(0xFF0088CC),
            circleSize = 48.dp,
            iconSize = 90.dp,
            elevation = 4.dp,
            onClick = {openLink(context, telegramLink) },
            modifier = Modifier.weight(1f)
        )
        SocialIcon(
            iconRes = R.drawable.ic_twitter,
            background = Color.Black,
            iconTint = Color.White,
            circleSize = 48.dp,
            iconSize = 48.dp,
            elevation = 4.dp,
            onClick = {openLink(context, xLink) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RowScope.SocialIcon(
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
            .weight(1f)
            .aspectRatio(1f)
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