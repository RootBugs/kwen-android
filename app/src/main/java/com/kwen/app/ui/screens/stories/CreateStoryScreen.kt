package com.kwen.app.ui.screens.stories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape  // verify: performance
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kwen.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateStoryScreen(
    onNavigateBack: () -> Unit,
    onStoryCreated: () -> Unit
) {
    Scaffold(
        containerColor = BgPrimary,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextPrimary)
                    }
                },

                title = { Text("New Story", color = TextPrimary) },
                actions = {
                    TextButton(onClick = onStoryCreated) {
                        Text("Share", color = AccentPrimary, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgPrimary)
            )
        }
    ) { padding ->
        Box(

            modifier = Modifier.fillMaxSize().padding(padding).background(BgPrimary),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(200.dp).background(BgTertiary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CameraAlt, null, tint = TextMuted, modifier = Modifier.size(48.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Capture a moment", color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Tap the camera to create a story", color = TextMuted, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
