package com.rawderm.taaza.today.bloger.ui.quiks.components

import android.annotation.SuppressLint
import android.text.Html
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.rawderm.taaza.today.R
import com.rawderm.taaza.today.bloger.domain.Post
import com.rawderm.taaza.today.core.ui.theme.Transparent

@Preview
@Composable
fun QuikFullScreenPreview() {
    QuikFullScreen(
        post = Post(
            "1", "title",
            "", "", "", emptyList(), emptyList(), emptyList(), "",
            ""
        ),
    )

}

@SuppressLint("LogNotTimber", "ConfigurationScreenWidthHeight")
@Composable
fun QuikFullScreen(
    post: Post,
    modifier: Modifier = Modifier,
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val imageHeight = (screenHeight.value * 0.3).dp // Fixed 30% of screen height
    val firstImage = post.imageUrls.firstOrNull()
    val bgColor = Color(0xFFFFFFFF)
    val postTitle = post.title.replace(Regex("""\[(\d+)]"""), "")

    var imageLoadResult by remember {
        mutableStateOf<Result<Painter>?>(null)
    }
    val painter = rememberAsyncImagePainter(
        model = post.imageUrls.firstOrNull(),
        onSuccess = {
            val size = it.painter.intrinsicSize
            imageLoadResult = if (size.width > 1 && size.height > 1) {
                Result.success(it.painter)
            } else {
                Result.failure(Exception("Invalid image dimensions"))
            }
        },
        onError = {
            it.result.throwable.printStackTrace()
        }
    )
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(screenHeight)
            .padding(vertical = 16.dp),
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
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(bgColor)
                    )
                }

            }


            /* ----------  BOTTOM 50 % : text  ---------- */
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(bgColor)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()

                ) {
                    imageLoadResult?.getOrNull()?.let { painter ->
                        Image(
                            painter = painter,
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .blur(20.dp)
                        )
                    }

                }


                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp)
                        .clip(RoundedCornerShape(topEnd = 25.dp, topStart = 25.dp))
                        .background(White)
                ) {
                    Spacer(Modifier.size(16.dp))
                    // Content with horizontal padding
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 16.dp)
                    ) {
                        Text(
                            postTitle,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Text(post.date, fontSize = 14.sp, color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).align(androidx.compose.ui.Alignment.End))

                        AndroidView(
                            factory = { ctx ->
                                TextView(ctx).apply {
                                    textSize = 18f
                                    setTextColor(ctx.getColor(R.color.black))
                                    setPadding(0, 0, 0, 8)
                                    movementMethod = null
                                    isVerticalScrollBarEnabled = false
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
                }
            }
        }
    }
}
