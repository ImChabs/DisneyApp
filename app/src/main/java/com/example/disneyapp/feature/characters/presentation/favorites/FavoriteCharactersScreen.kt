package com.example.disneyapp.feature.characters.presentation.favorites

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.disneyapp.R
import com.example.disneyapp.core.presentation.asString
import com.example.disneyapp.feature.characters.presentation.components.CharacterBackIconButton
import com.example.disneyapp.feature.characters.presentation.components.CharacterPortrait
import com.example.disneyapp.feature.characters.presentation.components.CharacterPortraitVariant
import com.example.disneyapp.feature.characters.presentation.components.PremiumStatePanel
import com.example.disneyapp.feature.characters.presentation.list.CharacterListItemUi
import com.example.disneyapp.ui.theme.DisneyBrushes
import com.example.disneyapp.ui.theme.DisneyAppTheme
import com.example.disneyapp.ui.theme.DisneyColors
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FavoriteCharactersRoot(
    onBackClick: () -> Unit,
    onCharacterClick: (Int) -> Unit,
    viewModel: FavoriteCharactersViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is FavoriteCharactersEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message.asString(context))
                }
            }
        }
    }

    FavoriteCharactersScreen(
        state = state,
        onAction = viewModel::onAction,
        onBackClick = onBackClick,
        onCharacterClick = onCharacterClick,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteCharactersScreen(
    state: FavoriteCharactersState,
    onAction: (FavoriteCharactersAction) -> Unit,
    onBackClick: () -> Unit,
    onCharacterClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Box(modifier = modifier.fillMaxSize()) {
        FavoriteCharactersBackground()
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.favorites_title),
                            style = MaterialTheme.typography.titleLarge,
                            color = DisneyColors.TextPrimaryOnDark,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    navigationIcon = {
                        CharacterBackIconButton(onClick = onBackClick)
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        navigationIconContentColor = DisneyColors.Gold,
                    ),
                )
            },
            containerColor = Color.Transparent,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) { contentPadding ->
            FavoriteCharactersContent(
                state = state,
                onFavoriteClick = { onAction(FavoriteCharactersAction.OnFavoriteClick(it)) },
                onCharacterClick = onCharacterClick,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )
        }
    }
}

@Composable
private fun FavoriteCharactersContent(
    state: FavoriteCharactersState,
    onFavoriteClick: (Int) -> Unit,
    onCharacterClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier,
        contentPadding = PaddingValues(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 28.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (state.isEmpty) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                FavoritesEmptyState()
            }
        }

        items(
            items = state.favorites,
            key = { it.id },
        ) { favorite ->
            FavoriteCharacterCard(
                character = favorite,
                onClick = { onCharacterClick(favorite.id) },
                onFavoriteClick = { onFavoriteClick(favorite.id) },
            )
        }
    }
}

@Composable
private fun FavoriteCharacterCard(
    character: CharacterListItemUi,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.78f),
        ) {
            FavoriteCharacterImage(
                character = character,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.14f),
                                Color.Black.copy(alpha = 0.78f),
                            ),
                        ),
                    ),
            )
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(38.dp),
                shape = CircleShape,
                color = DisneyColors.Ink.copy(alpha = 0.74f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
            ) {
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = stringResource(R.string.characters_remove_favorite_content_description),
                        tint = DisneyColors.Gold,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    character.metadataBadges
                        .ifEmpty { listOf(stringResource(R.string.characters_badge_profile)) }
                        .take(2)
                        .forEach { badge ->
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = Color.White.copy(alpha = 0.2f),
                            ) {
                                Text(
                                    text = badge,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                }
            }
        }
    }
}

@Composable
private fun FavoriteCharacterImage(
    character: CharacterListItemUi,
    modifier: Modifier = Modifier,
) {
    CharacterPortrait(
        name = character.name,
        imageUrl = character.imageUrl,
        contentDescription = stringResource(R.string.characters_image_content_description, character.name),
        modifier = modifier,
        variant = CharacterPortraitVariant.Compact,
    )
}

@Composable
private fun FavoritesEmptyState(modifier: Modifier = Modifier) {
    PremiumStatePanel(
        title = stringResource(R.string.favorites_empty_title),
        message = stringResource(R.string.favorites_empty_message),
        icon = Icons.Filled.Favorite,
        modifier = modifier,
    )
}

@Composable
private fun FavoriteCharactersBackground(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DisneyBrushes.favoritesBackground),
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        val stars = listOf(
            Offset(size.width * 0.08f, size.height * 0.12f),
            Offset(size.width * 0.18f, size.height * 0.28f),
            Offset(size.width * 0.28f, size.height * 0.08f),
            Offset(size.width * 0.38f, size.height * 0.22f),
            Offset(size.width * 0.48f, size.height * 0.12f),
            Offset(size.width * 0.58f, size.height * 0.30f),
            Offset(size.width * 0.68f, size.height * 0.10f),
            Offset(size.width * 0.80f, size.height * 0.24f),
            Offset(size.width * 0.92f, size.height * 0.14f),
            Offset(size.width * 0.12f, size.height * 0.44f),
            Offset(size.width * 0.26f, size.height * 0.54f),
            Offset(size.width * 0.42f, size.height * 0.40f),
            Offset(size.width * 0.54f, size.height * 0.58f),
            Offset(size.width * 0.70f, size.height * 0.46f),
            Offset(size.width * 0.86f, size.height * 0.56f),
            Offset(size.width * 0.18f, size.height * 0.72f),
            Offset(size.width * 0.34f, size.height * 0.84f),
            Offset(size.width * 0.50f, size.height * 0.76f),
            Offset(size.width * 0.66f, size.height * 0.88f),
            Offset(size.width * 0.82f, size.height * 0.78f),
            Offset(size.width * 0.94f, size.height * 0.90f),
        )

        stars.forEachIndexed { index, offset ->
            val radius = when {
                index % 6 == 0 -> 1.9.dp.toPx()
                index % 3 == 0 -> 1.45.dp.toPx()
                else -> 1.05.dp.toPx()
            }
            val alpha = when {
                index % 6 == 0 -> 0.58f
                index % 2 == 0 -> 0.42f
                else -> 0.28f
            }
            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = radius,
                center = offset,
            )
        }
        drawCircle(
            color = DisneyColors.Gold.copy(alpha = 0.18f),
            radius = 2.8.dp.toPx(),
            center = Offset(size.width * 0.76f, size.height * 0.34f),
        )
        drawCircle(
            color = DisneyColors.LavenderMuted.copy(alpha = 0.16f),
            radius = 3.2.dp.toPx(),
            center = Offset(size.width * 0.24f, size.height * 0.64f),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoriteCharactersEmptyPreview() {
    DisneyAppTheme(dynamicColor = false) {
        FavoriteCharactersScreen(
            state = FavoriteCharactersState(),
            onAction = {},
            onBackClick = {},
            onCharacterClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoriteCharactersScreenPreview() {
    DisneyAppTheme(dynamicColor = false) {
        FavoriteCharactersScreen(
            state = FavoriteCharactersState(
                favorites = listOf(
                    CharacterListItemUi(
                        id = 4703,
                        name = "Mickey Mouse",
                        imageUrl = null,
                        metadataBadges = listOf("1 film", "1 short"),
                        isFavorite = true,
                    ),
                ),
            ),
            onAction = {},
            onBackClick = {},
            onCharacterClick = {},
        )
    }
}
