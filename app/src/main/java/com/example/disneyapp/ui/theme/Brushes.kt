package com.example.disneyapp.ui.theme

import androidx.compose.ui.graphics.Brush

object DisneyBrushes {
    val catalogBackground: Brush
        get() = Brush.verticalGradient(
            colors = listOf(
                DisneyColors.Midnight,
                DisneyColors.Night,
                DisneyColors.Twilight,
                DisneyColors.DeepPlum,
            ),
        )

    val favoritesBackground: Brush
        get() = Brush.verticalGradient(
            colors = listOf(
                DisneyColors.Midnight,
                DisneyColors.FavoritesNight,
                DisneyColors.FavoritesPlum,
            ),
        )

    val heroGradient: Brush
        get() = Brush.linearGradient(
            colors = listOf(
                DisneyColors.RoyalBlue,
                DisneyColors.Violet,
                DisneyColors.Orchid,
                DisneyColors.GoldDeep,
            ),
        )

    val panelGradient: Brush
        get() = Brush.linearGradient(
            colors = listOf(
                DisneyColors.RoyalBlue.copy(alpha = 0.94f),
                DisneyColors.VioletDeep.copy(alpha = 0.92f),
                DisneyColors.Plum.copy(alpha = 0.86f),
            ),
        )

    val detailHeaderGradient: Brush
        get() = Brush.linearGradient(
            colors = listOf(
                DisneyColors.Ink.copy(alpha = 0.92f),
                DisneyColors.DetailViolet.copy(alpha = 0.88f),
                DisneyColors.Violet.copy(alpha = 0.76f),
            ),
        )

    val compactDetailGradient: Brush
        get() = Brush.linearGradient(
            colors = listOf(
                DisneyColors.DeepBlue,
                DisneyColors.Orchid,
                DisneyColors.GoldDeep,
            ),
        )

    val imagePlaceholderGradient: Brush
        get() = Brush.linearGradient(
            colors = listOf(
                DisneyColors.PlaceholderStart,
                DisneyColors.PlaceholderMiddle,
                DisneyColors.PlaceholderEnd,
                DisneyColors.GoldShadow,
            ),
        )
}
