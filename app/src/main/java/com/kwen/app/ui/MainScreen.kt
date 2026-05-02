package com.kwen.app.ui

import androidx.compose.foundation.layout.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kwen.app.ui.theme.*
import com.kwen.app.data.AuthViewModel
import com.kwen.app.ui.screens.create.CreateScreen
import com.kwen.app.ui.screens.explore.ExploreScreen
import com.kwen.app.ui.screens.feed.FeedScreen
import com.kwen.app.ui.screens.profile.ProfileScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    authViewModel: AuthViewModel,
    onNavigateToMessages: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToPost: (String) -> Unit,
    onNavigateToCreate: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val authState by authViewModel.authState.collectAsState()
    val currentUserId = authState.userId ?: return

    val tabs = listOf(
        Icons.Filled.Home to Icons.Outlined.Home,
        Icons.Filled.Search to Icons.Outlined.Search,

        Icons.Filled.AddBox to Icons.Outlined.AddBox,
        Icons.Filled.Person to Icons.Outlined.Person
    )

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Black,
        topBar = {
            if (selectedTab != 2) {
                TopAppBar(
                    title = {
                        Text(
                            text = "Kwen",
                            color = AccentPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            letterSpacing = 1.sp
                        )
                    },
                    actions = {
                        IconButton(onClick = onNavigateToMessages) {
                            Icon(Icons.Outlined.Email, "Messages", tint = androidx.compose.ui.graphics.Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = androidx.compose.ui.graphics.Color.Black)
                )
            }
        },
        bottomBar = {
            NavigationBar(containerColor = androidx.compose.ui.graphics.Color.Black) {
                tabs.forEachIndexed { index, (selected, unselected) ->
                    NavigationBarItem(
                        icon = { Icon(if (selectedTab == index) selected else unselected, null) },
                        label = null,
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = androidx.compose.ui.graphics.Color.White,
                            unselectedIconColor = androidx.compose.ui.graphics.Color(0xFF888888),
                            indicatorColor = androidx.compose.ui.graphics.Color.Transparent
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (selectedTab) {
                0 -> FeedScreen(
                    currentUserId = currentUserId,
                    onNavigateToMessages = onNavigateToMessages,
                    onNavigateToPost = onNavigateToPost,
                    onNavigateToProfile = onNavigateToProfile
                )
                1 -> ExploreScreen(
                    onNavigateToPost = onNavigateToPost,
                    onNavigateToProfile = onNavigateToProfile
                )
                2 -> CreateScreen(
                    onNavigateBack = { selectedTab = 0 },
                    onPostCreated = { selectedTab = 0 }
                )
                3 -> ProfileScreen(

                    username = null,
                    currentUserId = currentUserId,
                    onBack = { selectedTab = 0 },
                    onNavigateToPost = onNavigateToPost
                )
            }
        }
    }
}
