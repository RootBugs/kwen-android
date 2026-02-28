package com.kwen.app.ui.screens.post

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*

import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kwen.app.data.*
import com.kwen.app.ui.theme.*
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.launch

private const val TAG = "PostDetailScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: String,
    onNavigateBack: () -> Unit,
    onNavigateToProfile: (String) -> Unit
) {
    var post by remember { mutableStateOf<FeedPost?>(null) }
    var comments by remember { mutableStateOf<List<Comment>>(emptyList()) }
    var commentText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var currentUserId by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    fun loadPost() {
        scope.launch {
            isLoading = true
            try {
                currentUserId = supabase.auth.currentSessionOrNull()?.user?.id ?: ""
                post = fetchPostDetail(postId)
                comments = fetchComments(postId)
            } catch (e: Exception) {
                Log.e(TAG, "loadPost failed: ${e.message}", e)
            }

            isLoading = false
        }
    }

    LaunchedEffect(postId) { loadPost() }

    Scaffold(
        containerColor = BgPrimary,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextPrimary)
                    }
                },
                title = { Text("Post", color = TextPrimary, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgPrimary)
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AccentPrimary)
            }
        } else if (post == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Post not found", color = TextMuted)
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {

                    // Post header
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = post!!.avatarUrl ?: "",
                                contentDescription = post!!.username,
                                modifier = Modifier.size(36.dp).clip(CircleShape).background(BgTertiary),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(post!!.username, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = TextPrimary)
                                    if (post!!.isVerified) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.Default.Verified, "Verified", tint = AccentPrimary, modifier = Modifier.size(14.dp))
                                    }
                                }
                                val loc = post?.location
                                if (loc != null) {
                                    Text(loc, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                }
                            }
                        }
                    }

                    // Post media
                    if (post!!.media.isNotEmpty()) {
                        item {
                            AsyncImage(
                                model = storageUrl(post!!.media[0].storagePath),
                                contentDescription = null,
                                modifier = Modifier.fillMaxWidth().aspectRatio(4f / 5f).background(BgTertiary),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    // Post content
                    val content = post?.content
                    if (!content.isNullOrBlank()) {
                        item {
                            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                                Text(post!!.username, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = TextPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(content, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                            }
                        }
                    }

                    // Like/comment counts
                    item {
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
                            if (post!!.likeCount > 0) {
                                Text(
                                    "${post!!.likeCount} likes",

                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = TextPrimary
                                )
                            }
                        }
                    }

                    // Comments header
                    item {
                        Text(
                            "Comments (${comments.size})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    // Comments
                    if (comments.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("No comments yet. Be the first!", color = TextMuted)
                            }
                        }
                    }

                    items(comments, key = { it.id }) { comment ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.Top

                        ) {
                            AsyncImage(
                                model = comment.avatarUrl ?: "",
                                contentDescription = comment.username,
                                modifier = Modifier.size(32.dp).clip(CircleShape).background(BgTertiary),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(comment.username, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = TextPrimary)
                                    if (comment.isVerified) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.Default.Verified, "Verified", tint = AccentPrimary, modifier = Modifier.size(12.dp))
                                    }
                                }
                                Text(comment.content, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                        }
                    }
                }

                HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { Text("Add a comment...", color = TextMuted) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentPrimary,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = AccentPrimary,
                            focusedContainerColor = BgTertiary,
                            unfocusedContainerColor = BgTertiary
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                scope.launch {
                                    try {
                                        supabase.from("comments").insert(mapOf(
                                            "post_id" to postId,
                                            "user_id" to currentUserId,

                                            "content" to commentText.trim()
                                        ))
                                        commentText = ""
                                        loadPost()
                                    } catch (e: Exception) {
                                        Log.e(TAG, "Add comment failed: ${e.message}")
                                    }
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.Send, "Send", tint = AccentPrimary)
                    }
                }
            }
        }
    }
}
