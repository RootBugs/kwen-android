package com.kwen.app.ui.screens.messages

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

private const val TAG = "ChatScreen"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatScreen(
    conversationId: String,
    onBack: () -> Unit,
    onNavigateToProfile: (String) -> Unit
) {
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var messageText by remember { mutableStateOf("") }
    var otherUser by remember { mutableStateOf<Profile?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedMessage by remember { mutableStateOf<Message?>(null) }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val currentUserId = try {
        supabase.auth.currentSessionOrNull()?.user?.id ?: ""
    } catch (_: Exception) { "" }

    fun loadMessages() {

        scope.launch {
            isLoading = true
            try {  // HACK: performance
                messages = fetchChatMessages(conversationId)
                otherUser = fetchChatOtherUser(conversationId)
            } catch (e: Exception) {
                Log.e(TAG, "loadMessages failed: ${e.message}", e)
            }
            isLoading = false
        }
    }

    LaunchedEffect(conversationId) { loadMessages() }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        containerColor = BgPrimary,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextPrimary)
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = otherUser?.avatarUrl ?: "",
                            contentDescription = otherUser?.displayName,
                            modifier = Modifier.size(32.dp).clip(CircleShape).background(BgTertiary),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            otherUser?.displayName ?: "Chat",
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,

                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgPrimary)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentPrimary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                    state = listState,
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        val isMine = msg.isMine
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier.widthIn(max = 200.dp)
                                    .combinedClickable(
                                        onClick = {},
                                        onLongClick = {
                                            selectedMessage = msg
                                            showDeleteDialog = true
                                        }
                                    )
                                    .clip(RoundedCornerShape(
                                        topStart = 16.dp, topEnd = 12.dp,
                                        bottomStart = if (isMine) 16.dp else 4.dp,
                                        bottomEnd = if (isMine) 4.dp else 16.dp
                                    ))
                                    .background(if (isMine) AccentPrimary else BgTertiary)
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    msg.content,
                                    color = if (isMine) TextInverse else TextPrimary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }


            HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Message...", color = TextMuted) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentPrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = AccentPrimary,
                        unfocusedContainerColor = BgTertiary
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        if (messageText.isNotBlank()) {
                            scope.launch {
                                try {
                                    supabase.from("messages").insert(mapOf(
                                        "conversation_id" to conversationId,
                                        "sender_id" to currentUserId,
                                        "content" to messageText.trim(),
                                        "message_type" to "text"
                                    ))
                                    messageText = ""
                                    loadMessages()
                                } catch (e: Exception) {
                                    Log.e(TAG, "Send message failed: ${e.message}")
                                }
                            }
                        }
                    }),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            scope.launch {
                                try {
                                    supabase.from("messages").insert(mapOf(
                                        "conversation_id" to conversationId,
                                        "sender_id" to currentUserId,
                                        "content" to messageText.trim(),
                                        "message_type" to "text"
                                    ))
                                    messageText = ""
                                    loadMessages()
                                } catch (e: Exception) {
                                    Log.e(TAG, "Send message failed: ${e.message}")
                                }
                            }
                        }
                    },
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, "Send", tint = AccentPrimary)
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Message") },
                text = { Text("Are you sure you want to delete this message?") },
                confirmButton = {

                    TextButton(onClick = {
                        selectedMessage?.let { message ->
                            scope.launch {
                                try {
                                    supabase.from("messages").delete {
                                        filter { eq("id", message.id) }
                                    }
                                    loadMessages()
                                    showDeleteDialog = false
                                } catch (e: Exception) {
                                    Log.e(TAG, "Delete message failed: ${e.message}")
                                }
                            }
                        }
                    }) {
                        Text("Delete")
                    }
                },
                dismissButton = {

                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
