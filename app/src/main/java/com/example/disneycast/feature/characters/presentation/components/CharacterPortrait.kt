package com.example.disneycast.feature.characters.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import com.example.disneycast.ui.theme.DisneyBrushes
import com.example.disneycast.ui.theme.DisneyColors

enum class CharacterPortraitVariant(
    val contentPadding: Dp,
    val fallbackMedallionSize: Dp,
    val loadingMedallionSize: Dp,
) {
    Compact(
        contentPadding = 14.dp,
        fallbackMedallionSize = 84.dp,
        loadingMedallionSize = 64.dp,
    ),
    Hero(
        contentPadding = 22.dp,
        fallbackMedallionSize = 124.dp,
        loadingMedallionSize = 96.dp,
    ),
}

@Composable
fun CharacterPortrait(
    name: String,
    imageUrl: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    variant: CharacterPortraitVariant = CharacterPortraitVariant.Compact,
) {
    if (imageUrl.isNullOrBlank()) {
        CharacterPortraitFallback(
            name = name,
            variant = variant,
            modifier = modifier,
        )
    } else {
        SubcomposeAsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Crop,
            loading = {
                CharacterPortraitLoading(
                    variant = variant,
                    modifier = Modifier.fillMaxSize(),
                )
            },
            error = {
                CharacterPortraitFallback(
                    name = name,
                    variant = variant,
                    modifier = Modifier.fillMaxSize(),
                )
            },
            success = {
                SubcomposeAsyncImageContent()
            },
        )
    }
}

@Composable
private fun CharacterPortraitLoading(
    variant: CharacterPortraitVariant,
    modifier: Modifier = Modifier,
) {
    CharacterPortraitPremiumBackground(
        modifier = modifier,
        contentPadding = variant.contentPadding,
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, DisneyColors.Gold.copy(alpha = 0.26f)),
            modifier = Modifier.size(variant.loadingMedallionSize),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(variant.loadingMedallionSize * 0.36f)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    DisneyColors.Gold.copy(alpha = 0.34f),
                                    Color.Transparent,
                                ),
                            ),
                            shape = CircleShape,
                        ),
                )
            }
        }
    }
}

@Composable
private fun CharacterPortraitFallback(
    name: String,
    variant: CharacterPortraitVariant,
    modifier: Modifier = Modifier,
) {
    CharacterPortraitPremiumBackground(
        modifier = modifier,
        contentPadding = variant.contentPadding,
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            shape = CircleShape,
            color = DisneyColors.InkElevated.copy(alpha = 0.72f),
            border = BorderStroke(1.dp, DisneyColors.Gold.copy(alpha = 0.54f)),
            modifier = Modifier.size(variant.fallbackMedallionSize),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = name.firstOrNull()?.uppercase() ?: "?",
                    style = if (variant == CharacterPortraitVariant.Hero) {
                        MaterialTheme.typography.displayMedium
                    } else {
                        MaterialTheme.typography.headlineMedium
                    },
                    color = DisneyColors.GoldSoft,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun CharacterPortraitPremiumBackground(
    modifier: Modifier = Modifier,
    contentPadding: Dp,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DisneyBrushes.imagePlaceholderGradient),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = contentAlignment,
        ) {
            content()
        }
    }
}
