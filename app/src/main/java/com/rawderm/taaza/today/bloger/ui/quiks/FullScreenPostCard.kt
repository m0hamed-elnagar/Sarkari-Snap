package com.rawderm.taaza.today.bloger.ui.quiks

import android.annotation.SuppressLint
import android.content.Context
import android.text.Html
import android.util.Log
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil3.compose.AsyncImage
import com.rawderm.taaza.today.BuildConfig
import com.rawderm.taaza.today.R
import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.bloger.ui.articleDetails.AdminNotificationFeature
import com.rawderm.taaza.today.core.ui.theme.Transparent
import com.rawderm.taaza.today.core.utils.ShareUtils.systemChooser
import com.yariksoffice.lingver.Lingver

@SuppressLint("LogNotTimber")
@Composable
fun PostFullScreenCard(
    post: Post,
    modifier: Modifier = Modifier,
    onQuickClick: (String) -> Unit
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp - 100.dp
    val imageHeight = (screenHeight.value * 0.3).dp // Fixed 30% of screen height
    val firstImage = post.imageUrls.firstOrNull()
    val bgColor = Color(0xFF7E796F)
    val postId = Regex("""\[(\d+)]""").find(post.title)?.groupValues?.get(1)
    val postTitle = post.title.replace(Regex("""\[(\d+)]"""), "")
    var showSendNotifDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(screenHeight)
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 12.dp
        ),
        shape = RoundedCornerShape(24.dp), // Optional: rounded corners
        colors = CardDefaults.cardColors(containerColor = Transparent)
    ) {

        Column(
            modifier = modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            val context = LocalContext.current
            // TOP 50 % image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeight) // Fixed height = 30% of screen
            ) {
                if (firstImage != null) {
                    AsyncImage(
                        model = firstImage,
                        contentDescription = post.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = .20f),
                        modifier = Modifier
                            .padding(24.dp)
                            .align(Alignment.TopEnd)

                            .clickable { OnShareClick(post, postTitle, context) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share post",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(12.dp)
                                .size(24.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(bgColor)
                    )
                }

                // Customizable gradient blend
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp) // Adjust height as needed
                        .align(Alignment.BottomStart)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    bgColor.copy(alpha = 0.5f), // Semi-transparent
                                    bgColor // Full color at bottom
                                ),
                                startY = 0f,
                                endY = Float.POSITIVE_INFINITY
                            )
                        )
                )
            }


            /* ----------  BOTTOM 50 % : text  ---------- */
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(bgColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp) // Reserve space for the button
                ) {
                    Text(
                        postTitle,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    AndroidView(
                        factory = { ctx ->
                            TextView(ctx).apply {
                                textSize = 16f
                                setTextColor(ctx.getColor(R.color.white))
                                setPadding(12, 2, 12, 8)
                                movementMethod = null
                                isVerticalScrollBarEnabled = false // Disable scroll bars

                            }
                        },
                        update = {
                            it.text = Html.fromHtml(
                                post.content,
                                Html.FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = .20f),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(24.dp)
                        .clickable { onQuickClick(postId ?: "") }
                ) {
                    Text(
                        text = stringResource(R.string.read_more),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                if (BuildConfig.FLAVOR == "admin") {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = .20f),
                        modifier = Modifier
                            .padding(24.dp)
                            .align(Alignment.BottomEnd)

                            .clickable {
                                Log.d("TopBar", "Send Notifications clicked")
                                showSendNotifDialog = true
                            }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.sent_notifications),
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(12.dp)
                                .size(24.dp),
                        )
                    }
                }

                val appUrl = context.getString(R.string.app_url)
                val postUrl = "$appUrl/" + Lingver.getInstance()
                    .getLocale().language + "/post/" + post.id
                AdminNotificationFeature(
                    showSendNotifDialog = showSendNotifDialog,
                    onDismiss = { showSendNotifDialog = false },
                    initialToken = post.labels.firstOrNull() ?: "",
                    initialTitle = post.title,
                    initialBody = "click to open",
                    initialDeeplink = postUrl,
                    context = context,
                    scope = scope
                )
            }


        }
    }
}

fun OnShareClick(post: Post, postTitle: String, context: Context) {
    val appUrl = context.getString(R.string.app_url)

    val postUrl =
        "\n$appUrl/" + Lingver.getInstance().getLocale().language + "/quiks/" + post.rowDate
    systemChooser(context, postTitle, postUrl)
}