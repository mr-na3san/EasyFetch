package com.easyfetch.app.ui.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.easyfetch.app.data.model.Platform
import com.easyfetch.app.ui.theme.MidnightDeep
import com.easyfetch.app.ui.theme.accentBrush
import com.easyfetch.app.ui.theme.shortLabel

@Composable
fun PlatformRail(
    detectedPlatform: Platform?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Platform.values().forEach { platform ->
            val isActive = platform == detectedPlatform
            val scale by animateFloatAsState(if (isActive) 1.15f else 1f, label = "platformScale")
            val alpha by animateFloatAsState(
                if (detectedPlatform == null || isActive) 1f else 0.35f,
                label = "platformAlpha"
            )
            val ringSize by animateDpAsState(if (isActive) 38.dp else 32.dp, label = "platformSize")

            Box(
                modifier = Modifier
                    .size(ringSize)
                    .scale(scale)
                    .alpha(alpha)
                    .clip(CircleShape)
                    .background(platform.accentBrush()),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = platform.shortLabel(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MidnightDeep
                )
            }
        }
    }
}
