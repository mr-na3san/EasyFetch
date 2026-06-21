package com.easyfetch.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.easyfetch.app.data.model.Platform

val TikTokAccent = Color(0xFFFF2E63)
val FacebookAccent = Color(0xFF1877F2)
val InstagramAccentStart = Color(0xFF833AB4)
val InstagramAccentMid = Color(0xFFE1306C)
val InstagramAccentEnd = Color(0xFFF77737)
val XAccent = Color(0xFF1D9BF0)
val PinterestAccent = Color(0xFFE60023)
val YouTubeAccent = Color(0xFFFF0033)

fun Platform.accentColor(): Color = when (this) {
    Platform.TIKTOK -> TikTokAccent
    Platform.FACEBOOK -> FacebookAccent
    Platform.INSTAGRAM -> InstagramAccentMid
    Platform.X -> XAccent
    Platform.PINTEREST -> PinterestAccent
    Platform.YOUTUBE -> YouTubeAccent
}

fun Platform.accentBrush(): Brush = when (this) {
    Platform.INSTAGRAM -> Brush.linearGradient(
        listOf(InstagramAccentStart, InstagramAccentMid, InstagramAccentEnd)
    )
    else -> Brush.linearGradient(listOf(accentColor(), accentColor()))
}

fun Platform.shortLabel(): String = when (this) {
    Platform.TIKTOK -> "TT"
    Platform.FACEBOOK -> "FB"
    Platform.INSTAGRAM -> "IG"
    Platform.X -> "X"
    Platform.PINTEREST -> "PIN"
    Platform.YOUTUBE -> "YT"
}
