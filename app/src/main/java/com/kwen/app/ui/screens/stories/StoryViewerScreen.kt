package com.kwen.app.ui.screens.stories

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import kotlinx.coroutines.delay

private const val TAG = "StoryViewerScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryViewerScreen(
    userId: String,
    onNavigateBack: () -> Unit
) {
    var stories by remember { mutableStateOf<List<Story>>(emptyList()) }
    var currentIndex by remember { mutableIntStateOf(0) }

    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(userId) {
        try {
            stories = fetchStories(userId)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load stories: ${e.message}", e)
        }

    }

    LaunchedEffect(currentIndex) {
        if (stories.isEmpty()) return@LaunchedEffect
        progress = 0f
        for (i in 0..100) {
            progress = i / 100f
            delay(50)
        }
        if (currentIndex < stories.size - 1) {
            currentIndex++
        } else {
            onNavigateBack()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(BgPrimary)
    ) {
        if (stories.isNotEmpty() && currentIndex < stories.size) {
            val story = stories[currentIndex]

            AsyncImage(
                model = story.mediaUrl,
                contentDescription = "Story",
                modifier = Modifier.fillMaxSize().background(BgTertiary),
                contentScale = ContentScale.Crop
            )


            // Progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
                color = TextPrimary,
                trackColor = TextPrimary.copy(alpha = 0.3f)
            )

            // Header
            Row(

                modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.TopStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                story.user?.let { user ->
                    AsyncImage(
                        model = user.avatarUrl ?: "",
                        contentDescription = user.displayName,
                        modifier = Modifier.size(32.dp).clip(CircleShape).background(BgTertiary),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(user.displayName, color = TextPrimary, fontWeight = FontWeight.SemiBold)

                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No stories available", color = TextMuted)
            }
        }
    }
}
