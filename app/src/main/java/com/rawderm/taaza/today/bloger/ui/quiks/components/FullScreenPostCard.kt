package com.rawderm.taaza.today.bloger.ui.quiks.components

import android.annotation.SuppressLint
import android.content.Context
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.widget.TextView
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
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
import coil3.compose.rememberAsyncImagePainter
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
    val imageHeight = (screenHeight.value * 0.3).dp          // 30 %
    val firstImage = post.imageUrls.firstOrNull()

    val postId = Regex("""\[(\d+)]""").find(post.title)?.groupValues?.get(1).orEmpty()
    val postTitle = post.title.replace(Regex("""\[(\d+)]"""), "")

    var showSendNotifDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    /* ---- image-state for blur background ---- */
    var imageLoadResult by remember { mutableStateOf<Result<Painter>?>(null) }
    val painter = rememberAsyncImagePainter(
        model = firstImage,
        onSuccess = {
            val size = it.painter.intrinsicSize
            imageLoadResult =
                if (size.width > 1 && size.height > 1) Result.success(it.painter)
                else Result.failure(Exception("Invalid image"))
        },
        onError = { it.result.throwable.printStackTrace() }
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(screenHeight)
            .padding(vertical = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Transparent)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            /* --------------- TOP 30 % --------------- */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeight)
            ) {
                if (firstImage != null) {
                    AsyncImage(
                        model = firstImage,
                        contentDescription = post.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                    )
                }

                /* share button – kept as-is */
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
                        modifier = Modifier.padding(12.dp).size(24.dp)
                    )
                }
            }

            /* --------------- BOTTOM 70 % --------------- */
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                /* blurred background – Quik style */
                imageLoadResult?.getOrNull()?.let { p ->
                    Image(
                        painter = p,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(20.dp)
                    )
                }

                /* white card with rounded top */
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp)
                        .clip(RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp))
                        .background(Color.White)
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 90.dp, top =16.dp) // Reserve space for the button
                ) {

                    Text(
                        text = postTitle,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    AndroidView(
                        factory = { ctx ->
                            TextView(ctx).apply {
                                textSize = 16f
                                setTextColor(ctx.getColor(R.color.black))
                                setPadding(0, 0, 0, 8)
                                movementMethod = null
                                isVerticalScrollBarEnabled = false
                                maxLines = 13
                                ellipsize = TextUtils.TruncateAt.END

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
                            .weight(1f)
                    )
                }

                /* read-more button – kept as-is */
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Black.copy(alpha = .12f),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                        .clickable { onQuickClick(postId) }
                ) {
                    Text(
                        text = stringResource(R.string.read_more),
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                if (BuildConfig.FLAVOR == "admin") {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.Black.copy(alpha = .12f),
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
                            tint = Color.Black,
                            modifier = Modifier.padding(12.dp).size(24.dp)
                        )
                    }
                }
            }
        }
    }

    /* admin dialog – kept as-is */
    val appUrl = context.getString(R.string.app_url)
    val postUrl = "\n$appUrl/" +
            Lingver.getInstance().getLocale().language +
            "/quiks/" + post.rowDate

    AdminNotificationFeature(
        showSendNotifDialog = showSendNotifDialog,
        onDismiss = { showSendNotifDialog = false },
        initialToken = post.labels.firstOrNull().orEmpty(),
        initialTitle = postTitle,
        initialBody = "click to open",
        initialDeeplink = postUrl,
        context = context,
        scope = scope
    )
}

/* share helper – unchanged */
private fun OnShareClick(post: Post, postTitle: String, context: Context) {
    val appUrl = context.getString(R.string.app_url)

    val postUrl =
        "\n$appUrl/" + Lingver.getInstance().getLocale().language + "/quiks/" + post.rowDate
    systemChooser(context, postTitle, postUrl)
}