package com.example.disneyapp.feature.characters.presentation.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.disneyapp.core.presentation.components.PremiumScrollToTopButton
import com.example.disneyapp.feature.catalog.presentation.CatalogScaffold
import com.example.disneyapp.feature.catalog.presentation.CatalogSection
import com.example.disneyapp.feature.characters.presentation.components.CharacterPortrait
import com.example.disneyapp.feature.characters.presentation.components.CharacterPortraitVariant
import com.example.disneyapp.feature.characters.presentation.components.PremiumStatePanel
import com.example.disneyapp.ui.theme.DisneyBrushes
import com.example.disneyapp.ui.theme.DisneyAppTheme
import com.example.disneyapp.ui.theme.DisneyColors
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CharacterListRoot(
    resetRequest: Int = 0,
    onCharacterClick: (Int) -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    onFilmsClick: () -> Unit = {},
    viewModel: CharacterListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val comingSoonMessage = stringResource(R.string.catalog_section_coming_soon)
    var handledResetRequest by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is CharacterListEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message.asString(context))
                }
            }
        }
    }

    LaunchedEffect(resetRequest) {
        if (resetRequest > handledResetRequest) {
            handledResetRequest = resetRequest
            viewModel.onAction(CharacterListAction.OnResetCatalog)
        }
    }

    CharacterListScreen(
        state = state,
        onAction = viewModel::onAction,
        resetRequest = resetRequest,
        onCharacterClick = onCharacterClick,
        onFavoritesClick = onFavoritesClick,
        onCatalogSectionClick = { section ->
            when (section) {
                CatalogSection.Characters -> Unit
                CatalogSection.Films -> onFilmsClick()
                CatalogSection.Shows,
                CatalogSection.Parks,
                -> coroutineScope.launch {
                    snackbarHostState.showSnackbar(comingSoonMessage)
                }
            }
        },
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun CharacterListScreen(
    state: CharacterListState,
    onAction: (CharacterListAction) -> Unit,
    resetRequest: Int = 0,
    onCharacterClick: (Int) -> Unit,
    onFavoritesClick: () -> Unit,
    onCatalogSectionClick: (CatalogSection) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    CatalogScaffold(
        selectedSection = CatalogSection.Characters,
        onSectionClick = onCatalogSectionClick,
        onFavoritesClick = onFavoritesClick,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    ) { contentModifier ->
        CharacterCatalogContent(
            state = state,
            errorMessage = state.error?.asString(LocalContext.current),
            resetRequest = resetRequest,
            onRetryClick = { onAction(CharacterListAction.OnRetryClick) },
            onLoadMore = { onAction(CharacterListAction.OnLoadMore) },
            onSearchQueryChange = { onAction(CharacterListAction.OnSearchQueryChange(it)) },
            onFavoriteClick = { onAction(CharacterListAction.OnFavoriteClick(it)) },
            onCharacterClick = onCharacterClick,
            modifier = contentModifier,
        )
    }
}

@Composable
private fun CharacterCatalogContent(
    state: CharacterListState,
    errorMessage: String?,
    resetRequest: Int,
    onRetryClick: () -> Unit,
    onLoadMore: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onFavoriteClick: (Int) -> Unit,
    onCharacterClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val gridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    var handledScrollResetRequest by rememberSaveable { mutableIntStateOf(0) }
    val showScrollToTop by remember {
        derivedStateOf { gridState.firstVisibleItemIndex > 0 }
    }

    LaunchedEffect(resetRequest) {
        if (resetRequest > handledScrollResetRequest) {
            handledScrollResetRequest = resetRequest
            gridState.scrollToItem(0)
        }
    }

    LaunchedEffect(
        gridState,
        state.canLoadMore,
        state.isLoading,
        state.isLoadingMore,
        state.characters.size,
    ) {
        snapshotFlow {
            val layoutInfo = gridState.layoutInfo
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return@snapshotFlow false

            state.canLoadMore &&
                !state.isLoading &&
                !state.isLoadingMore &&
                layoutInfo.totalItemsCount > 0 &&
                lastVisibleIndex >= layoutInfo.totalItemsCount - LOAD_MORE_ITEM_THRESHOLD
        }
            .distinctUntilChanged()
            .collect { shouldLoadMore ->
                if (shouldLoadMore) {
                    onLoadMore()
                }
            }
    }

    Box(modifier = modifier) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            state = gridState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, top = 0.dp, end = 20.dp, bottom = 96.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                CharacterCatalogHeader(
                    searchQuery = state.searchQuery,
                    isLoading = state.isLoading,
                    onSearchQueryChange = onSearchQueryChange,
                )
            }

            when {
                errorMessage != null -> {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        CharacterListErrorState(
                            message = errorMessage,
                            isLoading = state.isLoading,
                            onRetryClick = onRetryClick,
                        )
                    }
                }

                state.isEmpty -> {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        CharacterListEmptyState(searchQuery = state.searchQuery)
                    }
                }

                state.isLoading && state.characters.isEmpty() -> {
                    items(
                        count = 3,
                        span = { GridItemSpan(maxLineSpan) },
                    ) {
                        LoadingCharacterRow()
                    }
                }
            }

            items(
                items = state.characters,
                key = { it.id },
            ) { character ->
                CharacterCard(
                    character = character,
                    onClick = { onCharacterClick(character.id) },
                    onFavoriteClick = { onFavoriteClick(character.id) },
                )
            }

            if (state.isLoadingMore) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    LoadMoreIndicator()
                }
            }
        }

        PremiumScrollToTopButton(
            isVisible = showScrollToTop,
            onClick = {
                coroutineScope.launch {
                    gridState.animateScrollToItem(0)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 20.dp, bottom = 20.dp),
        )
    }
}

@Composable
private fun CharacterCatalogHeader(
    searchQuery: String,
    isLoading: Boolean,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        CharacterHero()
        CharacterSearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
        )
        Box(modifier = Modifier.height(4.dp)) {
            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(999.dp)),
                    color = DisneyColors.Gold,
                    trackColor = Color.White.copy(alpha = 0.12f),
                )
            }
        }
    }
}

@Composable
private fun CharacterHero(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        tonalElevation = 4.dp,
        shadowElevation = 2.dp,
        color = Color.Transparent,
    ) {
        Box(
            modifier = Modifier
                .background(DisneyBrushes.heroGradient)
                .padding(22.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(R.string.characters_hero_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(R.string.characters_hero_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.88f),
                )
            }
        }
    }
}

@Composable
private fun CharacterSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(24.dp),
        placeholder = {
            Text(text = stringResource(R.string.characters_search_placeholder))
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedPlaceholderColor = Color.White.copy(alpha = 0.64f),
            unfocusedPlaceholderColor = Color.White.copy(alpha = 0.58f),
            focusedContainerColor = DisneyColors.Ink.copy(alpha = 0.78f),
            unfocusedContainerColor = DisneyColors.Ink.copy(alpha = 0.62f),
            focusedBorderColor = DisneyColors.Lavender.copy(alpha = 0.84f),
            unfocusedBorderColor = Color.White.copy(alpha = 0.18f),
            cursorColor = DisneyColors.Gold,
        ),
    )
}

@Composable
private fun CharacterCard(
    character: CharacterListItemUi,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.78f),
        ) {
            CharacterImage(
                character = character,
                modifier = Modifier.fillMaxSize(),
            )

            FavoriteCardButton(
                isFavorite = character.isFavorite,
                onClick = onFavoriteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.12f),
                                Color.Black.copy(alpha = 0.74f),
                            ),
                        ),
                    ),
            )

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

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val badges = character.metadataBadges.ifEmpty {
                        listOf(stringResource(R.string.characters_badge_profile))
                    }
                    badges.take(2).forEach { label ->
                        CharacterMetadataBadge(label = label)
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteCardButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.size(38.dp),
        shape = CircleShape,
        color = DisneyColors.Ink.copy(alpha = 0.74f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = stringResource(
                    if (isFavorite) {
                        R.string.characters_remove_favorite_content_description
                    } else {
                        R.string.characters_add_favorite_content_description
                    },
                ),
                tint = if (isFavorite) DisneyColors.Gold else Color.White.copy(alpha = 0.78f),
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun CharacterImage(
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
private fun CharacterMetadataBadge(
    label: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        color = Color.White.copy(alpha = 0.2f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.28f)),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun LoadMoreIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(28.dp),
            strokeWidth = 2.dp,
        )
    }
}

@Composable
private fun LoadingCharacterRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        repeat(3) {
            LoadingCharacterCard(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun LoadingCharacterCard(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.78f),
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent,
        shadowElevation = 3.dp,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f)),
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            DisneyColors.RoyalBlue.copy(alpha = 0.94f),
                            DisneyColors.Violet.copy(alpha = 0.9f),
                            DisneyColors.Orchid.copy(alpha = 0.78f),
                            DisneyColors.GoldDeep.copy(alpha = 0.72f),
                        ),
                    ),
                ),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 34.dp, y = (-36).dp)
                    .size(108.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.2f),
                                Color.Transparent,
                            ),
                        ),
                        shape = CircleShape,
                    ),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (-42).dp)
                    .size(116.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                DisneyColors.Lavender.copy(alpha = 0.22f),
                                Color.Transparent,
                            ),
                        ),
                        shape = CircleShape,
                    ),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.08f),
                                Color.Black.copy(alpha = 0.64f),
                            ),
                        ),
                    ),
            )
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(58.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.2f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(DisneyColors.Gold.copy(alpha = 0.54f)),
                    )
                }
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {


                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    LoadingCatalogPlaceholder(
                        modifier = Modifier.weight(1f),
                        height = 20,
                        radius = 999,
                        alpha = 0.17f,
                    )
                    LoadingCatalogPlaceholder(
                        modifier = Modifier.weight(0.5f),
                        height = 20,
                        radius = 999,
                        alpha = 0.13f,
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingCatalogPlaceholder(
    modifier: Modifier = Modifier,
    height: Int,
    radius: Int = 9,
    alpha: Float = 0.16f,
) {
    Box(
        modifier = modifier
            .height(height.dp)
            .clip(RoundedCornerShape(radius.dp))
            .background(Color.White.copy(alpha = alpha)),
    )
}

@Composable
private fun CharacterListErrorState(
    message: String,
    isLoading: Boolean,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PremiumStatePanel(
        title = stringResource(R.string.characters_error_title),
        message = message,
        icon = Icons.Outlined.CloudOff,
        action = {
            Button(
                onClick = onRetryClick,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DisneyColors.Gold,
                    contentColor = DisneyColors.Ink,
                    disabledContainerColor = DisneyColors.Gold.copy(alpha = 0.74f),
                    disabledContentColor = DisneyColors.Ink.copy(alpha = 0.74f),
                ),
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = DisneyColors.Ink,
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                    }
                    Text(text = stringResource(R.string.characters_retry))
                }
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun CharacterListEmptyState(
    searchQuery: String,
    modifier: Modifier = Modifier,
) {
    val hasSearch = searchQuery.isNotBlank()
    PremiumStatePanel(
        title = if (hasSearch) {
            stringResource(R.string.characters_empty_search_title)
        } else {
            stringResource(R.string.characters_empty_title)
        },
        message = if (hasSearch) {
            stringResource(R.string.characters_empty_search_message)
        } else {
            stringResource(R.string.characters_empty_message)
        },
        modifier = modifier,
    )
}

private const val LOAD_MORE_ITEM_THRESHOLD = 6

@Preview(showBackground = true)
@Composable
private fun CharacterListScreenPreview() {
    DisneyAppTheme(dynamicColor = false) {
        CharacterListScreen(
            state = CharacterListState(
                characters = listOf(
                    CharacterListItemUi(
                        id = 1,
                        name = "Achilles",
                        imageUrl = null,
                        metadataBadges = listOf("2 films", "1 show"),
                    ),
                    CharacterListItemUi(
                        id = 2,
                        name = "Moana",
                        imageUrl = null,
                        metadataBadges = listOf("1 film", "2 games"),
                    ),
                    CharacterListItemUi(
                        id = 3,
                        name = "Tarzan",
                        imageUrl = null,
                        metadataBadges = listOf("3 films", "1 game"),
                    )
                ),
            ),
            onAction = {},
            onCharacterClick = {},
            onFavoritesClick = {},
            onCatalogSectionClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CharacterListEmptyPreview() {
    DisneyAppTheme(dynamicColor = false) {
        CharacterListScreen(
            state = CharacterListState(searchQuery = "Aurora"),
            onAction = {},
            onCharacterClick = {},
            onFavoritesClick = {},
            onCatalogSectionClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CharacterListLoadingPreview() {
    DisneyAppTheme(dynamicColor = false) {
        CharacterListScreen(
            state = CharacterListState(isLoading = true),
            onAction = {},
            onCharacterClick = {},
            onFavoritesClick = {},
            onCatalogSectionClick = {},
        )
    }
}
