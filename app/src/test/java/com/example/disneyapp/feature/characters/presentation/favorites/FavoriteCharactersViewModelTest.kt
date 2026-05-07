package com.example.disneyapp.feature.characters.presentation.favorites

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import com.example.disneyapp.feature.characters.FakeFavoriteCharacterLocalDataSource
import com.example.disneyapp.feature.characters.domain.model.DisneyCharacter
import com.example.disneyapp.feature.characters.domain.usecase.ObserveFavoriteCharactersUseCase
import com.example.disneyapp.feature.characters.domain.usecase.RemoveFavoriteCharacterUseCase
import com.example.disneyapp.feature.characters.presentation.list.CharacterListItemUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteCharactersViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `favorites are exposed as favorite ui items`() = runTest(testDispatcher) {
        val viewModel = createViewModel(
            FakeFavoriteCharacterLocalDataSource(initialFavorites = listOf(mickey)),
        )

        advanceUntilIdle()

        assertThat(viewModel.state.value).isEqualTo(
            FavoriteCharactersState(favorites = listOf(mickeyListItem.copy(isFavorite = true))),
        )
    }

    @Test
    fun `favorite click removes favorite`() = runTest(testDispatcher) {
        val favorites = FakeFavoriteCharacterLocalDataSource(initialFavorites = listOf(mickey))
        val viewModel = createViewModel(favorites)
        advanceUntilIdle()

        viewModel.onAction(FavoriteCharactersAction.OnFavoriteClick(4703))
        advanceUntilIdle()

        assertThat(favorites.removedFavoriteIds).containsExactly(4703)
        assertThat(viewModel.state.value).isEqualTo(FavoriteCharactersState())
    }

    @Test
    fun `search query filters favorites by name`() = runTest(testDispatcher) {
        val viewModel = createViewModel(
            FakeFavoriteCharacterLocalDataSource(initialFavorites = listOf(mickey, minnie, donald)),
        )
        advanceUntilIdle()

        viewModel.onAction(FavoriteCharactersAction.OnSearchQueryChange("minnie"))

        assertThat(viewModel.state.value).isEqualTo(
            FavoriteCharactersState(
                favorites = listOf(minnieListItem.copy(isFavorite = true)),
                searchQuery = "minnie",
                totalFavoritesCount = 3,
            ),
        )
    }

    @Test
    fun `search query is case insensitive`() = runTest(testDispatcher) {
        val viewModel = createViewModel(
            FakeFavoriteCharacterLocalDataSource(initialFavorites = listOf(mickey, minnie)),
        )
        advanceUntilIdle()

        viewModel.onAction(FavoriteCharactersAction.OnSearchQueryChange("MICKEY"))

        assertThat(viewModel.state.value).isEqualTo(
            FavoriteCharactersState(
                favorites = listOf(mickeyListItem.copy(isFavorite = true)),
                searchQuery = "MICKEY",
                totalFavoritesCount = 2,
            ),
        )
    }

    @Test
    fun `clearing search query shows all favorites`() = runTest(testDispatcher) {
        val viewModel = createViewModel(
            FakeFavoriteCharacterLocalDataSource(initialFavorites = listOf(mickey, minnie)),
        )
        advanceUntilIdle()

        viewModel.onAction(FavoriteCharactersAction.OnSearchQueryChange("mick"))
        viewModel.onAction(FavoriteCharactersAction.OnSearchQueryChange(""))

        assertThat(viewModel.state.value).isEqualTo(
            FavoriteCharactersState(
                favorites = listOf(
                    mickeyListItem.copy(isFavorite = true),
                    minnieListItem.copy(isFavorite = true),
                ),
            ),
        )
    }

    @Test
    fun `favorites updates remain filtered when search query is active`() = runTest(testDispatcher) {
        val favorites = FakeFavoriteCharacterLocalDataSource(initialFavorites = listOf(mickey))
        val viewModel = createViewModel(favorites)
        advanceUntilIdle()

        viewModel.onAction(FavoriteCharactersAction.OnSearchQueryChange("min"))
        favorites.saveFavorite(minnie)
        advanceUntilIdle()

        assertThat(viewModel.state.value).isEqualTo(
            FavoriteCharactersState(
                favorites = listOf(minnieListItem.copy(isFavorite = true)),
                searchQuery = "min",
                totalFavoritesCount = 2,
            ),
        )
    }

    private fun createViewModel(
        favoriteLocalDataSource: FakeFavoriteCharacterLocalDataSource,
    ): FavoriteCharactersViewModel =
        FavoriteCharactersViewModel(
            observeFavoriteCharactersUseCase = ObserveFavoriteCharactersUseCase(favoriteLocalDataSource),
            removeFavoriteCharacterUseCase = RemoveFavoriteCharacterUseCase(favoriteLocalDataSource),
        )
}

private val mickey = DisneyCharacter(
    id = 4703,
    name = "Mickey Mouse",
    alignment = "Good",
    imageUrl = "https://example.com/mickey.jpg",
    sourceUrl = "https://disney.fandom.com/wiki/Mickey_Mouse",
    apiUrl = "https://api.disneyapi.dev/characters/4703",
    films = listOf("Fantasia"),
    shortFilms = listOf("Steamboat Willie"),
    tvShows = listOf("Mickey Mouse Clubhouse"),
    videoGames = listOf("Kingdom Hearts"),
    parkAttractions = listOf("Mickey's PhilharMagic"),
    allies = listOf("Minnie Mouse"),
    enemies = listOf("Pete"),
)

private val mickeyListItem = CharacterListItemUi(
    id = 4703,
    name = "Mickey Mouse",
    imageUrl = "https://example.com/mickey.jpg",
    metadataBadges = listOf("1 film", "1 short", "1 show"),
)

private val minnie = mickey.copy(
    id = 4704,
    name = "Minnie Mouse",
    imageUrl = "https://example.com/minnie.jpg",
)

private val minnieListItem = mickeyListItem.copy(
    id = 4704,
    name = "Minnie Mouse",
    imageUrl = "https://example.com/minnie.jpg",
)

private val donald = mickey.copy(
    id = 4705,
    name = "Donald Duck",
    imageUrl = "https://example.com/donald.jpg",
)
