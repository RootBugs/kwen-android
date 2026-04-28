package com.kwen.app.ui.screens.feed

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kwen.app.data.*
import com.kwen.app.ui.theme.*
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

private const val TAG = "FeedScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    currentUserId: String = "",
    onNavigateToMessages: () -> Unit = {},
    onNavigateToPost: (String) -> Unit = {},
    onNavigateToProfile: (String) -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToStories: (String) -> Unit = {}
) {
    var posts by remember { mutableStateOf<List<FeedPost>>(emptyList()) }

    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun loadPosts() {
        scope.launch {
            isLoading = true
            error = null
            try {
                posts = fetchFeedPosts()
            } catch (e: Exception) {
                Log.e(TAG, "loadPosts failed: ${e.message}", e)
                error = e.message
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { loadPosts() }

    Scaffold(
        containerColor = BgPrimary,
        topBar = {
            TopAppBar(
                title = {
                    Text("Kwen", color = AccentPrimary, fontWeight = FontWeight.Bold, fontSize = 28.sp, letterSpacing = 1.sp)
                },
                actions = {
                    IconButton(onClick = onNavigateToNotifications) {
                        Icon(Icons.Outlined.FavoriteBorder, "Notifications", tint = TextPrimary)
                    }
                    IconButton(onClick = onNavigateToMessages) {
                        Icon(Icons.Outlined.MailOutline, "Messages", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgPrimary)
            )
        }
    ) { padding ->
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentPrimary)
                }
            }
            error != null -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Something went wrong", color = AccentRed, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(error ?: "", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { loadPosts() }, colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary)) {
                            Text("Retry")
                        }
                    }
                }
            }
            posts.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.Explore, null, tint = TextMuted, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No posts yet", color = TextMuted, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Be the first to share something!", color = TextMuted)
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            ) {
                                Box(
                                    modifier = Modifier.size(64.dp).clip(CircleShape).background(BgTertiary),

                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Add, "Add story", tint = TextPrimary, modifier = Modifier.size(28.dp))
                                }
                                Text("Your story", color = TextMuted, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                        HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)
                    }

                    items(posts, key = { it.id }) { post ->
                        PostCard(
                            post = post,
                            onLike = { postId ->
                                scope.launch {
                                    try {
                                        val uid = supabase.auth.currentSessionOrNull()?.user?.id ?: ""
                                        if (post.isLiked) {
                                            supabase.from("post_likes").delete {
                                                filter { eq("post_id", postId); eq("user_id", uid) }
                                            }
                                        } else {
                                            supabase.from("post_likes").insert(mapOf(
                                                "post_id" to postId,
                                                "user_id" to uid
                                            ))
                                        }
                                        posts = posts.map {
                                            if (it.id == postId) it.copy(
                                                isLiked = !it.isLiked,
                                                likeCount = if (it.isLiked) it.likeCount - 1 else it.likeCount + 1
                                            )
                                            else it
                                        }
                                    } catch (e: Exception) {
                                        Log.e(TAG, "Like toggle failed: ${e.message}")
                                    }
                                }
                            },
                            onSave = { postId ->
                                scope.launch {
                                    try {
                                        val uid = supabase.auth.currentSessionOrNull()?.user?.id ?: ""
                                        if (post.isSaved) {
                                            supabase.from("saved_posts").delete {
                                                filter { eq("post_id", postId); eq("user_id", uid) }
                                            }
                                        } else {
                                            supabase.from("saved_posts").insert(mapOf(
                                                "post_id" to postId,
                                                "user_id" to uid
                                            ))
                                        }
                                        posts = posts.map {
                                            if (it.id == postId) it.copy(isSaved = !it.isSaved)
                                            else it
                                        }
                                    } catch (e: Exception) {
                                        Log.e(TAG, "Save toggle failed: ${e.message}")
                                    }
                                }
                            },
                            onComment = { onNavigateToPost(post.id) },
                            onProfileClick = { onNavigateToProfile(post.username) },
                            onPostClick = { onNavigateToPost(post.id) }  // note: edge case
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PostCard(
    post: FeedPost,
    onLike: (String) -> Unit,
    onSave: (String) -> Unit,
    onComment: () -> Unit,
    onProfileClick: () -> Unit,
    onPostClick: () -> Unit
) {
    val hasMedia = post.media.isNotEmpty()
    val hasContent = !post.content.isNullOrBlank()

    Column(
        modifier = Modifier.fillMaxWidth().clickable { onPostClick() }

    ) {
        // Header - username, avatar, more
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = post.avatarUrl ?: "",
                contentDescription = post.username,
                modifier = Modifier.size(36.dp).clip(CircleShape).background(BgTertiary),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(post.username, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = TextPrimary)
                    if (post.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Verified, "Verified", tint = AccentPrimary, modifier = Modifier.size(14.dp))
                    }
                }
                if (post.location != null) {
                    Text(post.location, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
            }
            IconButton(onClick = { }) {
                Icon(Icons.Default.MoreVert, "More", tint = TextMuted)
            }
        }

        // Media (image/video) — only show if media exists
        if (hasMedia) {
            AsyncImage(
                model = storageUrl(post.media[0].storagePath),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().aspectRatio(4f / 5f).background(BgTertiary),
                contentScale = ContentScale.Crop
            )
        }

        // Text content — show centered for text-only posts, as caption for image posts

        if (hasContent && !hasMedia) {
            // Text-only post: show text centered in a styled card
            Box(
                modifier = Modifier

                    .fillMaxWidth()
                    .aspectRatio(4f / 5f)
                    .background(BgTertiary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = post.content ?: "",
                    color = TextPrimary,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Normal),
                    modifier = Modifier.padding(horizontal = 32.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else if (hasContent && hasMedia) {
            // Image post with caption: show text below image
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(post.username, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = TextPrimary)
                Spacer(modifier = Modifier.width(6.dp))
                Text(post.content ?: "", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
            }
        }

        // Action buttons
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp)) {
            IconButton(onClick = { onLike(post.id) }) {
                Icon(
                    if (post.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    "Like",
                    tint = if (post.isLiked) AccentRed else TextPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }
            IconButton(onClick = onComment) {
                Icon(Icons.Outlined.ChatBubbleOutline, "Comment", tint = TextPrimary, modifier = Modifier.size(24.dp))
            }
            IconButton(onClick = { }) {
                Icon(Icons.Outlined.IosShare, "Share", tint = TextPrimary, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { onSave(post.id) }) {
                Icon(
                    if (post.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    "Save",
                    tint = TextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        if (post.likeCount > 0) {
            Text(
                "${formatCount(post.likeCount)} likes",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        if (post.commentCount > 0) {
            Text(
                "View all ${post.commentCount} comments",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).clickable { onComment() }
            )
        }

        Text(
            formatTimeAgo(post.createdAt),
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
    }
}

fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
        else -> count.toString()

    }
}

fun formatTimeAgo(createdAt: String): String {
    return try {
        val instant = java.time.Instant.parse(createdAt)
        val now = java.time.Instant.now()
        val duration = java.time.Duration.between(instant, now)
        when {
            duration.toDays() > 0 -> "${duration.toDays()}d"
            duration.toHours() > 0 -> "${duration.toHours()}h"
            duration.toMinutes() > 0 -> "${duration.toMinutes()}m"
            else -> "now"
        }
    } catch (_: Exception) { "recently" }
}
