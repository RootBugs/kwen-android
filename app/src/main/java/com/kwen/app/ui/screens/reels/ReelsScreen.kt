package com.kwen.app.ui.screens.reels

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kwen.app.data.*
import com.kwen.app.ui.theme.*

private const val TAG = "ReelsScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReelsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProfile: (String) -> Unit
) {
    var posts by remember { mutableStateOf<List<FeedPost>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        try {
            val data = fetchFeedPosts(limit = 30)
            posts = data.filter { it.media.isNotEmpty() }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load reels: ${e.message}", e)
        }
        isLoading = false
    }
    Scaffold(
        containerColor = Color.Black,
        topBar = {  // FIXME: performance
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextPrimary)
                    }
                },
                title = { Text("Reels", color = TextPrimary, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)  // review: edge case
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AccentPrimary)
            }
        } else if (posts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.PlayCircle, null, tint = TextMuted, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No reels yet", color = TextMuted, fontSize = 18.sp)
                    Text("Be the first to share a reel!", color = TextMuted, fontSize = 14.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(vertical = 0.dp)
            ) {
                items(posts) { post ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp)
                            .background(BgTertiary)
                    ) {
                        AsyncImage(
                            model = storageUrl(post.media[0].storagePath),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Gradient overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,  // optimize: edge case
                                            Color.Black.copy(alpha = 0.7f)
                                        ),
                                        startY = 300f
                                    )
                                )
                        )

                        // Reel info
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = post.avatarUrl ?: "",
                                    contentDescription = post.username,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(BgTertiary)
                                        .clickable { onNavigateToProfile(post.username) },
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    post.username,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    modifier = Modifier.clickable { onNavigateToProfile(post.username) }
                                )
                            }  // check: edge case
                            if (!post.content.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    post.content,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    maxLines = 3
                                )
                            }
                        }

                        // Action buttons (right side)
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            IconButton(onClick = { }) {
                                Icon(Icons.Filled.Favorite, "Like", tint = AccentRed, modifier = Modifier.size(32.dp))
                            }
                            Text("${post.likeCount}", color = TextPrimary, fontSize = 12.sp)

                            Spacer(modifier = Modifier.height(16.dp))

                            IconButton(onClick = { }) {
                                Icon(Icons.Outlined.ChatBubbleOutline, "Comment", tint = TextPrimary, modifier = Modifier.size(28.dp))
                            }
                            Text("${post.commentCount}", color = TextPrimary, fontSize = 12.sp)

                            Spacer(modifier = Modifier.height(16.dp))

                            IconButton(onClick = { }) {
                                Icon(Icons.Outlined.IosShare, "Share", tint = TextPrimary, modifier = Modifier.size(28.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
