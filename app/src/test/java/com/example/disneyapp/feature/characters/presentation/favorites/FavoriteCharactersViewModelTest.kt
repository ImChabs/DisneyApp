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
