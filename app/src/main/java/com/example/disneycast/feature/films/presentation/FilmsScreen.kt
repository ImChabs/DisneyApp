package com.example.disneycast.feature.films.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.disneycast.R
import com.example.disneycast.core.presentation.asString
import com.example.disneycast.core.presentation.components.PremiumScrollToTopButton
import com.example.disneycast.feature.catalog.presentation.CatalogScaffold
import com.example.disneycast.feature.catalog.presentation.CatalogSection
import com.example.disneycast.feature.characters.presentation.components.CharacterPortrait
import com.example.disneycast.feature.characters.presentation.components.CharacterPortraitVariant
import com.example.disneycast.feature.characters.presentation.components.PremiumStatePanel
import com.example.disneycast.ui.theme.DisneyCastTheme
import com.example.disneycast.ui.theme.DisneyBrushes
import com.example.disneycast.ui.theme.DisneyColors
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FilmsRoot(
    onCharactersClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onCharacterClick: (Int) -> Unit,
    viewModel: FilmsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val comingSoonMessage = stringResource(R.string.catalog_section_coming_soon)

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is FilmsEvent.NavigateToCharacterDetail -> onCharacterClick(event.characterId)
            }
        }
    }

    FilmsScreen(
        state = state,
        onAction = viewModel::onAction,
        onFavoritesClick = onFavoritesClick,
        onCatalogSectionClick = { section ->
            when (section) {
                CatalogSection.Characters -> onCharactersClick()
                CatalogSection.Films -> Unit
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
fun FilmsScreen(
    state: FilmsState,
    onAction: (FilmsAction) -> Unit,
    onFavoritesClick: () -> Unit,
    onCatalogSectionClick: (CatalogSection) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    CatalogScaffold(
        selectedSection = CatalogSection.Films,
        onSectionClick = onCatalogSectionClick,
        onFavoritesClick = onFavoritesClick,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    ) { contentModifier ->
        FilmsContent(
            state = state,
            errorMessage = state.error?.asString(LocalContext.current),
            onSearchQueryChange = { onAction(FilmsAction.OnSearchQueryChange(it)) },
            onRetryClick = { onAction(FilmsAction.OnRetryClick) },
            onCharacterClick = { onAction(FilmsAction.OnCharacterClick(it)) },
            modifier = contentModifier,
        )
    }
}

@Composable
private fun FilmsContent(
    state: FilmsState,
    errorMessage: String?,
    onSearchQueryChange: (String) -> Unit,
    onRetryClick: () -> Unit,
    onCharacterClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val showScrollToTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 }
    }

    Box(modifier = modifier) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, top = 0.dp, end = 20.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                FilmsHeader(
                    query = state.searchQuery,
                    isLoading = state.isLoading,
                    onQueryChange = onSearchQueryChange,
                )
            }

            when {
                errorMessage != null -> {
                    item {
                        FilmsErrorState(
                            message = errorMessage,
                            isLoading = state.isLoading,
                            onRetryClick = onRetryClick,
                        )
                    }
                }

                state.isIdle -> {
                    item {
                        FilmsIdleState()
                    }
                }

                state.isLoading -> {
                    item {
                        FilmsLoadingState()
                    }
                }

                state.isEmptyResult -> {
                    item {
                        FilmsEmptyState()
                    }
                }
            }

            items(
                items = state.results,
                key = { it.characterId },
            ) { result ->
                FilmCharacterCard(
                    result = result,
                    onClick = { onCharacterClick(result.characterId) },
                )
            }
        }

        PremiumScrollToTopButton(
            isVisible = showScrollToTop,
            onClick = {
                coroutineScope.launch {
                    listState.animateScrollToItem(0)
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
private fun FilmsHeader(
    query: String,
    isLoading: Boolean,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        FilmsHero()
        FilmsSearchBar(
            query = query,
            onQueryChange = onQueryChange,
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
private fun FilmsHero(modifier: Modifier = Modifier) {
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
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.films_hero_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(R.string.films_hero_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.88f),
                )
            }
        }
    }
}

@Composable
private fun FilmsSearchBar(
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
            Text(text = stringResource(R.string.films_search_placeholder))
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
private fun FilmCharacterCard(
    result: FilmCharacterUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = DisneyColors.Ink.copy(alpha = 0.72f),
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            CharacterPortrait(
                name = result.characterName,
                imageUrl = result.imageUrl,
                contentDescription = stringResource(
                    R.string.films_character_image_content_description,
                    result.characterName,
                ),
                modifier = Modifier
                    .width(92.dp)
                    .height(118.dp)
                    .clip(RoundedCornerShape(18.dp)),
                variant = CharacterPortraitVariant.Compact,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = result.characterName,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                FilmAppearanceCountBadge(count = result.appearanceCount)
                AppearanceSection(
                    title = stringResource(R.string.films_section_films),
                    appearances = result.films,
                )
                AppearanceSection(
                    title = stringResource(R.string.films_section_short_films),
                    appearances = result.shortFilms,
                )
            }
        }
    }
}

@Composable
private fun FilmAppearanceCountBadge(
    count: Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        color = DisneyColors.Gold.copy(alpha = 0.16f),
        border = BorderStroke(1.dp, DisneyColors.Gold.copy(alpha = 0.32f)),
    ) {
        Text(
            text = stringResource(
                if (count == 1) {
                    R.string.films_appearance_count_singular
                } else {
                    R.string.films_appearance_count_plural
                },
                count,
            ),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelMedium,
            color = DisneyColors.GoldSoft,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun AppearanceSection(
    title: String,
    appearances: List<String>,
    modifier: Modifier = Modifier,
) {
    if (appearances.isEmpty()) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = DisneyColors.Lavender,
            fontWeight = FontWeight.SemiBold,
        )
        appearances.take(MAX_VISIBLE_APPEARANCES).forEach { appearance ->
            Text(
                text = appearance,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.84f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (appearances.size > MAX_VISIBLE_APPEARANCES) {
            Text(
                text = stringResource(
                    R.string.films_more_appearances,
                    appearances.size - MAX_VISIBLE_APPEARANCES,
                ),
                style = MaterialTheme.typography.labelMedium,
                color = DisneyColors.Gold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun FilmsLoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(30.dp),
            color = DisneyColors.Gold,
            strokeWidth = 2.dp,
        )
    }
}

@Composable
private fun FilmsErrorState(
    message: String,
    isLoading: Boolean,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PremiumStatePanel(
        title = stringResource(R.string.films_error_title),
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
private fun FilmsIdleState(modifier: Modifier = Modifier) {
    PremiumStatePanel(
        title = stringResource(R.string.films_idle_title),
        message = stringResource(R.string.films_idle_message),
        modifier = modifier,
    )
}

@Composable
private fun FilmsEmptyState(modifier: Modifier = Modifier) {
    PremiumStatePanel(
        title = stringResource(R.string.films_empty_title),
        message = stringResource(R.string.films_empty_message),
        modifier = modifier,
    )
}

private const val MAX_VISIBLE_APPEARANCES = 4

@Preview(showBackground = true)
@Composable
private fun FilmsScreenPreview() {
    DisneyCastTheme(dynamicColor = false) {
        FilmsScreen(
            state = FilmsState(
                searchQuery = "Mickey",
                submittedQuery = "Mickey",
                results = listOf(
                    FilmCharacterUi(
                        characterId = 4703,
                        characterName = "Mickey Mouse",
                        imageUrl = null,
                        films = listOf("Fantasia", "Fun and Fancy Free"),
                        shortFilms = listOf("Steamboat Willie"),
                    ),
                ),
            ),
            onAction = {},
            onFavoritesClick = {},
            onCatalogSectionClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FilmsIdlePreview() {
    DisneyCastTheme(dynamicColor = false) {
        FilmsScreen(
            state = FilmsState(),
            onAction = {},
            onFavoritesClick = {},
            onCatalogSectionClick = {},
        )
    }
}
