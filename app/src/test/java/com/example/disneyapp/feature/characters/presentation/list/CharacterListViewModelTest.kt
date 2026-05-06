package com.example.disneyapp.feature.characters.presentation.list

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.example.disneyapp.core.domain.DataError
import com.example.disneyapp.core.domain.Result
import com.example.disneyapp.core.presentation.toUiText
import com.example.disneyapp.feature.characters.domain.model.DisneyCharacter
import com.example.disneyapp.feature.characters.domain.repository.CharacterRepository
import com.example.disneyapp.feature.characters.domain.usecase.GetCharactersUseCase
import com.example.disneyapp.feature.characters.domain.usecase.SearchCharactersUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterListViewModelTest {
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
    fun `initial load shows loading then characters`() = runTest(testDispatcher) {
        val pendingResult = CompletableDeferred<Result<List<DisneyCharacter>, DataError.Network>>()
        val repository = FakeCharacterRepository(
            getCharactersRequest = { pendingResult.await() },
        )
        val viewModel = createViewModel(repository)

        viewModel.state.test {
            assertThat(awaitItem().isLoading).isFalse()

            runCurrent()
            assertThat(awaitItem().isLoading).isTrue()

            pendingResult.complete(Result.Success(listOf(mickey)))
            assertThat(awaitItem()).isEqualTo(
                CharacterListState(
                    characters = listOf(mickeyListItem),
                    isLoading = false,
                ),
            )
        }
    }

    @Test
    fun `initial load failure shows error state`() = runTest(testDispatcher) {
        val repository = FakeCharacterRepository(
            getCharactersResult = Result.Failure(DataError.Network.NO_INTERNET),
        )
        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        assertThat(viewModel.state.value).isEqualTo(
            CharacterListState(
                error = DataError.Network.NO_INTERNET.toUiText(),
            ),
        )
    }

    @Test
    fun `initial empty success exposes empty state`() = runTest(testDispatcher) {
        val viewModel = createViewModel(FakeCharacterRepository())

        advanceUntilIdle()

        assertThat(viewModel.state.value.isEmpty).isTrue()
        assertThat(viewModel.state.value.error).isEqualTo(null)
    }

    @Test
    fun `nonblank query searches characters`() = runTest(testDispatcher) {
        val repository = FakeCharacterRepository(
            getCharactersResult = Result.Success(listOf(mickey)),
            searchCharactersResult = Result.Success(listOf(minnie)),
        )
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        viewModel.onAction(CharacterListAction.OnSearchQueryChange("  Minnie Mouse  "))
        advanceUntilIdle()

        assertThat(repository.requestedSearchQueries).containsExactly("Minnie Mouse")
        assertThat(viewModel.state.value).isEqualTo(
            CharacterListState(
                characters = listOf(minnieListItem),
                searchQuery = "  Minnie Mouse  ",
            ),
        )
    }

    @Test
    fun `query change updates state immediately before debounced search runs`() = runTest(testDispatcher) {
        val repository = FakeCharacterRepository(
            getCharactersResult = Result.Success(listOf(mickey)),
            searchCharactersResult = Result.Success(listOf(minnie)),
        )
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        viewModel.onAction(CharacterListAction.OnSearchQueryChange("Minnie"))
        runCurrent()

        assertThat(repository.requestedSearchQueries).containsExactly()
        assertThat(viewModel.state.value).isEqualTo(
            CharacterListState(
                characters = listOf(mickeyListItem),
                searchQuery = "Minnie",
            ),
        )
    }

    @Test
    fun `rapid query changes search only latest debounced query`() = runTest(testDispatcher) {
        val repository = FakeCharacterRepository(
            getCharactersResult = Result.Success(listOf(mickey)),
            searchCharactersResult = Result.Success(listOf(minnie)),
        )
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        viewModel.onAction(CharacterListAction.OnSearchQueryChange("M"))
        advanceTimeBy(100)
        viewModel.onAction(CharacterListAction.OnSearchQueryChange("Mi"))
        advanceTimeBy(100)
        viewModel.onAction(CharacterListAction.OnSearchQueryChange("Min"))
        advanceTimeBy(300)
        advanceUntilIdle()

        assertThat(repository.requestedSearchQueries).containsExactly("Min")
        assertThat(viewModel.state.value).isEqualTo(
            CharacterListState(
                characters = listOf(minnieListItem),
                searchQuery = "Min",
            ),
        )
    }

    @Test
    fun `clearing search query restores full character list`() = runTest(testDispatcher) {
        val repository = FakeCharacterRepository(
            getCharactersResult = Result.Success(listOf(mickey)),
            searchCharactersResult = Result.Success(listOf(minnie)),
        )
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        viewModel.onAction(CharacterListAction.OnSearchQueryChange("Minnie"))
        advanceUntilIdle()
        viewModel.onAction(CharacterListAction.OnSearchQueryChange(""))
        advanceUntilIdle()

        assertThat(repository.getCharactersCallCount).isEqualTo(2)
        assertThat(viewModel.state.value).isEqualTo(
            CharacterListState(
                characters = listOf(mickeyListItem),
            ),
        )
    }

    @Test
    fun `search failure preserves previous characters`() = runTest(testDispatcher) {
        val repository = FakeCharacterRepository(
            getCharactersResult = Result.Success(listOf(mickey)),
            searchCharactersResult = Result.Failure(DataError.Network.SERVER_ERROR),
        )
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        viewModel.onAction(CharacterListAction.OnSearchQueryChange("Minnie"))
        advanceUntilIdle()

        assertThat(viewModel.state.value).isEqualTo(
            CharacterListState(
                characters = listOf(mickeyListItem),
                searchQuery = "Minnie",
                error = DataError.Network.SERVER_ERROR.toUiText(),
            ),
        )
    }

    @Test
    fun `changing query cancels in flight search result`() = runTest(testDispatcher) {
        val pendingSearch = CompletableDeferred<Result<List<DisneyCharacter>, DataError.Network>>()
        val repository = FakeCharacterRepository(
            getCharactersResult = Result.Success(listOf(mickey)),
            searchCharactersResult = Result.Success(listOf(minnie)),
            searchCharactersRequest = { query ->
                if (query == "Min") {
                    pendingSearch.await()
                } else {
                    Result.Success(emptyList())
                }
            },
        )
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        viewModel.onAction(CharacterListAction.OnSearchQueryChange("Min"))
        advanceTimeBy(300)
        runCurrent()
        viewModel.onAction(CharacterListAction.OnSearchQueryChange("zzzzzzzz"))
        pendingSearch.complete(Result.Success(listOf(minnie)))
        runCurrent()

        assertThat(viewModel.state.value).isEqualTo(
            CharacterListState(
                characters = listOf(mickeyListItem),
                searchQuery = "zzzzzzzz",
            ),
        )

        advanceTimeBy(300)
        advanceUntilIdle()

        assertThat(repository.requestedSearchQueries).containsExactly("Min", "zzzzzzzz")
        assertThat(viewModel.state.value).isEqualTo(
            CharacterListState(
                searchQuery = "zzzzzzzz",
            ),
        )
    }

    @Test
    fun `retry uses current search query mode`() = runTest(testDispatcher) {
        val repository = FakeCharacterRepository(
            searchCharactersResult = Result.Failure(DataError.Network.SERVER_ERROR),
        )
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        viewModel.onAction(CharacterListAction.OnSearchQueryChange("Minnie"))
        advanceUntilIdle()
        repository.searchCharactersResult = Result.Success(listOf(minnie))

        viewModel.onAction(CharacterListAction.OnRetryClick)
        advanceUntilIdle()

        assertThat(repository.requestedSearchQueries).containsExactly("Minnie", "Minnie")
        assertThat(viewModel.state.value).isEqualTo(
            CharacterListState(
                characters = listOf(minnieListItem),
                searchQuery = "Minnie",
            ),
        )
    }

    private fun createViewModel(
        repository: CharacterRepository,
    ): CharacterListViewModel =
        CharacterListViewModel(
            getCharactersUseCase = GetCharactersUseCase(repository),
            searchCharactersUseCase = SearchCharactersUseCase(repository),
        )
}

private class FakeCharacterRepository(
    var getCharactersResult: Result<List<DisneyCharacter>, DataError.Network> =
        Result.Success(emptyList()),
    private val getCharacterResult: Result<DisneyCharacter, DataError.Network> =
        Result.Failure(DataError.Network.UNKNOWN),
    var searchCharactersResult: Result<List<DisneyCharacter>, DataError.Network> =
        Result.Success(emptyList()),
    var getCharactersRequest: suspend () -> Result<List<DisneyCharacter>, DataError.Network> = {
        getCharactersResult
    },
    var searchCharactersRequest: (suspend (String) -> Result<List<DisneyCharacter>, DataError.Network>)? = null,
) : CharacterRepository {
    var getCharactersCallCount = 0
        private set

    val requestedSearchQueries = mutableListOf<String>()

    override suspend fun getCharacters(): Result<List<DisneyCharacter>, DataError.Network> {
        getCharactersCallCount++
        return getCharactersRequest()
    }

    override suspend fun getCharacter(id: Int): Result<DisneyCharacter, DataError.Network> =
        getCharacterResult

    override suspend fun searchCharacters(
        name: String,
    ): Result<List<DisneyCharacter>, DataError.Network> {
        requestedSearchQueries.add(name)
        return searchCharactersRequest?.invoke(name) ?: searchCharactersResult
    }
}

private val mickey = DisneyCharacter(
    id = 4703,
    name = "Mickey Mouse",
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

private val minnie = DisneyCharacter(
    id = 1949,
    name = "Minnie Mouse",
    imageUrl = null,
    sourceUrl = null,
    apiUrl = "https://api.disneyapi.dev/characters/1949",
    films = emptyList(),
    shortFilms = emptyList(),
    tvShows = emptyList(),
    videoGames = emptyList(),
    parkAttractions = emptyList(),
    allies = emptyList(),
    enemies = emptyList(),
)

private val mickeyListItem = CharacterListItemUi(
    id = 4703,
    name = "Mickey Mouse",
    imageUrl = "https://example.com/mickey.jpg",
    metadataBadges = listOf("1 film", "1 short", "1 show"),
)

private val minnieListItem = CharacterListItemUi(
    id = 1949,
    name = "Minnie Mouse",
    imageUrl = null,
    metadataBadges = emptyList(),
)
