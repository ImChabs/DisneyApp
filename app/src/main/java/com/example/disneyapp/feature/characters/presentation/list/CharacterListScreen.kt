package com.example.disneyapp.feature.characters.presentation.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.disneyapp.R
import com.example.disneyapp.core.presentation.asString
import com.example.disneyapp.ui.theme.DisneyAppTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CharacterListRoot(
    onCharacterClick: (Int) -> Unit = {},
    viewModel: CharacterListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CharacterListScreen(
        state = state,
        onAction = viewModel::onAction,
        onCharacterClick = onCharacterClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListScreen(
    state: CharacterListState,
    onAction: (CharacterListAction) -> Unit,
    onCharacterClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        CharacterCatalogBackground()
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        CharacterTitleMark()
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        titleContentColor = Color.White,
                    ),
                )
            },
            containerColor = Color.Transparent,
        ) { contentPadding ->
            CharacterCatalogContent(
                state = state,
                errorMessage = state.error?.asString(LocalContext.current),
                onRetryClick = { onAction(CharacterListAction.OnRetryClick) },
                onLoadMore = { onAction(CharacterListAction.OnLoadMore) },
                onSearchQueryChange = { onAction(CharacterListAction.OnSearchQueryChange(it)) },
                onCharacterClick = onCharacterClick,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )
        }
    }
}

@Composable
private fun CharacterCatalogBackground(modifier: Modifier = Modifier) {
    val backgroundColors = listOf(
        Color(0xFF07152D),
        Color(0xFF111D3D),
        Color(0xFF171A3A),
        Color(0xFF201735),
    )
    val topAccent = Color(0xFF5C86FF).copy(alpha = 0.24f)
    val middleAccent = Color(0xFF8D6CFF).copy(alpha = 0.18f)
    val bottomAccent = Color(0xFFC472FF).copy(alpha = 0.16f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = backgroundColors,
                ),
            ),
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
            val glowColor = Color(0xFFFFD782)
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
                color = Color(0xFFBDA8FF).copy(alpha = 0.16f),
                radius = 3.dp.toPx(),
                center = Offset(size.width * 0.22f, size.height * 0.74f),
            )
        }
    }
}

@Composable
private fun CharacterTitleMark(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary,
                        ),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "D",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 7.dp, end = 7.dp)
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiaryContainer),
            )
        }
        Text(
            text = stringResource(R.string.characters_title),
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFFF9FBFF),
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun CharacterCatalogContent(
    state: CharacterListState,
    errorMessage: String?,
    onRetryClick: () -> Unit,
    onLoadMore: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onCharacterClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val gridState = rememberLazyGridState()

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

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        state = gridState,
        modifier = modifier,
        contentPadding = PaddingValues(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 28.dp),
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
            )
        }

        if (state.isLoadingMore) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                LoadMoreIndicator()
            }
        }
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
                    color = Color(0xFFFFD782),
                    trackColor = Color.White.copy(alpha = 0.12f),
                )
            }
        }
        CharacterFilterChips()
        Spacer(modifier = Modifier.height(2.dp))
    }
}

@Composable
private fun CharacterHero(modifier: Modifier = Modifier) {
    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF172E66),
            Color(0xFF3F347F),
            Color(0xFF7A4B9A),
            Color(0xFFC08A3A),
        ),
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        tonalElevation = 4.dp,
        shadowElevation = 2.dp,
        color = Color.Transparent,
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
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
            focusedContainerColor = Color(0xFF101A35).copy(alpha = 0.78f),
            unfocusedContainerColor = Color(0xFF101A35).copy(alpha = 0.62f),
            focusedBorderColor = Color(0xFFC7B8FF).copy(alpha = 0.84f),
            unfocusedBorderColor = Color.White.copy(alpha = 0.18f),
            cursorColor = Color(0xFFFFD782),
        ),
    )
}

@Composable
private fun CharacterFilterChips(modifier: Modifier = Modifier) {
    val selectedContainer = Color(0xFF4D3F86).copy(alpha = 0.88f)
    val unselectedContainer = Color(0xFF101A35).copy(alpha = 0.54f)
    val selectedContent = Color(0xFFFFD782)
    val unselectedContent = Color.White.copy(alpha = 0.76f)
    val chipColors = FilterChipDefaults.elevatedFilterChipColors(
        containerColor = unselectedContainer,
        labelColor = unselectedContent,
        iconColor = unselectedContent,
        selectedContainerColor = selectedContainer,
        selectedLabelColor = selectedContent,
        selectedLeadingIconColor = selectedContent,
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 2.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        val labels = listOf(
            stringResource(R.string.characters_filter_all),
            stringResource(R.string.characters_filter_films),
            stringResource(R.string.characters_filter_shows),
            stringResource(R.string.characters_filter_parks),
        )

        labels.forEachIndexed { index, label ->
            val selected = index == 0

            ElevatedFilterChip(
                selected = selected,
                onClick = {},
                modifier = Modifier.height(36.dp),
                shape = RoundedCornerShape(22.dp),
                colors = chipColors,
                elevation = FilterChipDefaults.elevatedFilterChipElevation(
                    elevation = 1.dp,
                    pressedElevation = 2.dp,
                    focusedElevation = 2.dp,
                    hoveredElevation = 2.dp,
                    draggedElevation = 3.dp,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selected,
                    borderColor = Color.White.copy(alpha = 0.18f),
                    selectedBorderColor = Color(0xFFFFD782).copy(alpha = 0.46f),
                    borderWidth = 1.dp,
                    selectedBorderWidth = 1.dp,
                ),
                leadingIcon = if (selected) {
                    {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(
                                    color = selectedContent,
                                    shape = CircleShape,
                                ),
                        )
                    }
                } else {
                    null
                },
                label = {
                    Text(
                        text = label,
                        modifier = Modifier.padding(horizontal = 2.dp),
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
            )
        }
    }
}

@Composable
private fun CharacterCard(
    character: CharacterListItemUi,
    onClick: () -> Unit,
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
private fun CharacterImage(
    character: CharacterListItemUi,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        CharacterImageFallback(
            name = character.name,
            modifier = Modifier.fillMaxSize(),
        )

        if (!character.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = character.imageUrl,
                contentDescription = stringResource(R.string.characters_image_content_description, character.name),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Composable
private fun CharacterImageFallback(
    name: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.secondaryContainer,
                        MaterialTheme.colorScheme.tertiaryContainer,
                    ),
                ),
            )
            .padding(18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.24f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.32f)),
            modifier = Modifier.size(74.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = name.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
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
                            Color(0xFF172E66).copy(alpha = 0.94f),
                            Color(0xFF3F347F).copy(alpha = 0.9f),
                            Color(0xFF7A4B9A).copy(alpha = 0.78f),
                            Color(0xFFC08A3A).copy(alpha = 0.72f),
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
                                Color(0xFFC7B8FF).copy(alpha = 0.22f),
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
                            .background(Color(0xFFFFD782).copy(alpha = 0.54f)),
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
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    StatePanel(
        title = stringResource(R.string.characters_error_title),
        message = message,
        action = {
            Button(onClick = onRetryClick) {
                Text(text = stringResource(R.string.characters_retry))
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
    StatePanel(
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

@Composable
private fun StatePanel(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            if (action != null) {
                Spacer(modifier = Modifier.height(2.dp))
                action()
            }
        }
    }
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
        )
    }
}
