package com.kwen.app.data

import android.util.Log
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

private const val TAG = "KwenData"

// ─────────────────────────── Feed Posts ───────────────────────────

suspend fun fetchFeedPosts(limit: Int = 50): List<FeedPost> {
    return try {
        // 1. Fetch raw posts
        val rawPosts = supabase.from("posts")
            .select {
                order("created_at", Order.DESCENDING)
                limit(limit.toLong())
            }
            .decodeList<Post>()

        if (rawPosts.isEmpty()) return emptyList()

        val userIds = rawPosts.map { it.userId }.distinct()
        val postIds = rawPosts.map { it.id }

        // 2. Fetch profiles (batch)
        val profiles = try {
            if (userIds.isNotEmpty()) {
                supabase.from("profiles")
                    .select { filter { isIn("id", userIds) } }
                    .decodeList<Profile>()
            } else emptyList()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch profiles: ${e.message}")
            emptyList()
        }
        val profileMap = profiles.associateBy { it.id }

        // 3. Fetch media (batch)
        val media = try {
            if (postIds.isNotEmpty()) {
                supabase.from("post_media")
                    .select { filter { isIn("post_id", postIds) } }
                    .decodeList<PostMedia>()
            } else emptyList()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch post_media: ${e.message}")
            emptyList()
        }
        val mediaMap = media.groupBy { it.postId }

        // 4. Fetch current user's likes & saves
        val currentUserId = try {

            supabase.auth.currentSessionOrNull()?.user?.id ?: ""
        } catch (_: Exception) { "" }

        val likedPostIds = if (currentUserId.isNotEmpty()) {
            try {
                val likes = supabase.from("post_likes")
                    .select { filter { eq("user_id", currentUserId); isIn("post_id", postIds) } }
                    .decodeList<PostLike>()
                likes.map { it.postId }.toSet()
            } catch (e: Exception) {
                Log.w(TAG, "Failed to fetch likes: ${e.message}")
                emptySet()
            }
        } else emptySet()

        val savedPostIds = if (currentUserId.isNotEmpty()) {
            try {
                val saves = supabase.from("saved_posts")
                    .select { filter { eq("user_id", currentUserId); isIn("post_id", postIds) } }
                    .decodeList<SavedPost>()
                saves.map { it.postId }.toSet()
            } catch (e: Exception) {
                Log.w(TAG, "Failed to fetch saves: ${e.message}")
                emptySet()
            }
        } else emptySet()

        // 5. Combine into FeedPost list
        rawPosts.map { post ->
            val profile = profileMap[post.userId]
            FeedPost(
                id = post.id,
                userId = post.userId,
                content = post.content,
                location = post.location,
                createdAt = post.createdAt,
                likeCount = post.likeCount,
                commentCount = post.commentCount,
                saveCount = post.saveCount,
                shareCount = post.shareCount,
                isLiked = post.id in likedPostIds,
                isSaved = post.id in savedPostIds,
                displayName = profile?.displayName ?: "",
                username = profile?.username ?: "",
                avatarUrl = profile?.avatarUrl,
                isVerified = profile?.isVerified ?: false,
                media = mediaMap[post.id] ?: emptyList()
            )
        }
    } catch (e: Exception) {
        Log.e(TAG, "fetchFeedPosts failed: ${e.message}", e)
        emptyList()
    }
}

// ─────────────────────────── Explore Posts ───────────────────────────

suspend fun fetchExplorePosts(limit: Int = 100): List<ExplorePost> {
    return try {
        val rawPosts = supabase.from("posts")
            .select {
                order("created_at", Order.DESCENDING)
                limit(limit.toLong())
            }
            .decodeList<Post>()

        if (rawPosts.isEmpty()) return emptyList()

        val userIds = rawPosts.map { it.userId }.distinct()
        val postIds = rawPosts.map { it.id }

        val profiles = try {
            if (userIds.isNotEmpty()) {
                supabase.from("profiles")
                    .select { filter { isIn("id", userIds) } }
                    .decodeList<Profile>()
            } else emptyList()
        } catch (e: Exception) { Log.w(TAG, "Failed to fetch explore profiles: ${e.message}"); emptyList() }
        val profileMap = profiles.associateBy { it.id }

        val media = try {
            if (postIds.isNotEmpty()) {
                supabase.from("post_media")
                    .select { filter { isIn("post_id", postIds) } }
                    .decodeList<PostMedia>()
            } else emptyList()
        } catch (e: Exception) { Log.w(TAG, "Failed to fetch explore media: ${e.message}"); emptyList() }
        val mediaMap = media.groupBy { it.postId }

        rawPosts.map { post ->
            val profile = profileMap[post.userId]
            ExplorePost(
                id = post.id,
                userId = post.userId,
                content = post.content,
                createdAt = post.createdAt,
                likeCount = post.likeCount,
                commentCount = post.commentCount,
                displayName = profile?.displayName ?: "",
                username = profile?.username ?: "",
                avatarUrl = profile?.avatarUrl,
                media = mediaMap[post.id] ?: emptyList()
            )
        }
    } catch (e: Exception) {
        Log.e(TAG, "fetchExplorePosts failed: ${e.message}", e)
        emptyList()
    }
}

// ─────────────────────────── Single Post Detail ───────────────────────────

suspend fun fetchPostDetail(postId: String): FeedPost? {
    return try {
        val posts = supabase.from("posts")
            .select { filter { eq("id", postId) } }
            .decodeList<Post>()
        val post = posts.firstOrNull() ?: return null

        val profile = try {
            supabase.from("profiles")
                .select { filter { eq("id", post.userId) } }
                .decodeList<Profile>()
                .firstOrNull()
        } catch (_: Exception) { null }

        val media = try {
            supabase.from("post_media")
                .select { filter { eq("post_id", postId) } }
                .decodeList<PostMedia>()
        } catch (_: Exception) { emptyList() }

        val currentUserId = try {
            supabase.auth.currentSessionOrNull()?.user?.id ?: ""
        } catch (_: Exception) { "" }

        val isLiked = if (currentUserId.isNotEmpty()) {
            try {
                val likes = supabase.from("post_likes")
                    .select { filter { eq("post_id", postId); eq("user_id", currentUserId) } }
                    .decodeList<PostLike>()
                likes.isNotEmpty()
            } catch (_: Exception) { false }
        } else false

        val isSaved = if (currentUserId.isNotEmpty()) {
            try {
                val saves = supabase.from("saved_posts")
                    .select { filter { eq("post_id", postId); eq("user_id", currentUserId) } }
                    .decodeList<SavedPost>()
                saves.isNotEmpty()
            } catch (_: Exception) { false }
        } else false

        FeedPost(
            id = post.id,
            userId = post.userId,
            content = post.content,
            location = post.location,
            createdAt = post.createdAt,
            likeCount = post.likeCount,
            commentCount = post.commentCount,
            saveCount = post.saveCount,
            shareCount = post.shareCount,
            isLiked = isLiked,
            isSaved = isSaved,
            displayName = profile?.displayName ?: "",
            username = profile?.username ?: "",
            avatarUrl = profile?.avatarUrl,
            isVerified = profile?.isVerified ?: false,
            media = media
        )
    } catch (e: Exception) {
        Log.e(TAG, "fetchPostDetail failed for $postId: ${e.message}", e)
        null
    }
}

// ─────────────────────────── Comments ───────────────────────────

suspend fun fetchComments(postId: String): List<Comment> {
    return try {
        val rawComments = supabase.from("comments")
            .select {
                filter { eq("post_id", postId) }
                order("created_at", Order.ASCENDING)
                limit(50)
            }
            .decodeList<Comment>()

        // Comments already have display_name, username, avatar_url in the table
        // so no need for client-side joins
        rawComments
    } catch (e: Exception) {
        Log.e(TAG, "fetchComments failed for $postId: ${e.message}", e)
        emptyList()
    }
}

// ─────────────────────────── Conversations / Messages ───────────────────────────

suspend fun fetchConversations(): List<ConversationItem> {
    return try {
        val currentUserId = supabase.auth.currentSessionOrNull()?.user?.id ?: return emptyList()

        // 1. Get current user's conversation participants
        val myParticipants = try {
            supabase.from("conversation_participants")
                .select { filter { eq("user_id", currentUserId) } }
                .decodeList<ConversationParticipant>()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch conversation_participants: ${e.message}")
            return emptyList()
        }

        if (myParticipants.isEmpty()) return emptyList()

        val convIds = myParticipants.map { it.conversationId }

        // 2. Get ALL participants for these conversations (to find the "other" user)
        val allParticipants = try {
            supabase.from("conversation_participants")
                .select { filter { isIn("conversation_id", convIds) } }
                .decodeList<ConversationParticipant>()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch all participants: ${e.message}")
            emptyList()
        }

        // 3. Collect all other user IDs to batch-fetch profiles
        val otherUserIds = allParticipants
            .filter { it.userId != currentUserId }
            .map { it.userId }
            .distinct()

        val otherProfiles = if (otherUserIds.isNotEmpty()) {
            try {
                supabase.from("profiles")
                    .select { filter { isIn("id", otherUserIds) } }
                    .decodeList<Profile>()
            } catch (_: Exception) { emptyList() }
        } else emptyList()
        val profileMap = otherProfiles.associateBy { it.id }

        // 4. Get last message for each conversation
        val lastMessages = mutableMapOf<String, Message>()
        for (convId in convIds) {
            try {
                val msgs = supabase.from("messages")
                    .select {
                        filter { eq("conversation_id", convId) }
                        order("created_at", Order.DESCENDING)
                        limit(1)
                    }
                    .decodeList<Message>()
                if (msgs.isNotEmpty()) {
                    lastMessages[convId] = msgs.first()
                }
            } catch (e: Exception) { Log.w(TAG, "Failed to fetch last message for $convId: ${e.message}") }
        }

        // 5. Build ConversationItem list
        convIds.mapNotNull { convId ->
            val myP = myParticipants.firstOrNull { it.conversationId == convId } ?: return@mapNotNull null
            val otherP = allParticipants.firstOrNull {
                it.conversationId == convId && it.userId != currentUserId
            }
            val otherProfile = otherP?.userId?.let { profileMap[it] }
            val lastMsg = lastMessages[convId]

            ConversationItem(
                id = convId,
                lastMessageAt = lastMsg?.createdAt ?: "",
                lastMessagePreview = lastMsg?.content,
                lastMessageType = lastMsg?.messageType,
                hasUnread = myP.hasUnread,
                unreadCount = 0,
                otherUser = otherProfile
            )
        }.sortedByDescending { it.lastMessageAt }
    } catch (e: Exception) {
        Log.e(TAG, "fetchConversations failed: ${e.message}", e)
        emptyList()
    }
}

suspend fun fetchChatMessages(conversationId: String): List<Message> {
    return try {
        val currentUserId = supabase.auth.currentSessionOrNull()?.user?.id ?: ""

        val msgs = supabase.from("messages")

            .select {
                filter { eq("conversation_id", conversationId) }
                order("created_at", Order.ASCENDING)
                limit(100)
            }
            .decodeList<Message>()

        msgs.map { it.copy(isMine = it.senderId == currentUserId) }
    } catch (e: Exception) {
        Log.e(TAG, "fetchChatMessages failed: ${e.message}", e)
        emptyList()
    }
}

suspend fun fetchChatOtherUser(conversationId: String): Profile? {
    return try {
        val currentUserId = supabase.auth.currentSessionOrNull()?.user?.id ?: ""

        val participants = supabase.from("conversation_participants")
            .select {
                filter { eq("conversation_id", conversationId) }
            }
            .decodeList<ConversationParticipant>()

        val other = participants.firstOrNull { it.userId != currentUserId } ?: return null

        try {
            supabase.from("profiles")
                .select { filter { eq("id", other.userId) } }
                .decodeList<Profile>()
                .firstOrNull()
        } catch (_: Exception) { null }
    } catch (e: Exception) {
        Log.e(TAG, "fetchChatOtherUser failed: ${e.message}", e)
        null
    }
}

// ─────────────────────────── Profile ───────────────────────────

suspend fun fetchProfileByUsername(username: String): Profile? {
    return try {
        val profiles = supabase.from("profiles")
            .select { filter { eq("username", username) } }
            .decodeList<Profile>()
        profiles.firstOrNull()
    } catch (e: Exception) {
        Log.e(TAG, "fetchProfileByUsername failed for $username: ${e.message}", e)
        null
    }
}

suspend fun fetchProfileById(userId: String): Profile? {
    return try {
        supabase.from("profiles")
            .select { filter { eq("id", userId) } }
            .decodeList<Profile>()
            .firstOrNull()
    } catch (e: Exception) {
        Log.e(TAG, "fetchProfileById failed for $userId: ${e.message}", e)
        null
    }
}

suspend fun fetchPostsByUser(userId: String): List<FeedPost> {
    return try {
        val rawPosts = supabase.from("posts")
            .select {
                filter { eq("user_id", userId) }
                order("created_at", Order.DESCENDING)
            }
            .decodeList<Post>()

        if (rawPosts.isEmpty()) return emptyList()

        val postIds = rawPosts.map { it.id }

        val profile = try {
            supabase.from("profiles")
                .select { filter { eq("id", userId) } }
                .decodeList<Profile>()
                .firstOrNull()
        } catch (_: Exception) { null }

        val media = try {
            supabase.from("post_media")
                .select { filter { isIn("post_id", postIds) } }
                .decodeList<PostMedia>()
        } catch (_: Exception) { emptyList() }
        val mediaMap = media.groupBy { it.postId }

        rawPosts.map { post ->
            FeedPost(
                id = post.id,
                userId = post.userId,
                content = post.content,
                location = post.location,
                createdAt = post.createdAt,
                likeCount = post.likeCount,
                commentCount = post.commentCount,
                saveCount = post.saveCount,
                shareCount = post.shareCount,
                displayName = profile?.displayName ?: "",
                username = profile?.username ?: "",
                avatarUrl = profile?.avatarUrl,
                isVerified = profile?.isVerified ?: false,
                media = mediaMap[post.id] ?: emptyList()
            )
        }
    } catch (e: Exception) {
        Log.e(TAG, "fetchPostsByUser failed for $userId: ${e.message}", e)
        emptyList()
    }
}

// ─────────────────────────── Saved Posts ───────────────────────────

suspend fun fetchSavedPosts(): List<FeedPost> {
    return try {
        val currentUserId = supabase.auth.currentSessionOrNull()?.user?.id ?: return emptyList()

        val saved = supabase.from("saved_posts")
            .select {
                filter { eq("user_id", currentUserId) }
                order("created_at", Order.DESCENDING)
            }
            .decodeList<SavedPost>()

        if (saved.isEmpty()) return emptyList()

        val postIds = saved.map { it.postId }

        // Fetch actual posts
        val rawPosts = try {
            supabase.from("posts")
                .select { filter { isIn("id", postIds) } }
                .decodeList<Post>()
        } catch (_: Exception) { emptyList() }

        if (rawPosts.isEmpty()) return emptyList()

        val userIds = rawPosts.map { it.userId }.distinct()

        val profiles = try {
            if (userIds.isNotEmpty()) {
                supabase.from("profiles")
                    .select { filter { isIn("id", userIds) } }
                    .decodeList<Profile>()
            } else emptyList()
        } catch (_: Exception) { emptyList() }
        val profileMap = profiles.associateBy { it.id }

        val media = try {
            if (postIds.isNotEmpty()) {
                supabase.from("post_media")
                    .select { filter { isIn("post_id", postIds) } }
                    .decodeList<PostMedia>()
            } else emptyList()
        } catch (_: Exception) { emptyList() }
        val mediaMap = media.groupBy { it.postId }

        rawPosts.map { post ->
            val profile = profileMap[post.userId]
            FeedPost(
                id = post.id,
                userId = post.userId,
                content = post.content,
                location = post.location,
                createdAt = post.createdAt,
                likeCount = post.likeCount,
                commentCount = post.commentCount,
                saveCount = post.saveCount,
                shareCount = post.shareCount,
                isLiked = false,
                isSaved = true,
                displayName = profile?.displayName ?: "",
                username = profile?.username ?: "",
                avatarUrl = profile?.avatarUrl,
                isVerified = profile?.isVerified ?: false,
                media = mediaMap[post.id] ?: emptyList()
            )
        }
    } catch (e: Exception) {
        Log.e(TAG, "fetchSavedPosts failed: ${e.message}", e)
        emptyList()
    }
}

// ─────────────────────────── Stories ───────────────────────────

suspend fun fetchStories(userId: String? = null): List<Story> {
    return try {
        val result = supabase.from("stories")
            .select {
                if (userId != null) {
                    filter {
                        eq("user_id", userId)
                        gt("expires_at", java.time.Instant.now().toString())
                    }
                } else {
                    filter { gt("expires_at", java.time.Instant.now().toString()) }
                }
                order("created_at", Order.DESCENDING)
                limit(20)
            }
            .decodeList<Story>()
        // Fetch user profiles for stories
        val userIds = result.map { it.userId }.distinct()
        val profiles = if (userIds.isNotEmpty()) {
            try {
                supabase.from("profiles")
                    .select { filter { isIn("id", userIds) } }
                    .decodeList<Profile>()
            } catch (_: Exception) { emptyList() }
        } else emptyList()
        val profileMap = profiles.associateBy { it.id }

        result.map { story ->
            story.copy(user = profileMap[story.userId])
        }
    } catch (e: Exception) {
        Log.e(TAG, "fetchStories failed: ${e.message}", e)
        emptyList()
    }
}

// ─────────────────────────── Notifications ───────────────────────────

suspend fun fetchNotifications(): List<Notification> {
    return try {
        val userId = supabase.auth.currentSessionOrNull()?.user?.id ?: return emptyList()
        supabase.from("notifications")
            .select {
                filter { eq("user_id", userId) }
                order("created_at", Order.DESCENDING)
                limit(50)
            }
            .decodeList<Notification>()
    } catch (e: Exception) {
        Log.e(TAG, "fetchNotifications failed: ${e.message}", e)
        emptyList()
    }
}
