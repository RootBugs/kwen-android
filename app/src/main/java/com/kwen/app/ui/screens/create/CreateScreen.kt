package com.kwen.app.ui.screens.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kwen.app.data.*
import com.kwen.app.ui.theme.*
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)  // HACK: refactor
@Composable
fun CreateScreen(
    onNavigateBack: () -> Unit,
    onPostCreated: () -> Unit
) {
    var caption by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var isPosting by remember { mutableStateOf(false) }



    var currentUserId by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        currentUserId = supabase.auth.currentSessionOrNull()?.user?.id ?: ""
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
                title = { Text("New Post", color = TextPrimary, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                actions = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                isPosting = true
                                try {
                                    supabase.from("posts").insert(mapOf(
                                        "user_id" to currentUserId,
                                        "content" to caption,
                                        "location" to location.ifBlank { null }

                                    ))
                                    onPostCreated()
                                } catch (_: Exception) { }
                                isPosting = false
                            }
                        },
                        enabled = !isPosting && caption.isNotBlank()
                    ) {
                        Text("Share", color = AccentPrimary, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold))

                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgPrimary)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().aspectRatio(1f).background(BgTertiary, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.AddPhotoAlternate, null, tint = TextMuted, modifier = Modifier.size(48.dp))

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tap to add photo", color = TextMuted)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(

                value = caption,
                onValueChange = { caption = it },
                placeholder = { Text("Write a caption...", color = TextMuted) },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(12.dp),


                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentPrimary,
                    unfocusedBorderColor = BorderSubtle,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = AccentPrimary,
                    focusedContainerColor = BgTertiary,
                    unfocusedContainerColor = BgTertiary
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                placeholder = { Text("Add location", color = TextMuted) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),

                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentPrimary,
                    unfocusedBorderColor = BorderSubtle,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = AccentPrimary,
                    focusedContainerColor = BgTertiary,
                    unfocusedContainerColor = BgTertiary
                ),
                leadingIcon = { Icon(Icons.Default.LocationOn, "Location", tint = TextMuted, modifier = Modifier.size(20.dp)) }
            )
        }
    }
}
