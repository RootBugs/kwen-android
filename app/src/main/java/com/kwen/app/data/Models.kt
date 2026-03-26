package com.kwen.app.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String,
    val username: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    val bio: String? = null,
    val website: String? = null,
    @SerialName("is_verified") val isVerified: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("follower_count") val followerCount: Int = 0,
    @SerialName("following_count") val followingCount: Int = 0,
    @SerialName("post_count") val postCount: Int = 0,
    @SerialName("is_following") val isFollowing: Boolean = false
)

@Serializable
data class PostMedia(
    val id: String = "",
    @SerialName("post_id") val postId: String = "",
    @SerialName("storage_path") val storagePath: String = "",
    @SerialName("media_type") val mediaType: String = "image",
    @SerialName("sort_order") val sortOrder: Int = 0
)

@Serializable
data class FeedPost(
    val id: String,
    @SerialName("user_id") val userId: String,
    val content: String? = null,
    val location: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("like_count") val likeCount: Int = 0,
    @SerialName("comment_count") val commentCount: Int = 0,
    @SerialName("save_count") val saveCount: Int = 0,
    @SerialName("share_count") val shareCount: Int = 0,
    @SerialName("is_liked") val isLiked: Boolean = false,
    @SerialName("is_saved") val isSaved: Boolean = false,
    @SerialName("display_name") val displayName: String,
    val username: String,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("is_verified") val isVerified: Boolean = false,
    val media: List<PostMedia> = emptyList()
)

@Serializable
data class Post(
    val id: String = "",
    @SerialName("user_id") val userId: String = "",
    val content: String? = null,
    val location: String? = null,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = "",
    @SerialName("like_count") val likeCount: Int = 0,
    @SerialName("comment_count") val commentCount: Int = 0,

    @SerialName("save_count") val saveCount: Int = 0,
    @SerialName("share_count") val shareCount: Int = 0
)

@Serializable
data class Comment(
    val id: String,
    @SerialName("post_id") val postId: String,
    @SerialName("user_id") val userId: String,
    val content: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("display_name") val displayName: String,
    val username: String,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("is_verified") val isVerified: Boolean = false,
    @SerialName("like_count") val likeCount: Int = 0,
    @SerialName("is_liked") val isLiked: Boolean = false
)

@Serializable
data class Story(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("media_url") val mediaUrl: String,
    @SerialName("media_type") val mediaType: String = "image",
    @SerialName("expires_at") val expiresAt: String,
    @SerialName("created_at") val createdAt: String,
    val user: Profile? = null
)

@Serializable
data class StoryUser(
    val id: String,
    val username: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("has_unseen_story") val hasUnseenStory: Boolean = false,
    val stories: List<Story> = emptyList()
)

@Serializable
data class Conversation(
    val id: String,
    @SerialName("last_message_at") val lastMessageAt: String = "",
    val other: Profile? = null
)

@Serializable
data class ConversationParticipant(
    val id: String,
    @SerialName("conversation_id") val conversationId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("has_unread") val hasUnread: Boolean = false,
    val profile: Profile? = null
)

@Serializable
data class ConversationItem(
    val id: String,
    @SerialName("last_message_at") val lastMessageAt: String = "",
    @SerialName("last_message_preview") val lastMessagePreview: String? = null,
    @SerialName("last_message_type") val lastMessageType: String? = null,
    @SerialName("has_unread") val hasUnread: Boolean = false,
    @SerialName("unread_count") val unreadCount: Int = 0,
    @SerialName("other_user") val otherUser: Profile? = null
)

@Serializable
data class Message(
    val id: String = "",
    @SerialName("conversation_id") val conversationId: String = "",
    @SerialName("sender_id") val senderId: String = "",
    val content: String = "",
    @SerialName("message_type") val messageType: String = "text",
    @SerialName("media_url") val mediaUrl: String? = null,
    @SerialName("is_seen") val isSeen: Boolean = false,
    @SerialName("is_mine") val isMine: Boolean = false,

    @SerialName("reply_to") val replyTo: ReplyTo? = null,
    val sender: Profile? = null,
    @SerialName("created_at") val createdAt: String = ""
)

@Serializable
data class ReplyTo(
    val id: String = "",
    @SerialName("sender_name") val senderName: String = "",
    val content: String = ""
)

@Serializable
data class Notification(
    val id: String,
    @SerialName("user_id") val userId: String = "",
    val type: String,
    @SerialName("actor_id") val actorId: String = "",
    @SerialName("actor_display_name") val actorDisplayName: String = "",
    @SerialName("actor_username") val actorUsername: String = "",
    @SerialName("actor_avatar_url") val actorAvatarUrl: String? = null,
    @SerialName("post_id") val postId: String? = null,
    @SerialName("is_read") val isRead: Boolean = false,
    @SerialName("created_at") val createdAt: String
)

@Serializable
data class Follow(
    val id: String = "",
    @SerialName("follower_id") val followerId: String = "",
    @SerialName("following_id") val followingId: String = ""
)

@Serializable
data class SavedPost(
    val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("post_id") val postId: String = "",
    @SerialName("created_at") val createdAt: String = ""
)

@Serializable
data class PostLike(
    val id: String = "",
    @SerialName("post_id") val postId: String = "",
    @SerialName("user_id") val userId: String = ""
)

@Serializable
data class ExplorePost(
    val id: String,

    @SerialName("user_id") val userId: String,
    val content: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("like_count") val likeCount: Int = 0,
    @SerialName("comment_count") val commentCount: Int = 0,
    @SerialName("display_name") val displayName: String,
    val username: String,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    val media: List<PostMedia> = emptyList()
)

@Serializable
data class TrendingTag(
    val tag: String,
    @SerialName("post_count") val postCount: Int
)

@Serializable
data class SuggestedUser(
    val id: String,
    val username: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("is_verified") val isVerified: Boolean = false,
    @SerialName("follower_count") val followerCount: Int = 0
)

@Serializable
data class UserSettings(
    @SerialName("user_id") val userId: String,
    @SerialName("push_notifications") val pushNotifications: Boolean = true,
    @SerialName("dark_mode") val darkMode: Boolean = true,
    @SerialName("language") val language: String = "en"
)
