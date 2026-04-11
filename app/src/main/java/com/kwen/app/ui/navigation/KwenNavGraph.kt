package com.kwen.app.ui.navigation


import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kwen.app.data.AuthViewModel
import com.kwen.app.data.supabase
import io.github.jan.supabase.auth.auth
import com.kwen.app.ui.screens.auth.LoginScreen
import com.kwen.app.ui.screens.auth.RegisterScreen
import com.kwen.app.ui.screens.auth.CompleteProfileScreen
import com.kwen.app.ui.screens.feed.FeedScreen
import com.kwen.app.ui.screens.explore.ExploreScreen
import com.kwen.app.ui.screens.create.CreateScreen
import com.kwen.app.ui.screens.messages.MessagesScreen
import com.kwen.app.ui.screens.messages.ChatScreen
import com.kwen.app.ui.screens.profile.ProfileScreen
import com.kwen.app.ui.screens.profile.EditProfileScreen
import com.kwen.app.ui.screens.post.PostDetailScreen
import com.kwen.app.ui.screens.notifications.NotificationsScreen
import com.kwen.app.ui.screens.settings.SettingsScreen
import com.kwen.app.ui.screens.saved.SavedScreen
import com.kwen.app.ui.screens.stories.StoryViewerScreen
import com.kwen.app.ui.screens.stories.StoriesScreen
import com.kwen.app.ui.screens.stories.CreateStoryScreen
import com.kwen.app.ui.screens.reels.ReelsScreen
import com.kwen.app.ui.screens.communities.CommunitiesScreen
import com.kwen.app.ui.theme.*

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val COMPLETE_PROFILE = "complete_profile"
    const val FEED = "feed"
    const val EXPLORE = "explore"
    const val CREATE = "create"
    const val MESSAGES = "messages"
    const val CHAT = "chat/{conversationId}"
    const val PROFILE = "profile/{username}"
    const val OWN_PROFILE = "own_profile"

    const val NOTIFICATIONS = "notifications"
    const val POST = "post/{postId}"
    const val SETTINGS = "settings"
    const val SAVED = "saved"
    const val STORIES = "stories/{userId}"
    const val CREATE_STORY = "create_story"
    const val EDIT_PROFILE = "edit_profile"
    const val REELS = "reels"
    const val COMMUNITIES = "communities"

    fun chat(id: String) = "chat/$id"
    fun profile(name: String) = "profile/$name"
    fun post(id: String) = "post/$id"
    fun stories(id: String) = "stories/$id"
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Routes.FEED, "Home", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(Routes.EXPLORE, "Explore", Icons.Filled.Search, Icons.Outlined.Search),
    BottomNavItem(Routes.CREATE, "Create", Icons.Filled.AddCircle, Icons.Outlined.AddCircle),
    BottomNavItem(Routes.MESSAGES, "Messages", Icons.Outlined.MailOutline, Icons.Outlined.MailOutline),
    BottomNavItem(Routes.OWN_PROFILE, "Profile", Icons.Filled.Person, Icons.Outlined.Person)
)

@Composable
fun KwenNavGraph(
    authViewModel: AuthViewModel,
    navController: NavHostController = rememberNavController()
) {
    val authState by authViewModel.authState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()  // FIXME: performance
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Routes.FEED, Routes.EXPLORE, Routes.CREATE, Routes.MESSAGES, Routes.OWN_PROFILE
    )

    val startDestination = if (authState.isLoggedIn) Routes.FEED else Routes.LOGIN

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = BgPrimary) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(Routes.FEED) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AccentPrimary,
                                selectedTextColor = AccentPrimary,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted,
                                indicatorColor = BgTertiary

                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(
                    authViewModel = authViewModel,
                    onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                    onNavigateToFeed = {
                        navController.navigate(Routes.FEED) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.REGISTER) {
                RegisterScreen(

                    authViewModel = authViewModel,
                    onNavigateToLogin = { navController.popBackStack() },
                    onNavigateToFeed = {
                        navController.navigate(Routes.FEED) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.COMPLETE_PROFILE) {
                CompleteProfileScreen(
                    authViewModel = authViewModel,
                    onNavigateToFeed = {
                        navController.navigate(Routes.FEED) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.FEED) {
                val uid = supabase.auth.currentSessionOrNull()?.user?.id ?: ""
                FeedScreen(
                    currentUserId = uid,
                    onNavigateToMessages = {
                        navController.navigate(Routes.MESSAGES) {
                            popUpTo(Routes.FEED) { saveState = true }
                        }
                    },
                    onNavigateToPost = { navController.navigate(Routes.post(it)) },
                    onNavigateToProfile = { navController.navigate(Routes.profile(it)) },
                    onNavigateToNotifications = { navController.navigate(Routes.NOTIFICATIONS) },
                    onNavigateToStories = { navController.navigate(Routes.stories(it)) }
                )
            }

            composable(Routes.EXPLORE) {
                ExploreScreen(
                    onNavigateToPost = { navController.navigate(Routes.post(it)) },
                    onNavigateToProfile = { navController.navigate(Routes.profile(it)) },
                    onNavigateToMessages = {
                        navController.navigate(Routes.MESSAGES) {
                            popUpTo(Routes.FEED) { saveState = true }

                        }
                    },
                    onNavigateToNotifications = { navController.navigate(Routes.NOTIFICATIONS) }
                )
            }

            composable(Routes.CREATE) {
                CreateScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onPostCreated = { navController.popBackStack() }
                )
            }

            composable(Routes.MESSAGES) {
                MessagesScreen(
                    onNavigateToChat = { navController.navigate(Routes.chat(it)) },
                    onNavigateToProfile = { navController.navigate(Routes.profile(it)) }
                )
            }

            composable(
                route = Routes.CHAT,
                arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
            ) { backStackEntry ->
                val conversationId = backStackEntry.arguments?.getString("conversationId") ?: return@composable
                ChatScreen(
                    conversationId = conversationId,
                    onBack = { navController.popBackStack() },
                    onNavigateToProfile = { navController.navigate(Routes.profile(it)) }
                )
            }

            composable(Routes.OWN_PROFILE) {
                val uid = supabase.auth.currentSessionOrNull()?.user?.id ?: ""
                ProfileScreen(
                    username = null,
                    currentUserId = uid,
                    onBack = { navController.popBackStack() },
                    onNavigateToPost = { navController.navigate(Routes.post(it)) },
                    onNavigateToEdit = { navController.navigate(Routes.EDIT_PROFILE) },
                    onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
                    onNavigateToSaved = { navController.navigate(Routes.SAVED) },
                    onNavigateToChat = { _, _, _ -> },
                    onNavigateToStory = { navController.navigate(Routes.stories(it)) }
                )
            }

            composable(
                route = Routes.PROFILE,
                arguments = listOf(navArgument("username") { type = NavType.StringType })
            ) { backStackEntry ->
                val username = backStackEntry.arguments?.getString("username") ?: return@composable
                val uid = supabase.auth.currentSessionOrNull()?.user?.id ?: ""
                ProfileScreen(
                    username = username,
                    currentUserId = uid,
                    onBack = { navController.popBackStack() },
                    onNavigateToPost = { navController.navigate(Routes.post(it)) },
                    onNavigateToEdit = { },
                    onNavigateToSettings = { },
                    onNavigateToSaved = { },
                    onNavigateToChat = { _, _, _ -> },
                    onNavigateToStory = { navController.navigate(Routes.stories(it)) }
                )

            }

            composable(Routes.NOTIFICATIONS) {
                NotificationsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToPost = { navController.navigate(Routes.post(it)) },
                    onNavigateToProfile = { navController.navigate(Routes.profile(it)) }
                )
            }


            composable(
                route = Routes.POST,
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId") ?: return@composable
                PostDetailScreen(
                    postId = postId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProfile = { navController.navigate(Routes.profile(it)) }
                )
            }

            composable(Routes.SETTINGS) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAccount = { },
                    onSignOut = {
                        authViewModel.signOut()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.SAVED) {
                SavedScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToPost = { navController.navigate(Routes.post(it)) },
                    onNavigateToProfile = { navController.navigate(Routes.profile(it)) }
                )
            }

            composable(
                route = Routes.STORIES,
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
                StoryViewerScreen(
                    userId = userId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Routes.CREATE_STORY) {
                CreateStoryScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onStoryCreated = { navController.popBackStack() }
                )
            }

            composable(Routes.EDIT_PROFILE) {
                EditProfileScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Routes.REELS) {
                ReelsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProfile = { navController.navigate(Routes.profile(it)) }
                )
            }

            composable(Routes.COMMUNITIES) {
                CommunitiesScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
