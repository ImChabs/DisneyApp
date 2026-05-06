package com.example.disneyapp.feature.characters.presentation.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CharacterDetailRoot(
    characterId: Int,
    onBackClick: () -> Unit,
    viewModel: CharacterDetailViewModel = koinViewModel(
        key = "character-detail-$characterId",
        parameters = { parametersOf(characterId) },
    ),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is CharacterDetailEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message.asString(context))
                }
            }
        }
    }

    CharacterDetailScreen(
        state = state,
        onAction = viewModel::onAction,
        onBackClick = onBackClick,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    state: CharacterDetailState,
    onAction: (CharacterDetailAction) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Box(modifier = modifier.fillMaxSize()) {
        CharacterDetailBackground()
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = state.character?.name ?: stringResource(R.string.character_detail_title),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color(0xFFF9FBFF),
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    navigationIcon = {
                        DetailBackButton(onClick = onBackClick)
                    },
                    actions = {
                        state.character?.let { character ->
                            DetailFavoriteButton(
                                isFavorite = character.isFavorite,
                                onClick = { onAction(CharacterDetailAction.OnFavoriteClick) },
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                    ),
                )
            },
            containerColor = Color.Transparent,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) { contentPadding ->
            CharacterDetailContent(
                state = state,
                errorMessage = state.error?.asString(LocalContext.current),
                onRetryClick = { onAction(CharacterDetailAction.OnRetryClick) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )
        }
    }
}

@Composable
private fun DetailFavoriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.padding(end = 8.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = stringResource(
                if (isFavorite) {
                    R.string.characters_remove_favorite_content_description
                } else {
                    R.string.characters_add_favorite_content_description
                },
            ),
            tint = if (isFavorite) Color(0xFFFFD782) else Color.White.copy(alpha = 0.76f),
        )
    }
}

@Composable
private fun DetailBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentDescription = stringResource(R.string.character_detail_back_content_description)

    Surface(
        modifier = modifier
            .padding(start = 12.dp)
            .size(42.dp)
            .semantics {
                this.contentDescription = contentDescription
            }
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = Color(0xFF101A35).copy(alpha = 0.72f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
private fun CharacterDetailContent(
    state: CharacterDetailState,
    errorMessage: String?,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        state.isLoading && state.character == null -> {
            CharacterDetailLoading(modifier = modifier)
        }
        errorMessage != null && state.character == null -> {
            CharacterDetailError(
                message = errorMessage,
                onRetryClick = onRetryClick,
                modifier = modifier,
            )
        }
        state.character != null -> {
            CharacterDetailLoaded(
                character = state.character,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun CharacterDetailLoaded(
    character: CharacterDetailUi,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            CharacterDetailHero(character = character)
        }

        if (character.hasProfileData) {
            items(
                items = character.sections,
                key = { it.title },
            ) { section ->
                CharacterDetailSection(section = section)
            }
        } else {
            item {
                CharacterDetailEmptyProfile()
            }
        }
    }
}

@Composable
private fun CharacterDetailHero(
    character: CharacterDetailUi,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.86f),
            ) {
                CharacterDetailImage(
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
                                    Color.Black.copy(alpha = 0.18f),
                                    Color.Black.copy(alpha = 0.82f),
                                ),
                            ),
                        ),
                )
                CharacterHeroText(
                    character = character,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(22.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CharacterHeroText(
    character: CharacterDetailUi,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = character.name,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        if (character.alignment != null) {
            CharacterDetailChip(label = character.alignment)
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val badges = character.metadataBadges.ifEmpty {
                listOf(stringResource(R.string.character_detail_badge_profile))
            }
            badges.take(3).forEach { badge ->
                CharacterDetailChip(label = badge)
            }
        }
    }
}

@Composable
private fun CharacterDetailImage(
    character: CharacterDetailUi,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        CharacterDetailImageFallback(
            name = character.name,
            modifier = Modifier.fillMaxSize(),
        )
        if (!character.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = character.imageUrl,
                contentDescription = stringResource(
                    R.string.character_detail_image_content_description,
                    character.name,
                ),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Composable
private fun CharacterDetailImageFallback(
    name: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF243B7A),
                        Color(0xFF7A4B9A),
                        Color(0xFFC08A3A),
                    ),
                ),
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.22f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.34f)),
            modifier = Modifier.size(104.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = name.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun CharacterDetailChip(
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
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun CharacterDetailSection(
    section: CharacterDetailSectionUi,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent,
        tonalElevation = 0.dp,
        shadowElevation = 3.dp,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.16f)),
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF101A35).copy(alpha = 0.92f),
                            Color(0xFF272557).copy(alpha = 0.88f),
                            Color(0xFF3F347F).copy(alpha = 0.76f),
                        ),
                    ),
                ),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 44.dp, y = (-52).dp)
                    .size(154.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFC7B8FF).copy(alpha = 0.18f),
                                Color.Transparent,
                            ),
                        ),
                        shape = CircleShape,
                    ),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-48).dp, y = 50.dp)
                    .size(142.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFD782).copy(alpha = 0.12f),
                                Color.Transparent,
                            ),
                        ),
                        shape = CircleShape,
                    ),
            )
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(11.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFD782)),
                    )
                    Text(
                        text = section.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    section.items.forEach { item ->
                        CharacterDetailFact(label = item)
                    }
                }
            }
        }
    }
}

@Composable
private fun CharacterDetailFact(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFD782),
                            Color(0xFFC7B8FF),
                        ),
                    ),
                ),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.88f),
        )
    }
}

@Composable
private fun CharacterDetailLoading(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            CharacterDetailLoadingHero()
        }
        items(count = 2) {
            CharacterDetailLoadingSection()
        }
    }
}

@Composable
private fun CharacterDetailLoadingHero(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        color = Color.Transparent,
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.86f)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF243B7A),
                            Color(0xFF7A4B9A),
                            Color(0xFFC08A3A),
                        ),
                    ),
                ),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 72.dp, y = (-64).dp)
                    .size(230.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.22f),
                                Color.Transparent,
                            ),
                        ),
                        shape = CircleShape,
                    ),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (-78).dp)
                    .size(220.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFC7B8FF).copy(alpha = 0.24f),
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
                                Color.Black.copy(alpha = 0.18f),
                                Color.Black.copy(alpha = 0.58f),
                            ),
                        ),
                    ),
            )
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(92.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.18f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.32f)),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(38.dp),
                        color = Color(0xFFFFD782),
                        strokeWidth = 3.dp,
                    )
                }
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                LoadingPlaceholder(
                    modifier = Modifier.fillMaxWidth(0.7f),
                    height = 28,
                    alpha = 0.24f,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LoadingPlaceholder(
                        modifier = Modifier.fillMaxWidth(0.26f),
                        height = 26,
                        radius = 999,
                        alpha = 0.2f,
                    )
                    LoadingPlaceholder(
                        modifier = Modifier.fillMaxWidth(0.34f),
                        height = 26,
                        radius = 999,
                        alpha = 0.16f,
                    )
                }
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = Color(0xFF101A35).copy(alpha = 0.54f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)),
                ) {
                    Text(
                        text = stringResource(R.string.character_detail_loading),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.86f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun CharacterDetailLoadingSection(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f)),
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF101A35).copy(alpha = 0.9f),
                            Color(0xFF272557).copy(alpha = 0.82f),
                            Color(0xFF3F347F).copy(alpha = 0.7f),
                        ),
                    ),
                )
                .padding(18.dp),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 52.dp, y = (-58).dp)
                    .size(150.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFC7B8FF).copy(alpha = 0.16f),
                                Color.Transparent,
                            ),
                        ),
                        shape = CircleShape,
                    ),
            )
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(11.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFD782).copy(alpha = 0.84f)),
                    )
                    LoadingPlaceholder(
                        modifier = Modifier.fillMaxWidth(0.38f),
                        height = 20,
                        alpha = 0.18f,
                    )
                }
                repeat(3) { index ->
                    LoadingPlaceholder(
                        modifier = Modifier.fillMaxWidth(if (index == 2) 0.72f else 1f),
                        height = 38,
                        radius = 16,
                        alpha = 0.08f,
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingPlaceholder(
    modifier: Modifier = Modifier,
    height: Int,
    radius: Int = 12,
    alpha: Float = 0.14f,
) {
    Box(
        modifier = modifier
            .height(height.dp)
            .clip(RoundedCornerShape(radius.dp))
            .background(Color.White.copy(alpha = alpha)),
    )
}

@Composable
private fun CharacterDetailError(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.padding(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        DetailStatePanel(
            title = stringResource(R.string.character_detail_error_title),
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
private fun CharacterDetailEmptyProfile(modifier: Modifier = Modifier) {
    DetailStatePanel(
        title = stringResource(R.string.character_detail_empty_title),
        message = stringResource(R.string.character_detail_empty_message),
        modifier = modifier,
    )
}

@Composable
private fun DetailStatePanel(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.94f),
        tonalElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary,
                            ),
                        ),
                    ),
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

@Composable
private fun CharacterDetailBackground(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF07152D),
                        Color(0xFF111D3D),
                        Color(0xFF171A3A),
                        Color(0xFF201735),
                    ),
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
                            Color(0xFF5C86FF).copy(alpha = 0.24f),
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
                            Color(0xFFC472FF).copy(alpha = 0.16f),
                            Color.Transparent,
                        ),
                    ),
                    shape = CircleShape,
                ),
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stars = listOf(
                Offset(size.width * 0.08f, size.height * 0.18f),
                Offset(size.width * 0.22f, size.height * 0.08f),
                Offset(size.width * 0.36f, size.height * 0.22f),
                Offset(size.width * 0.54f, size.height * 0.12f),
                Offset(size.width * 0.74f, size.height * 0.20f),
                Offset(size.width * 0.90f, size.height * 0.11f),
                Offset(size.width * 0.16f, size.height * 0.42f),
                Offset(size.width * 0.48f, size.height * 0.38f),
                Offset(size.width * 0.78f, size.height * 0.48f),
                Offset(size.width * 0.24f, size.height * 0.70f),
                Offset(size.width * 0.58f, size.height * 0.78f),
                Offset(size.width * 0.86f, size.height * 0.68f),
            )
            stars.forEachIndexed { index, offset ->
                drawCircle(
                    color = Color.White.copy(alpha = if (index % 2 == 0) 0.48f else 0.32f),
                    radius = if (index % 3 == 0) 1.8.dp.toPx() else 1.1.dp.toPx(),
                    center = offset,
                )
            }
            drawCircle(
                color = Color(0xFFFFD782).copy(alpha = 0.18f),
                radius = 2.8.dp.toPx(),
                center = Offset(size.width * 0.66f, size.height * 0.24f),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CharacterDetailScreenPreview() {
    DisneyAppTheme(dynamicColor = false) {
        CharacterDetailScreen(
            state = CharacterDetailState(
                character = CharacterDetailUi(
                    id = 4703,
                    name = "Mickey Mouse",
                    alignment = "Good",
                    imageUrl = null,
                    metadataBadges = listOf("1 film", "1 short", "1 show"),
                    sections = listOf(
                        CharacterDetailSectionUi("Films", listOf("Fantasia")),
                        CharacterDetailSectionUi("TV shows", listOf("Mickey Mouse Clubhouse")),
                        CharacterDetailSectionUi("Allies", listOf("Minnie Mouse", "Goofy")),
                    ),
                    isFavorite = true,
                ),
            ),
            onAction = {},
            onBackClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CharacterDetailLoadingPreview() {
    DisneyAppTheme(dynamicColor = false) {
        CharacterDetailScreen(
            state = CharacterDetailState(isLoading = true),
            onAction = {},
            onBackClick = {},
        )
    }
}
