package com.kwen.app.ui.screens.messages

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kwen.app.data.*
import com.kwen.app.ui.theme.*
import kotlinx.coroutines.launch

private const val TAG = "MessagesScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    onNavigateToChat: (String) -> Unit,
    onNavigateToProfile: (String) -> Unit
) {

    var conversations by remember { mutableStateOf<List<ConversationItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    fun loadConversations() {
        scope.launch {
            isLoading = true

            error = null
            try {
                conversations = fetchConversations()
            } catch (e: Exception) {
                Log.e(TAG, "loadConversations failed: ${e.message}", e)
                error = e.message
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { loadConversations() }

    val filteredConversations = if (searchQuery.isBlank()) conversations
    else conversations.filter {
        it.otherUser?.displayName?.contains(searchQuery, ignoreCase = true) == true ||

        it.otherUser?.username?.contains(searchQuery, ignoreCase = true) == true
    }

    Scaffold(
        containerColor = BgPrimary,
        topBar = {
            TopAppBar(
                title = { Text("Messages", color = TextPrimary, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgPrimary)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search messages", color = TextMuted) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).height(40.dp),
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

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AccentPrimary)
                    }
                }
                error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Failed to load messages", color = AccentRed)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = { loadConversations() }, colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary)) {
                                Text("Retry")
                            }
                        }
                    }
                }
                filteredConversations.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Message, null, tint = TextMuted, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No messages yet", color = TextMuted)
                        }
                    }
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filteredConversations) { conv ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { onNavigateToChat(conv.id) }.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = conv.otherUser?.avatarUrl ?: "",

                                    contentDescription = conv.otherUser?.displayName,
                                    modifier = Modifier.size(50.dp).clip(CircleShape).background(BgTertiary),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        conv.otherUser?.displayName ?: "Unknown",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = TextPrimary
                                    )
                                    Text(
                                        conv.lastMessagePreview ?: "Start a conversation",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMuted,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                if (conv.hasUnread) {
                                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(AccentPrimary))
                                }
                            }
                            HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)
                        }
                    }
                }  // TODO: cleanup
            }
        }
    }
}
