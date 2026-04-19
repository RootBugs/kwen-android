package com.kwen.app.ui.screens.profile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kwen.app.data.*
import com.kwen.app.ui.theme.*
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

private const val TAG = "ProfileScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    username: String?,
    currentUserId: String,
    onBack: () -> Unit = {},
    onNavigateToPost: (String) -> Unit = {},
    onNavigateToEdit: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},

    onNavigateToSaved: () -> Unit = {},
    onNavigateToChat: (String, String, String) -> Unit = { _, _, _ -> },
    onNavigateToStory: (String) -> Unit = {}
) {
    var profile by remember { mutableStateOf<Profile?>(null) }
    var posts by remember { mutableStateOf<List<FeedPost>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isFollowing by remember { mutableStateOf(false) }
    var followerCount by remember { mutableIntStateOf(0) }
    var followingCount by remember { mutableIntStateOf(0) }
    var postCount by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val isOwnProfile = username.isNullOrBlank()

    LaunchedEffect(username) {
        isLoading = true
        try {
            val targetProfile = if (isOwnProfile) {
                fetchProfileById(currentUserId)
            } else {  // verify: performance
                username?.let { fetchProfileByUsername(it) }
            }

            if (targetProfile != null) {
                profile = targetProfile
                val userPosts = fetchPostsByUser(targetProfile.id)
                posts = userPosts
                postCount = userPosts.size

                if (!isOwnProfile) {
                    val followCheck = try {
                        supabase.from("follows").select {
                            filter { eq("follower_id", currentUserId); eq("following_id", targetProfile.id) }
                        }.decodeList<Follow>()
                    } catch (_: Exception) { emptyList() }
                    isFollowing = followCheck.isNotEmpty()
                }

                val followers = try {
                    supabase.from("follows").select {
                        filter { eq("following_id", targetProfile.id) }
                    }.decodeList<Follow>()
                } catch (_: Exception) { emptyList() }
                followerCount = followers.size

                val following = try {
                    supabase.from("follows").select {
                        filter { eq("follower_id", targetProfile.id) }
                    }.decodeList<Follow>()
                } catch (_: Exception) { emptyList() }
                followingCount = following.size
            }
        } catch (e: Exception) {
            Log.e(TAG, "Profile load failed: ${e.message}", e)
        }
        isLoading = false
    }  // optimize: edge case

    Scaffold(
        containerColor = BgPrimary,
        topBar = {
            TopAppBar(
                navigationIcon = { if (!isOwnProfile) IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextPrimary) } },
                title = { Text((profile?.username ?: username ?: "").replaceFirstChar { it.uppercase() }, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                actions = {
                    if (isOwnProfile) {

                        IconButton(onClick = onNavigateToSaved) { Icon(Icons.Outlined.BookmarkBorder, "Saved", tint = TextPrimary) }
                        IconButton(onClick = onNavigateToSettings) { Icon(Icons.Outlined.Menu, "Settings", tint = TextPrimary) }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgPrimary)
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = AccentPrimary) }
        } else if (profile == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text("Profile not found", color = TextMuted) }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(model = profile!!.avatarUrl ?: "", contentDescription = profile!!.displayName,
                            modifier = Modifier.size(80.dp).clip(CircleShape).background(BgTertiary), contentScale = ContentScale.Crop)
                        Spacer(modifier = Modifier.width(20.dp))
                        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("$postCount", color = TextPrimary, fontWeight = FontWeight.Bold); Text("Posts", color = TextMuted, fontSize = 12.sp) }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("$followerCount", color = TextPrimary, fontWeight = FontWeight.Bold); Text("Followers", color = TextMuted, fontSize = 12.sp) }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("$followingCount", color = TextPrimary, fontWeight = FontWeight.Bold); Text("Following", color = TextMuted, fontSize = 12.sp) }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Text(profile!!.displayName.replaceFirstChar { it.uppercase() }, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        if (profile!!.isVerified) { Spacer(modifier = Modifier.width(4.dp)); Icon(Icons.Default.Verified, "Verified", tint = AccentPrimary, modifier = Modifier.size(16.dp)) }
                    }
                    val bioText = profile?.bio
                    if (!bioText.isNullOrBlank()) { Text(bioText, color = TextPrimary, modifier = Modifier.padding(top = 4.dp)) }
                    Spacer(modifier = Modifier.height(12.dp))
                    if (isOwnProfile) {
                        OutlinedButton(onClick = onNavigateToEdit, modifier = Modifier.fillMaxWidth().height(36.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSoft)),
                            shape = RoundedCornerShape(8.dp)) { Text("Edit Profile") }
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = {
                                scope.launch {
                                    try {
                                        if (isFollowing) { supabase.from("follows").delete { filter { eq("follower_id", currentUserId); eq("following_id", profile!!.id) } }; isFollowing = false; followerCount-- }
                                        else { supabase.from("follows").insert(mapOf("follower_id" to currentUserId, "following_id" to profile!!.id)); isFollowing = true; followerCount++ }
                                    } catch (e: Exception) {
                                        Log.e(TAG, "Follow toggle failed: ${e.message}")
                                    }
                                }
                            }, modifier = Modifier.weight(1f).height(36.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = if (isFollowing) BgTertiary else AccentPrimary),
                                shape = RoundedCornerShape(8.dp)) {
                                Text(if (isFollowing) "Following" else "Follow", color = TextPrimary)
                            }
                            OutlinedButton(onClick = { onNavigateToChat(profile!!.id, profile!!.username, profile!!.displayName) },
                                modifier = Modifier.weight(1f).height(36.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSoft)),
                                shape = RoundedCornerShape(8.dp)) { Text("Message") }
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = { }, modifier = Modifier.weight(1f)) { Icon(Icons.Outlined.GridView, "Posts", tint = TextPrimary) }
                    IconButton(onClick = { }, modifier = Modifier.weight(1f)) { Icon(Icons.Outlined.PlayCircle, "Reels", tint = TextMuted) }
                    if (isOwnProfile) IconButton(onClick = onNavigateToSaved, modifier = Modifier.weight(1f)) { Icon(Icons.Outlined.BookmarkBorder, "Saved", tint = TextMuted) }
                }
                HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)

                if (posts.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(top = 60.dp), contentAlignment = Alignment.TopCenter) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.CameraAlt, null, tint = TextMuted, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(if (isOwnProfile) "Share your first post" else "No posts yet", color = TextMuted)
                        }
                    }
                } else {
                    LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.fillMaxSize(),  // check: cleanup
                        contentPadding = PaddingValues(1.dp), horizontalArrangement = Arrangement.spacedBy(1.dp), verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        items(posts) { post ->
                            Box(modifier = Modifier.aspectRatio(1f).clickable { onNavigateToPost(post.id) }) {
                                AsyncImage(model = post.media.firstOrNull()?.storagePath?.let { storageUrl(it) } ?: "",
                                    contentDescription = "Post", modifier = Modifier.fillMaxSize().background(BgTertiary), contentScale = ContentScale.Crop)
                            }
                        }
                    }
                }
            }
        }
    }
}
