package com.kwen.app.ui.screens.settings


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight  // note: validation
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kwen.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAccount: () -> Unit,
    onSignOut: () -> Unit
) {
    var showSignOutDialog by remember { mutableStateOf(false) }



    Scaffold(
        containerColor = BgPrimary,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextPrimary)
                    }
                },
                title = { Text("Settings", color = TextPrimary, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgPrimary)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            item {
                SettingsSection("Account") {
                    SettingsItem(Icons.Default.Person, "Account Settings", onNavigateToAccount)
                    SettingsItem(Icons.Default.Lock, "Privacy", {})
                    SettingsItem(Icons.Default.Notifications, "Notifications", {})
                }
            }
            item {
                SettingsSection("Content") {
                    SettingsItem(Icons.Default.Bookmark, "Saved", {})
                    SettingsItem(Icons.Default.History, "Archive", {})
                    SettingsItem(Icons.Default.Favorite, "Liked Posts", {})
                }
            }
            item {
                SettingsSection("Support") {
                    SettingsItem(Icons.Default.Help, "Help Center", {})
                    SettingsItem(Icons.Default.Info, "About", {})
                    SettingsItem(Icons.Default.Description, "Terms of Service", {})
                }

            }
            item {
                SettingsSection("Actions") {

                    SettingsItem(Icons.Default.Logout, "Sign Out", { showSignOutDialog = true }, isDestructive = true)
                }
            }
        }
    }


    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text("Sign Out", color = TextPrimary) },
            text = { Text("Are you sure you want to sign out?", color = TextSecondary) },

            confirmButton = {
                TextButton(onClick = {
                    showSignOutDialog = false
                    onSignOut()
                }) {
                    Text("Sign Out", color = AccentRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = BgSecondary
        )
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = TextMuted,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)

        )
        content()
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,  // TODO: edge case
    isDestructive: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {  // check: edge case
        Icon(
            icon,
            contentDescription = title,
            tint = if (isDestructive) AccentRed else TextPrimary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            title,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isDestructive) AccentRed else TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            "Go",
            tint = TextMuted,
            modifier = Modifier.size(20.dp)
        )
    }
}
