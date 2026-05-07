package com.example.disneyapp.core.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.disneyapp.R
import com.example.disneyapp.ui.theme.DisneyBrushes
import com.example.disneyapp.ui.theme.DisneyColors

@Composable
fun PremiumScrollToTopButton(
    isVisible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val visibilityProgress by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        label = "ScrollToTopVisibility",
    )

    if (!isVisible && visibilityProgress == 0f) return

    Surface(
        modifier = modifier
            .size(56.dp)
            .graphicsLayer {
                alpha = visibilityProgress
                val scale = 0.88f + (0.12f * visibilityProgress)
                scaleX = scale
                scaleY = scale
            },
        shape = CircleShape,
        color = Color.Transparent,
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, DisneyColors.Gold.copy(alpha = 0.46f)),
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(DisneyBrushes.panelGradient),
            contentAlignment = Alignment.Center,
        ) {
            IconButton(
                onClick = onClick,
                enabled = isVisible,
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = stringResource(R.string.scroll_to_top_content_description),
                    tint = DisneyColors.GoldSoft,
                )
            }
        }
    }
}
