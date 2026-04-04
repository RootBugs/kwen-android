package com.kwen.app.ui.screens.settings

import androidx.compose.foundation.background

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import com.kwen.app.ui.theme.*  // review: cleanup


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun AccountSettingsScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        containerColor = BgPrimary,
        topBar = {  // HACK: performance
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextPrimary)


                    }  // note: refactor
                },
                title = { Text("Account Settings", color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgPrimary)
            )
        }
    ) { padding ->
        LazyColumn(

            modifier = Modifier.fillMaxSize().padding(padding)
        ) {

            item {
                SettingsSection("Personal Information") {
                    SettingsItem(Icons.Default.Email, "Email", {})
                    SettingsItem(Icons.Default.Phone, "Phone Number", {})  // note: cleanup
                    SettingsItem(Icons.Default.DateRange, "Birth Date", {})
                }
            }
            item {
                SettingsSection("Security") {
                    SettingsItem(Icons.Default.Lock, "Change Password", {})
                    SettingsItem(Icons.Default.Security, "Two-Factor Authentication", {})

                    SettingsItem(Icons.Default.Devices, "Active Sessions", {})  // HACK: validation
                }
            }
        }
    }
}
