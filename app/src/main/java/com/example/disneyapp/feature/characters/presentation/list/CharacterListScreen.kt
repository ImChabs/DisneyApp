package com.example.disneyapp.feature.characters.presentation.list

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.disneyapp.R
import com.example.disneyapp.core.presentation.asString
import com.example.disneyapp.ui.theme.DisneyAppTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CharacterListRoot(
    onCharacterClick: (Int) -> Unit = {},
    viewModel: CharacterListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

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
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.characters_title),
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { contentPadding ->
        when {
            state.error != null -> CharacterListErrorState(
                message = state.error.asString(LocalContext.current),
                searchQuery = state.searchQuery,
                onRetryClick = { onAction(CharacterListAction.OnRetryClick) },
                onSearchQueryChange = { onAction(CharacterListAction.OnSearchQueryChange(it)) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )

            state.isEmpty -> CharacterListEmptyState(
                searchQuery = state.searchQuery,
                onSearchQueryChange = { onAction(CharacterListAction.OnSearchQueryChange(it)) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )

            else -> CharacterCatalogContent(
                state = state,
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
private fun CharacterCatalogContent(
    state: CharacterListState,
    onSearchQueryChange: (String) -> Unit,
    onCharacterClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading && state.characters.isEmpty()) {
        CharacterListLoadingState(
            searchQuery = state.searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            modifier = modifier,
        )
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier,
        contentPadding = PaddingValues(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 28.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
            CharacterCatalogHeader(
                searchQuery = state.searchQuery,
                onSearchQueryChange = onSearchQueryChange,
            )
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
    }
}

@Composable
private fun CharacterCatalogHeader(
    searchQuery: String,
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
        CharacterFilterChips()
        Spacer(modifier = Modifier.height(2.dp))
    }
}

@Composable
private fun CharacterHero(modifier: Modifier = Modifier) {
    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF273D73),
            Color(0xFF6E5AA8),
            Color(0xFFD8A84E),
        ),
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        tonalElevation = 4.dp,
        shadowElevation = 2.dp,
        color = MaterialTheme.colorScheme.primaryContainer,
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
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.Transparent,
        ),
    )
}

@Composable
private fun CharacterFilterChips(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val labels = listOf(
            stringResource(R.string.characters_filter_all),
            stringResource(R.string.characters_filter_films),
            stringResource(R.string.characters_filter_shows),
            stringResource(R.string.characters_filter_parks),
        )

        labels.forEachIndexed { index, label ->
            ElevatedFilterChip(
                selected = index == 0,
                onClick = {},
                label = {
                    Text(
                        text = label,
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
private fun CharacterListLoadingState(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        CharacterCatalogHeader(
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange,
        )

        repeat(3) {
            LoadingCharacterRow()
        }
    }
}

@Composable
private fun LoadingCharacterRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        repeat(2) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(0.78f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
            )
        }
    }
}

@Composable
private fun CharacterListErrorState(
    message: String,
    searchQuery: String,
    onRetryClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    CharacterStateScaffold(
        searchQuery = searchQuery,
        onSearchQueryChange = onSearchQueryChange,
        modifier = modifier,
    ) {
        StatePanel(
            title = stringResource(R.string.characters_error_title),
            message = message,
            action = {
                Button(onClick = onRetryClick) {
                    Text(text = stringResource(R.string.characters_retry))
                }
            },
        )
    }
}

@Composable
private fun CharacterListEmptyState(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hasSearch = searchQuery.isNotBlank()
    CharacterStateScaffold(
        searchQuery = searchQuery,
        onSearchQueryChange = onSearchQueryChange,
        modifier = modifier,
    ) {
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
        )
    }
}

@Composable
private fun CharacterStateScaffold(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        CharacterCatalogHeader(
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange,
        )
        content()
    }
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

@Preview(showBackground = true)
@Composable
private fun CharacterListScreenPreview() {
    DisneyAppTheme(dynamicColor = false) {
        CharacterListScreen(
            state = CharacterListState(
                characters = listOf(
                    CharacterListItemUi(
                        id = 1,
                        name = "Mickey Mouse",
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
