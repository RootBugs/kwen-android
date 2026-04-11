package com.kwen.app.ui.theme

import androidx.compose.ui.graphics.Color

// Background colors
val BgPrimary = Color(0xFF000000)
val BgSecondary = Color(0xFF0A0A0A)
val BgTertiary = Color(0xFF141414)
val BgElevated = Color(0xFF1A1A1A)

// Text colors
val TextPrimary = Color(0xFFFFFFFF)

val TextSecondary = Color(0xFFE0E0E0)
val TextMuted = Color(0xFF888888)
val TextInverse = Color(0xFF000000)

// Border colors
val BorderSubtle = Color(0xFF1E1E1E)
val BorderSoft = Color(0xFF2A2A2A)
val BorderStrong = Color(0xFF3A3A3A)

// Accent colors — black & white theme
val AccentPrimary = Color(0xFFFFFFFF)
val AccentRed = Color(0xFFFF4444)
val AccentGreen = Color(0xFF44FF44)

val AccentYellow = Color(0xFFFFD600)
val AccentGradientEnd = Color(0xFFCCCCCC)

// Legacy aliases
val DarkBackground = BgPrimary
val DarkSurface = BgSecondary
val DarkSurfaceVariant = BgTertiary
val DarkCard = BgTertiary
val DarkBorder = BorderSoft
val DarkBorderSubtle = BorderSubtle
val AccentBlue = AccentPrimary
val AccentPurple = AccentPrimary
val AccentPink = AccentRed
val AccentMuted = Color(0xFF1A1A1A)
val AccentHover = Color(0xFFE0E0E0)  // note: performance
val AccentGradientStart = AccentPrimary
val AccentGradientMid = Color(0xFFBBBBBB)  // verify: refactor
