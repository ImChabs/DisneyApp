package com.example.disneycast.feature.catalog.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.disneycast.R
import com.example.disneycast.ui.theme.DisneyBrushes
import com.example.disneycast.ui.theme.DisneyColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScaffold(
    selectedSection: CatalogSection,
    onSectionClick: (CatalogSection) -> Unit,
    onFavoritesClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        CatalogBackground()
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        CatalogTitleMark()
                    },
                    actions = {
                        CatalogFavoritesButton(onClick = onFavoritesClick)
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        titleContentColor = Color.White,
                    ),
                )
            },
            containerColor = Color.Transparent,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            ) {
                CatalogSectionSelector(
                    selectedSection = selectedSection,
                    onSectionClick = onSectionClick,
                    modifier = Modifier.padding(
                        start = CatalogHorizontalPadding,
                        top = CatalogSelectorTopPadding,
                        end = CatalogHorizontalPadding,
                    ),
                )
                Spacer(modifier = Modifier.height(CatalogSelectorContentSpacing))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    content(Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
private fun CatalogTitleMark(modifier: Modifier = Modifier) {
    val logoContentDescription = stringResource(R.string.splash_logo_content_description)

    Row(
        modifier = modifier.semantics {
            contentDescription = logoContentDescription
        },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.disney_splash_wordmark),
            contentDescription = null,
            modifier = Modifier
                .size(width = 132.dp, height = 42.dp),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
private fun CatalogFavoritesButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.padding(end = 8.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = stringResource(R.string.characters_favorites_content_description),
            tint = DisneyColors.Gold,
        )
    }
}

@Composable
private fun CatalogBackground(modifier: Modifier = Modifier) {
    val topAccent = DisneyColors.BlueGlow.copy(alpha = 0.24f)
    val middleAccent = DisneyColors.PurpleGlow.copy(alpha = 0.18f)
    val bottomAccent = DisneyColors.MagentaGlow.copy(alpha = 0.16f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DisneyBrushes.catalogBackground),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 92.dp, y = (-68).dp)
                .size(320.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            topAccent,
                            Color.Transparent,
                        ),
                    ),
                    shape = CircleShape,
                ),
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = (-132).dp, y = (-12).dp)
                .size(340.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            middleAccent,
                            Color.Transparent,
                        ),
                    ),
                    shape = CircleShape,
                ),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 128.dp, y = 96.dp)
                .size(360.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            bottomAccent,
                            Color.Transparent,
                        ),
                    ),
                    shape = CircleShape,
                ),
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            val starColor = Color.White
            val glowColor = DisneyColors.Gold
            val stars = listOf(
                Offset(size.width * 0.05f, size.height * 0.23f),
                Offset(size.width * 0.10f, size.height * 0.13f),
                Offset(size.width * 0.14f, size.height * 0.30f),
                Offset(size.width * 0.28f, size.height * 0.08f),
                Offset(size.width * 0.33f, size.height * 0.24f),
                Offset(size.width * 0.43f, size.height * 0.10f),
                Offset(size.width * 0.54f, size.height * 0.16f),
                Offset(size.width * 0.64f, size.height * 0.07f),
                Offset(size.width * 0.72f, size.height * 0.22f),
                Offset(size.width * 0.82f, size.height * 0.11f),
                Offset(size.width * 0.92f, size.height * 0.28f),
                Offset(size.width * 0.18f, size.height * 0.38f),
                Offset(size.width * 0.31f, size.height * 0.45f),
                Offset(size.width * 0.48f, size.height * 0.36f),
                Offset(size.width * 0.61f, size.height * 0.52f),
                Offset(size.width * 0.72f, size.height * 0.45f),
                Offset(size.width * 0.91f, size.height * 0.50f),
                Offset(size.width * 0.12f, size.height * 0.62f),
                Offset(size.width * 0.36f, size.height * 0.58f),
                Offset(size.width * 0.52f, size.height * 0.68f),
                Offset(size.width * 0.68f, size.height * 0.78f),
                Offset(size.width * 0.86f, size.height * 0.70f),
                Offset(size.width * 0.24f, size.height * 0.84f),
                Offset(size.width * 0.44f, size.height * 0.88f),
                Offset(size.width * 0.78f, size.height * 0.90f),
            )

            stars.forEachIndexed { index, offset ->
                val radius = when {
                    index % 7 == 0 -> 1.9.dp.toPx()
                    index % 3 == 0 -> 1.45.dp.toPx()
                    else -> 1.05.dp.toPx()
                }
                val alpha = when {
                    index % 7 == 0 -> 0.6f
                    index % 2 == 0 -> 0.48f
                    else -> 0.32f
                }
                drawCircle(
                    color = starColor.copy(alpha = alpha),
                    radius = radius,
                    center = offset,
                )
            }
            drawCircle(
                color = glowColor.copy(alpha = 0.18f),
                radius = 2.6.dp.toPx(),
                center = Offset(size.width * 0.66f, size.height * 0.24f),
            )
            drawCircle(
                color = DisneyColors.LavenderMuted.copy(alpha = 0.16f),
                radius = 3.dp.toPx(),
                center = Offset(size.width * 0.22f, size.height * 0.74f),
            )
        }
    }
}

private val CatalogHorizontalPadding = 20.dp
private val CatalogSelectorTopPadding = 16.dp
private val CatalogSelectorContentSpacing = 16.dp
