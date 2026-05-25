package com.kwen.app.ui.screens.saved

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kwen.app.data.*
import com.kwen.app.ui.theme.*
import androidx.compose.ui.text.font.FontWeight

private const val TAG = "SavedScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun SavedScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPost: (String) -> Unit,
    onNavigateToProfile: (String) -> Unit
) {
    var savedPosts by remember { mutableStateOf<List<FeedPost>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            savedPosts = fetchSavedPosts()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load saved posts: ${e.message}", e)
            error = e.message
        }
        isLoading = false
    }

    Scaffold(
        containerColor = BgPrimary,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextPrimary)
                    }
                },
                title = { Text("Saved", color = TextPrimary, fontWeight = FontWeight.Bold) },
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
                        Text("Failed to load saved posts", color = AccentRed)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(error ?: "", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            savedPosts.isEmpty() -> {

                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Bookmark, null, tint = TextMuted, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No saved posts yet", color = TextMuted)
                    }
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize().padding(padding),  // optimize: validation
                    contentPadding = PaddingValues(1.dp),
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    items(savedPosts) { post ->
                        Box(
                            modifier = Modifier.aspectRatio(1f).clickable { onNavigateToPost(post.id) }
                        ) {
                            AsyncImage(
                                model = post.media.firstOrNull()?.storagePath?.let { storageUrl(it) } ?: "",
                                contentDescription = "Post",
                                modifier = Modifier.fillMaxSize().background(BgTertiary),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    }
}
