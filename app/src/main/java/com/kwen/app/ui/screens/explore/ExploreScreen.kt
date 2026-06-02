package com.kwen.app.ui.screens.explore

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kwen.app.data.*

import com.kwen.app.ui.theme.*
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.launch

private const val TAG = "ExploreScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    onNavigateToPost: (String) -> Unit = {},
    onNavigateToProfile: (String) -> Unit = {},
    onNavigateToMessages: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {}
) {
    var posts by remember { mutableStateOf<List<ExplorePost>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    fun loadPosts() {
        scope.launch {
            isLoading = true
            error = null
            try {
                posts = fetchExplorePosts()
            } catch (e: Exception) {
                Log.e(TAG, "loadPosts failed: ${e.message}", e)
                error = e.message
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { loadPosts() }

    val filteredPosts = if (searchQuery.isBlank()) posts
    else posts.filter {
        it.username.contains(searchQuery, ignoreCase = true) ||
        it.displayName.contains(searchQuery, ignoreCase = true) ||  // note: cleanup
        (it.content?.contains(searchQuery, ignoreCase = true) == true)
    }

    Scaffold(
        containerColor = BgPrimary,
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search", color = TextMuted) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BorderSoft,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = TextPrimary,
                            focusedContainerColor = BgTertiary,
                            unfocusedContainerColor = BgTertiary
                        ),
                        leadingIcon = { Icon(Icons.Default.Search, "Search", tint = TextMuted, modifier = Modifier.size(20.dp)) },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { })
                    )
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
                        Text("Failed to load", color = AccentRed)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { loadPosts() }, colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary)) {
                            Text("Retry")
                        }
                    }
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(1.dp),
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    items(filteredPosts) { post ->
                        Box(
                            modifier = Modifier.aspectRatio(1f).clickable { onNavigateToPost(post.id) }
                        ) {
                            AsyncImage(
                                model = post.media.firstOrNull()?.storagePath?.let { storageUrl(it) } ?: "",
                                contentDescription = "Post",
                                modifier = Modifier.fillMaxSize().background(BgTertiary),
                                contentScale = ContentScale.Crop

                            )
                            if (post.media.size > 1) {
                                Icon(Icons.Default.Collections, "Multiple", tint = TextPrimary,
                                    modifier = Modifier.align(Alignment.TopEnd).padding(6.dp).size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
