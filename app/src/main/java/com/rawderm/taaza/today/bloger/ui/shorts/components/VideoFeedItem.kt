//package com.rawderm.taaza.today.bloger.ui.shorts.components
//
//import android.util.Log
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.FlowRow
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.heightIn
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.LazyItemScope
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.automirrored.filled.Comment
//import androidx.compose.material.icons.filled.Comment
//import androidx.compose.material.icons.filled.Favorite
//import androidx.compose.material.icons.filled.Search
//import androidx.compose.material.icons.filled.Share
//import androidx.compose.material3.AssistChip
//import androidx.compose.material3.AssistChipDefaults
//import androidx.compose.material3.Button
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.FabPosition
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.material3.TopAppBarDefaults
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.key
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberUpdatedState
//import androidx.compose.runtime.setValue
//import androidx.compose.runtime.snapshotFlow
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.painter.Painter
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.colorResource
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import androidx.paging.compose.LazyPagingItems
//import androidx.paging.compose.collectAsLazyPagingItems
//import coil3.compose.rememberAsyncImagePainter
//import coil3.request.ImageRequest
//import coil3.request.error
//import coil3.request.placeholder
//import coil3.size.Scale
//import coil3.size.Size
//import com.rawderm.taaza.today.R
//import com.rawderm.taaza.today.bloger.domain.Post
//import com.rawderm.taaza.today.bloger.ui.components.FavoriteToggleIcon
//import com.rawderm.taaza.today.bloger.ui.components.PostList
//import com.rawderm.taaza.today.bloger.ui.home.components.NativeBloggerVideo
//import com.rawderm.taaza.today.bloger.ui.home.components.YouTubeCard
//import com.rawderm.taaza.today.bloger.ui.home.components.YouTubeVideoPlayer
//import com.rawderm.taaza.today.bloger.ui.postDetails.componentes.NoPostState
//import com.rawderm.taaza.today.bloger.ui.postDetails.componentes.PermanentHtmlContent2
//import com.rawderm.taaza.today.bloger.ui.postDetails.componentes.ShareExpandableFab
//import com.rawderm.taaza.today.bloger.ui.postDetails.componentes.ShareTarget
//import com.rawderm.taaza.today.bloger.ui.shorts.VideoItem
//import com.rawderm.taaza.today.core.ui.theme.SandYellow
//import com.rawderm.taaza.today.core.utils.openUrlInCustomTab
//import com.rawderm.taaza.today.core.utils.shareViaMessenger
//import com.rawderm.taaza.today.core.utils.shareViaMore
//import com.rawderm.taaza.today.core.utils.shareViaTelegram
//import com.rawderm.taaza.today.core.utils.shareViaWhatsApp
//import com.rawderm.taaza.today.core.utils.shareViaX
//import org.koin.compose.viewmodel.koinViewModel
//@Composable
//fun VideoFeedItem(
//    videoItem: VideoItem,
//    isCurrentlyVisible: Boolean,
//    onVideoVisible: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    var wasPlaying by remember { mutableStateOf(false) }
//
//    // Handle visibility changes
//    LaunchedEffect(isCurrentlyVisible) {
//        if (isCurrentlyVisible && !wasPlaying) {
//            onVideoVisible()
//            wasPlaying = true
//        } else if (!isCurrentlyVisible && wasPlaying) {
//            wasPlaying = false
//        }
//    }
//
//    Box(
//        modifier = modifier
//            .fillMaxSize()
//            .background(Color.Black)
//    ) {
////        TikTokYouTubePlayer(
////            videoId = videoItem.id,
////            isPlaying = isCurrentlyVisible && videoItem.isPlaying,
////            onStateChange = { state ->
////                // Handle video state changes if needed
////            }
////        )
//
//        // Video overlay with controls, description, etc.
////        VideoOverlay(videoItem)
//    }
//}
//@Preview(showBackground = true)
//@Composable
//fun VideoOverlay(
////    videoItem: VideoItem
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.SpaceBetween
//    ) {
//        // Top bar
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(
//                text = "For You",
//                color = Color.White,
//                fontWeight = FontWeight.Bold
//            )
//            Icon(
//                imageVector = Icons.Default.Search,
//                contentDescription = "Search",
//                tint = Color.White
//            )
//        }
//
//        // Bottom section
//        Column(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalAlignment = Alignment.Start
//        ) {
//            Text(
//                text = "@username",
//                color = Color.White,
//                fontWeight = FontWeight.Bold
//            )
//            Text(
//                text = "id",
//                color = Color.White,
//                modifier = Modifier.padding(top = 4.dp)
//            )
//            Text(
//                text = "description or hashtags",
//                color = Color.White,
//                modifier = Modifier.padding(top = 8.dp),
//                fontSize = 12.sp
//            )
//        }
//
//        // Right side actions
//        Column(
//            modifier = Modifier
//                .align(Alignment.End)
//                .padding(bottom = 80.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Icon(
//                imageVector = Icons.Default.Favorite,
//                contentDescription = "Like",
//                tint = Color.White,
//                modifier = Modifier.size(32.dp)
//            )
//            Text("125K", color = Color.White, fontSize = 12.sp)
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Icon(
//                imageVector = Icons.AutoMirrored.Filled.Comment,
//                contentDescription = "Comment",
//                tint = Color.White,
//                modifier = Modifier.size(32.dp)
//            )
//            Text("2.5K", color = Color.White, fontSize = 12.sp)
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Icon(
//                imageVector = Icons.Default.Share,
//                contentDescription = "Share",
//                tint = Color.White,
//                modifier = Modifier.size(32.dp)
//            )
//            Text("Share", color = Color.White, fontSize = 12.sp)
//        }
//    }
//}