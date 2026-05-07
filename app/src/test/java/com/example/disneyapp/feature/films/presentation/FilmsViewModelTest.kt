package com.example.disneyapp.feature.films.presentation

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.example.disneyapp.core.domain.DataError
import com.example.disneyapp.core.domain.Result
import com.example.disneyapp.core.presentation.toUiText
import com.example.disneyapp.feature.characters.domain.model.CharacterDetail
import com.example.disneyapp.feature.characters.domain.model.CharacterPage
import com.example.disneyapp.feature.characters.domain.model.DisneyCharacter
import com.example.disneyapp.feature.characters.domain.repository.CharacterRepository
import com.example.disneyapp.feature.characters.domain.usecase.SearchCharactersUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FilmsViewModelTest {
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
    fun `blank query does not search characters`() = runTest(testDispatcher) {
        val repository = FakeFilmsCharacterRepository()
        val viewModel = createViewModel(repository)

        viewModel.onAction(FilmsAction.OnSearchQueryChange("   "))
        advanceUntilIdle()

        assertThat(repository.requestedSearchQueries).containsExactly()
        assertThat(viewModel.state.value).isEqualTo(
            FilmsState(searchQuery = "   "),
        )
    }

    @Test
    fun `rapid query changes search only latest debounced query`() = runTest(testDispatcher) {
        val repository = FakeFilmsCharacterRepository(
            searchCharactersResult = Result.Success(characterPage(listOf(mickey))),
        )
        val viewModel = createViewModel(repository)

        viewModel.onAction(FilmsAction.OnSearchQueryChange("M"))
        advanceTimeBy(100)
        viewModel.onAction(FilmsAction.OnSearchQueryChange("Mi"))
        advanceTimeBy(100)
        viewModel.onAction(FilmsAction.OnSearchQueryChange(" Mickey "))
        advanceTimeBy(300)
        advanceUntilIdle()

        assertThat(repository.requestedSearchQueries).containsExactly("Mickey")
        assertThat(viewModel.state.value).isEqualTo(
            FilmsState(
                searchQuery = " Mickey ",
                submittedQuery = "Mickey",
                results = listOf(mickeyFilmUi),
            ),
        )
    }

    @Test
    fun `search maps films and short films into results`() = runTest(testDispatcher) {
        val repository = FakeFilmsCharacterRepository(
            searchCharactersResult = Result.Success(characterPage(listOf(mickey, moana))),
        )
        val viewModel = createViewModel(repository)

        viewModel.onAction(FilmsAction.OnSearchQueryChange("m"))
        advanceTimeBy(300)
        advanceUntilIdle()

        assertThat(viewModel.state.value.results).containsExactly(
            mickeyFilmUi,
            moanaFilmUi,
        )
    }

    @Test
    fun `search filters matching characters with no film appearances`() = runTest(testDispatcher) {
        val repository = FakeFilmsCharacterRepository(
            searchCharactersResult = Result.Success(characterPage(listOf(minnie, mickey))),
        )
        val viewModel = createViewModel(repository)

        viewModel.onAction(FilmsAction.OnSearchQueryChange("mouse"))
        advanceTimeBy(300)
        advanceUntilIdle()

        assertThat(viewModel.state.value.results).containsExactly(mickeyFilmUi)
        assertThat(viewModel.state.value.isEmptyResult).isFalse()
    }

    @Test
    fun `search exposes empty result when no film appearances exist`() = runTest(testDispatcher) {
        val repository = FakeFilmsCharacterRepository(
            searchCharactersResult = Result.Success(characterPage(listOf(minnie))),
        )
        val viewModel = createViewModel(repository)

        viewModel.onAction(FilmsAction.OnSearchQueryChange("minnie"))
        advanceTimeBy(300)
        advanceUntilIdle()

        assertThat(viewModel.state.value).isEqualTo(
            FilmsState(
                searchQuery = "minnie",
                submittedQuery = "minnie",
            ),
        )
        assertThat(viewModel.state.value.isEmptyResult).isTrue()
    }

    @Test
    fun `search failure exposes error state`() = runTest(testDispatcher) {
        val repository = FakeFilmsCharacterRepository(
            searchCharactersResult = Result.Failure(DataError.Network.SERVER_ERROR),
        )
        val viewModel = createViewModel(repository)

        viewModel.onAction(FilmsAction.OnSearchQueryChange("mickey"))
        advanceTimeBy(300)
        advanceUntilIdle()

        assertThat(viewModel.state.value).isEqualTo(
            FilmsState(
                searchQuery = "mickey",
                submittedQuery = "mickey",
                error = DataError.Network.SERVER_ERROR.toUiText(),
            ),
        )
    }

    @Test
    fun `character click emits navigate event`() = runTest(testDispatcher) {
        val viewModel = createViewModel(FakeFilmsCharacterRepository())

        viewModel.events.test {
            viewModel.onAction(FilmsAction.OnCharacterClick(4703))

            assertThat(awaitItem()).isEqualTo(FilmsEvent.NavigateToCharacterDetail(4703))
        }
    }

    private fun createViewModel(repository: CharacterRepository): FilmsViewModel =
        FilmsViewModel(
            searchCharactersUseCase = SearchCharactersUseCase(repository),
        )
}

private class FakeFilmsCharacterRepository(
    var searchCharactersResult: Result<CharacterPage, DataError> =
        Result.Success(characterPage(emptyList())),
) : CharacterRepository {
    val requestedSearchQueries = mutableListOf<String>()

    override suspend fun getCharacters(
        page: Int,
        pageSize: Int,
    ): Result<CharacterPage, DataError> =
        Result.Failure(DataError.Network.UNKNOWN)

    override suspend fun getCharacter(id: Int): Result<CharacterDetail, DataError> =
        Result.Failure(DataError.Network.UNKNOWN)

    override suspend fun searchCharacters(
        name: String,
        page: Int,
        pageSize: Int,
    ): Result<CharacterPage, DataError> {
        requestedSearchQueries.add(name)
        return searchCharactersResult
    }
}

private fun characterPage(
    characters: List<DisneyCharacter>,
    currentPage: Int = 1,
    pageSize: Int = FilmsState.DEFAULT_PAGE_SIZE,
): CharacterPage =
    CharacterPage(
        characters = characters,
        currentPage = currentPage,
        pageSize = pageSize,
        totalPages = currentPage,
        hasNextPage = false,
    )

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
    videoGames = emptyList(),
    parkAttractions = emptyList(),
    allies = emptyList(),
    enemies = emptyList(),
)

private val moana = DisneyCharacter(
    id = 12,
    name = "Moana",
    alignment = null,
    imageUrl = null,
    sourceUrl = null,
    apiUrl = "https://api.disneyapi.dev/characters/12",
    films = listOf("Moana", "Moana 2"),
    shortFilms = emptyList(),
    tvShows = emptyList(),
    videoGames = emptyList(),
    parkAttractions = emptyList(),
    allies = emptyList(),
    enemies = emptyList(),
)

private val minnie = DisneyCharacter(
    id = 1949,
    name = "Minnie Mouse",
    alignment = null,
    imageUrl = null,
    sourceUrl = null,
    apiUrl = "https://api.disneyapi.dev/characters/1949",
    films = emptyList(),
    shortFilms = emptyList(),
    tvShows = listOf("Mickey Mouse Clubhouse"),
    videoGames = emptyList(),
    parkAttractions = emptyList(),
    allies = emptyList(),
    enemies = emptyList(),
)

private val mickeyFilmUi = FilmCharacterUi(
    characterId = 4703,
    characterName = "Mickey Mouse",
    imageUrl = "https://example.com/mickey.jpg",
    films = listOf("Fantasia"),
    shortFilms = listOf("Steamboat Willie"),
)

private val moanaFilmUi = FilmCharacterUi(
    characterId = 12,
    characterName = "Moana",
    imageUrl = null,
    films = listOf("Moana", "Moana 2"),
    shortFilms = emptyList(),
)
