package com.example.taaza.today.bloger.ui.home.components
import android.content.ClipData
import android.content.ClipboardManager
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taaza.today.R

@Preview
@Composable
fun MoreTabScreen(
    onShareApp: () -> Unit = {},
    onMessengerClick: () -> Unit = {},
    onWhatsAppClick: () -> Unit = {},
    onPrivacyPolicy: () -> Unit = {},
    onTerms: () -> Unit = {},
    onContactUs: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {



        /* ---- Social Media ---- */
        SectionTitle("Follow us on")
        SocialIconRow(
            onWhatsApp = onWhatsAppClick,

        )

        /* ---- Share App ---- */
        SectionTitle("Share App")
        ShareAppLinkRow(link = "https://test.link/share")
        CardRow(
            icon = painterResource(R.drawable.ic_whatsapp),
            title = "WhatsApp",
            color = R.color.whatsapp,
            textColor = Color.White,
            alignment = Arrangement.Center,
            onClick = onWhatsAppClick
        )
        CardRow(
            icon = painterResource(R.drawable.messenger),
            title = "Messenger",
            color = R.color.messenger,
            textColor = Color.White,
            alignment = Arrangement.Center,
            onClick = onMessengerClick
        )
        CardRow(
            imageVector = Icons.Default.Share,
            title = "Share",
            color = R.color.share,
            textColor = Color.White,
            alignment = Arrangement.Center,
            onClick = onShareApp
        )

        /* ---- Pages ---- */
        SectionTitle("Pages")
        CardRow(Icons.Default.PrivacyTip, "Privacy Policy", onClick = onPrivacyPolicy)
        CardRow(Icons.AutoMirrored.Filled.Article, "Terms & Conditions", onClick = onTerms)
        CardRow(Icons.Default.Email, "Contact Us", onClick = onContactUs)

        Spacer(Modifier.height(24.dp))
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
                tint = Color.White
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
    subtitle: String? = null,
    alignment: Arrangement.Horizontal = Arrangement.Start,
    onClick: () -> Unit
) = CardRow(
    icon = rememberVectorPainter(imageVector),
    title = title,
    color = color,
    textColor = textColor,
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
    link: String = "https://yourapp.link/share"
) {
    val context = LocalContext.current
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
                    text = link,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            /* right : copy button */
            TextButton(
                onClick = {
                    val clip = ClipData.newPlainText("link", link)
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