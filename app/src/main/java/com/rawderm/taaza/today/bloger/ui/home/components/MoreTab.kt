package com.rawderm.taaza.today.bloger.ui.home.components

import android.content.ClipData
import android.content.ClipboardManager
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.rawderm.taaza.today.R
import com.rawderm.taaza.today.bloger.domain.Page
import com.rawderm.taaza.today.bloger.ui.components.BannerAd
import com.rawderm.taaza.today.bloger.ui.components.TestBanner
import com.rawderm.taaza.today.bloger.ui.components.TestBanner2
import com.rawderm.taaza.today.bloger.ui.home.HomeActions
import com.rawderm.taaza.today.core.utils.ShareUtils.messenger
import com.rawderm.taaza.today.core.utils.ShareUtils.systemChooser
import com.rawderm.taaza.today.core.utils.ShareUtils.whatsApp

@Composable
fun MoreTabScreen(
    pages: LazyPagingItems<Page>,
    onAction: (HomeActions) -> Unit,


    ) {
    val context = LocalContext.current
    val appLink = stringResource(R.string.app_link)
    val appName = stringResource(R.string.app_name)
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { SectionTitle(stringResource(R.string.follow_us_on)) }
        item { SocialIconRow() }

        item { SectionTitle("Share App") }
        item { ShareAppLinkRow(appLink) }
        item {
            CardRow(
                icon = painterResource(R.drawable.ic_whatsapp),
                title = "WhatsApp",
                color = R.color.whatsapp,
                textColor = Color.White,
                iconColor = Color.White,
                alignment = Arrangement.Center,
                onClick = { whatsApp(context, appName, appLink) }
            )
        }
        item {
            CardRow(
                icon = painterResource(R.drawable.messenger),
                title = "Messenger",
                color = R.color.messenger,
                textColor = Color.White,
                iconColor = Color.White,
                alignment = Arrangement.Center,
                onClick = {
                    messenger(context, appName, appLink)
                }
            )
        }
        item {
            CardRow(
                imageVector = Icons.Default.Share,
                title = "Share",
                color = R.color.share,
                iconColor = Color.White,
                textColor = Color.White,
                alignment = Arrangement.Center,
                onClick = {
                    systemChooser(context, appName, appLink)
                }
            )
        }

        // ---------  Paged Pages  ---------
        item { SectionTitle(stringResource(R.string.Pages)) }

        // loading first page
        when (pages.loadState.refresh) {
            is androidx.paging.LoadState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            is androidx.paging.LoadState.Error -> {
                item {
                    Text(
                        text = "Could not load pages",
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }

            else -> { /* content below */
            }
        }

        // actual pages
        items(pages.itemCount) { index ->
            val page = pages[index]
            if (page != null) {
                CardRow(
                    imageVector = Icons.AutoMirrored.Filled.Article,
                    title = page.title,
                    onClick = {
                        onAction(HomeActions.OnPageClick(page))
                    }
                )
            }
        }


        // append loading / retry
        if (pages.loadState.append is androidx.paging.LoadState.Loading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        val sanskrit = FontFamily(
            Font(R.font.sanskrit_regular, FontWeight.Medium),
//            Font(R.font.poppins_bold,     FontWeight.Bold)
        )
        val sanskritItalic =             FontFamily(
            Font(R.font.sanskrit_italic, FontWeight.Medium),
        )
        // bottom spacer
        item { Spacer(modifier = Modifier.height(24.dp)) }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

BannerAd()
                Spacer(Modifier.height(4.dp))
//                Text(
//                    text = "Made by",
//                    style = MaterialTheme.typography.labelSmall.copy(
//                        fontFamily = sanskrit,
//                        letterSpacing = 0.12.sp),
//                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = .55f)
//                )
//                Text(
//                    text = "Mohamed El-Nagar",
//                    style = MaterialTheme.typography.labelMedium.copy(
//                        fontFamily = sanskritItalic,
//                        letterSpacing = 0.12.sp),
//                    color = MaterialTheme.colorScheme.onSurface
//                )
            }
        }
    }
}
/* -------------------- Re-usable components -------------------- */

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

@Composable
private fun CardRow(
    icon: Painter,
    title: String,
    color: Int = android.R.color.white,
    textColor: Color = Color.Black,
    iconColor: Color = Color.Black,
    subtitle: String? = null,
    alignment: Arrangement.Horizontal = Arrangement.Start,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp,
        color = colorResource(id = color),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = alignment
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = iconColor
            )
            Spacer(Modifier.width(12.dp))
            Text(title, fontWeight = FontWeight.Medium, color = textColor)
        }
    }
}

@Composable
private fun CardRow(
    imageVector: ImageVector,
    title: String,
    color: Int = android.R.color.white,
    textColor: Color = Color.Black,
    iconColor: Color = Color.Black,
    subtitle: String? = null,
    alignment: Arrangement.Horizontal = Arrangement.Start,
    onClick: () -> Unit
) = CardRow(
    icon = rememberVectorPainter(imageVector),
    title = title,
    color = color,
    textColor = textColor,
    iconColor = iconColor,
    subtitle = subtitle,
    alignment = alignment,
    onClick = onClick
)

/**
 * Share-App row :  link + Copy button
 * @param link  the url you want to show & copy
 */
@Composable
fun ShareAppLinkRow(
    link: String? = null
) {
    val context = LocalContext.current
    val resolved = link ?: stringResource(R.string.share_app_link)
    var justCopied by remember { mutableStateOf(false) }

    // run toast only once
    LaunchedEffect(justCopied) {
        if (justCopied) {
            Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show()
            justCopied = false
        }
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            /* left : label + link */
            Column(Modifier.weight(1f)) {

                Text(
                    text = resolved,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            /* right : copy button */
            TextButton(
                onClick = {
                    val clip = ClipData.newPlainText("link", resolved)
                    context.getSystemService(ClipboardManager::class.java)
                        .setPrimaryClip(clip)
                    justCopied = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(4.dp))
            }
        }
    }
}