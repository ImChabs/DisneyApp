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
import com.example.disneyapp.feature.characters.FakeFavoriteCharacterLocalDataSource
import com.example.disneyapp.feature.characters.domain.model.CharacterDetail
import com.example.disneyapp.feature.characters.domain.model.CharacterPage
import com.example.disneyapp.feature.characters.domain.model.DisneyCharacter
import com.example.disneyapp.feature.characters.domain.repository.CharacterRepository
import com.example.disneyapp.feature.characters.domain.usecase.GetCharactersUseCase
import com.example.disneyapp.feature.characters.domain.usecase.ObserveFavoriteCharacterIdsUseCase
import com.example.disneyapp.feature.characters.domain.usecase.SearchCharactersUseCase
import com.example.disneyapp.feature.characters.domain.usecase.ToggleFavoriteCharacterUseCase
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
        val pendingResult = CompletableDeferred<Result<CharacterPage, DataError.Network>>()
        val repository = FakeCharacterRepository(
            getCharactersRequest = { _, _ -> pendingResult.await() },
        )
        val viewModel = createViewModel(repository)

        viewModel.state.test {
            assertThat(awaitItem().isLoading).isFalse()

            runCurrent()
            assertThat(awaitItem().isLoading).isTrue()

            pendingResult.complete(Result.Success(characterPage(listOf(mickey))))
            assertThat(awaitItem()).isEqualTo(
                CharacterListState(
                    characters = listOf(mickeyListItem),
                    currentPage = 1,
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
            getCharactersResult = Result.Success(characterPage(listOf(mickey))),
            searchCharactersResult = Result.Success(characterPage(listOf(minnie))),
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
                currentPage = 1,
            ),
        )
    }

    @Test
    fun `query change updates state immediately before debounced search runs`() = runTest(testDispatcher) {
        val repository = FakeCharacterRepository(
            getCharactersResult = Result.Success(characterPage(listOf(mickey))),
            searchCharactersResult = Result.Success(characterPage(listOf(minnie))),
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
                currentPage = 0,
            ),
        )
    }

    @Test
    fun `rapid query changes search only latest debounced query`() = runTest(testDispatcher) {
        val repository = FakeCharacterRepository(
            getCharactersResult = Result.Success(characterPage(listOf(mickey))),
            searchCharactersResult = Result.Success(characterPage(listOf(minnie))),
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
                currentPage = 1,
            ),
        )
    }

    @Test
    fun `clearing search query restores full character list`() = runTest(testDispatcher) {
        val repository = FakeCharacterRepository(
            getCharactersResult = Result.Success(characterPage(listOf(mickey))),
            searchCharactersResult = Result.Success(characterPage(listOf(minnie))),
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
                currentPage = 1,
            ),
        )
    }

    @Test
    fun `search failure preserves previous characters`() = runTest(testDispatcher) {
        val repository = FakeCharacterRepository(
            getCharactersResult = Result.Success(characterPage(listOf(mickey))),
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
                currentPage = 0,
                error = DataError.Network.SERVER_ERROR.toUiText(),
            ),
        )
    }

    @Test
    fun `changing query cancels in flight search result`() = runTest(testDispatcher) {
        val pendingSearch = CompletableDeferred<Result<CharacterPage, DataError.Network>>()
        val repository = FakeCharacterRepository(
            getCharactersResult = Result.Success(characterPage(listOf(mickey))),
            searchCharactersResult = Result.Success(characterPage(listOf(minnie))),
            searchCharactersRequest = { query, _, _ ->
                if (query == "Min") {
                    pendingSearch.await()
                } else {
                    Result.Success(characterPage(emptyList()))
                }
            },
        )
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        viewModel.onAction(CharacterListAction.OnSearchQueryChange("Min"))
        advanceTimeBy(300)
        runCurrent()
        viewModel.onAction(CharacterListAction.OnSearchQueryChange("zzzzzzzz"))
        pendingSearch.complete(Result.Success(characterPage(listOf(minnie))))
        runCurrent()

        assertThat(viewModel.state.value).isEqualTo(
            CharacterListState(
                characters = listOf(mickeyListItem),
                searchQuery = "zzzzzzzz",
                currentPage = 0,
            ),
        )

        advanceTimeBy(300)
        advanceUntilIdle()

        assertThat(repository.requestedSearchQueries).containsExactly("Min", "zzzzzzzz")
        assertThat(viewModel.state.value).isEqualTo(
            CharacterListState(
                searchQuery = "zzzzzzzz",
                currentPage = 1,
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
        repository.searchCharactersResult = Result.Success(characterPage(listOf(minnie)))

        viewModel.onAction(CharacterListAction.OnRetryClick)
        advanceUntilIdle()

        assertThat(repository.requestedSearchQueries).containsExactly("Minnie", "Minnie")
        assertThat(viewModel.state.value).isEqualTo(
            CharacterListState(
                characters = listOf(minnieListItem),
                searchQuery = "Minnie",
                currentPage = 1,
            ),
        )
    }

    @Test
    fun `load more appends next page and updates pagination state`() = runTest(testDispatcher) {
        val repository = FakeCharacterRepository(
            getCharactersRequest = { page, pageSize ->
                when (page) {
                    1 -> Result.Success(
                        characterPage(
                            characters = listOf(mickey),
                            currentPage = page,
                            pageSize = pageSize,
                            hasNextPage = true,
                        )
                    )
                    else -> Result.Success(
                        characterPage(
                            characters = listOf(minnie),
                            currentPage = page,
                            pageSize = pageSize,
                            hasNextPage = false,
                        )
                    )
                }
            },
        )
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        viewModel.onAction(CharacterListAction.OnLoadMore)
        advanceUntilIdle()

        assertThat(repository.requestedCharacterPages).containsExactly(1, 2)
        assertThat(viewModel.state.value).isEqualTo(
            CharacterListState(
                characters = listOf(mickeyListItem, minnieListItem),
                currentPage = 2,
                canLoadMore = false,
            ),
        )
    }

    @Test
    fun `load more ignores duplicate requests while already loading more`() = runTest(testDispatcher) {
        val pendingPage = CompletableDeferred<Result<CharacterPage, DataError.Network>>()
        val repository = FakeCharacterRepository(
            getCharactersRequest = { page, pageSize ->
                when (page) {
                    1 -> Result.Success(
                        characterPage(
                            characters = listOf(mickey),
                            currentPage = page,
                            pageSize = pageSize,
                            hasNextPage = true,
                        )
                    )
                    else -> pendingPage.await()
                }
            },
        )
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        viewModel.onAction(CharacterListAction.OnLoadMore)
        viewModel.onAction(CharacterListAction.OnLoadMore)
        runCurrent()

        assertThat(repository.requestedCharacterPages).containsExactly(1, 2)
        assertThat(viewModel.state.value.isLoadingMore).isTrue()

        pendingPage.complete(
            Result.Success(
                characterPage(
                    characters = listOf(minnie),
                    currentPage = 2,
                    hasNextPage = false,
                )
            )
        )
        advanceUntilIdle()

        assertThat(viewModel.state.value.characters).containsExactly(mickeyListItem, minnieListItem)
    }

    @Test
    fun `load more failure preserves existing characters`() = runTest(testDispatcher) {
        val repository = FakeCharacterRepository(
            getCharactersRequest = { page, pageSize ->
                when (page) {
                    1 -> Result.Success(
                        characterPage(
                            characters = listOf(mickey),
                            currentPage = page,
                            pageSize = pageSize,
                            hasNextPage = true,
                        )
                    )
                    else -> Result.Failure(DataError.Network.SERVER_ERROR)
                }
            },
        )
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        viewModel.onAction(CharacterListAction.OnLoadMore)
        advanceUntilIdle()

        assertThat(viewModel.state.value).isEqualTo(
            CharacterListState(
                characters = listOf(mickeyListItem),
                currentPage = 1,
                canLoadMore = true,
                error = DataError.Network.SERVER_ERROR.toUiText(),
            ),
        )
    }

    @Test
    fun `favorite ids mark loaded characters as favorites`() = runTest(testDispatcher) {
        val repository = FakeCharacterRepository(
            getCharactersResult = Result.Success(characterPage(listOf(mickey, minnie))),
        )
        val favorites = FakeFavoriteCharacterLocalDataSource(initialFavorites = listOf(mickey))
        val viewModel = createViewModel(repository, favorites)

        advanceUntilIdle()

        assertThat(viewModel.state.value.characters).containsExactly(
            mickeyListItem.copy(isFavorite = true),
            minnieListItem,
        )
    }

    @Test
    fun `favorite click saves loaded character snapshot`() = runTest(testDispatcher) {
        val repository = FakeCharacterRepository(
            getCharactersResult = Result.Success(characterPage(listOf(mickey))),
        )
        val favorites = FakeFavoriteCharacterLocalDataSource()
        val viewModel = createViewModel(repository, favorites)
        advanceUntilIdle()

        viewModel.onAction(CharacterListAction.OnFavoriteClick(4703))
        advanceUntilIdle()

        assertThat(favorites.savedFavorites).containsExactly(mickey)
        assertThat(viewModel.state.value.characters).containsExactly(
            mickeyListItem.copy(isFavorite = true),
        )
    }

    @Test
    fun `cached initial load emits snackbar and displays characters`() = runTest(testDispatcher) {
        val repository = FakeCharacterRepository(
            getCharactersResult = Result.Success(
                characterPage(listOf(mickey)).copy(isFromCache = true),
            ),
        )
        val viewModel = createViewModel(repository)

        viewModel.events.test {
            advanceUntilIdle()

            val event = awaitItem()
            assertThat(event).isEqualTo(
                CharacterListEvent.ShowSnackbar(
                    com.example.disneyapp.core.presentation.UiText.DynamicString(
                        "Showing saved characters.",
                    )
                )
            )
        }
        assertThat(viewModel.state.value.characters).containsExactly(mickeyListItem)
    }

    private fun createViewModel(
        repository: CharacterRepository,
        favoriteLocalDataSource: FakeFavoriteCharacterLocalDataSource = FakeFavoriteCharacterLocalDataSource(),
    ): CharacterListViewModel =
        CharacterListViewModel(
            getCharactersUseCase = GetCharactersUseCase(repository),
            searchCharactersUseCase = SearchCharactersUseCase(repository),
            observeFavoriteCharacterIdsUseCase = ObserveFavoriteCharacterIdsUseCase(favoriteLocalDataSource),
            toggleFavoriteCharacterUseCase = ToggleFavoriteCharacterUseCase(favoriteLocalDataSource),
        )
}

private class FakeCharacterRepository(
    var getCharactersResult: Result<CharacterPage, DataError.Network> =
        Result.Success(characterPage(emptyList())),
    private val getCharacterResult: Result<CharacterDetail, DataError> =
        Result.Failure(DataError.Network.UNKNOWN),
    var searchCharactersResult: Result<CharacterPage, DataError.Network> =
        Result.Success(characterPage(emptyList())),
    var getCharactersRequest: suspend (Int, Int) -> Result<CharacterPage, DataError.Network> = { _, _ ->
        getCharactersResult
    },
    var searchCharactersRequest: (suspend (String, Int, Int) -> Result<CharacterPage, DataError.Network>)? = null,
) : CharacterRepository {
    var getCharactersCallCount = 0
        private set

    val requestedCharacterPages = mutableListOf<Int>()
    val requestedSearchQueries = mutableListOf<String>()

    override suspend fun getCharacters(
        page: Int,
        pageSize: Int,
    ): Result<CharacterPage, DataError.Network> {
        getCharactersCallCount++
        requestedCharacterPages.add(page)
        return getCharactersRequest(page, pageSize)
    }

    override suspend fun getCharacter(id: Int): Result<CharacterDetail, DataError> =
        getCharacterResult

    override suspend fun searchCharacters(
        name: String,
        page: Int,
        pageSize: Int,
    ): Result<CharacterPage, DataError.Network> {
        requestedSearchQueries.add(name)
        return searchCharactersRequest?.invoke(name, page, pageSize) ?: searchCharactersResult
    }
}

private fun characterPage(
    characters: List<DisneyCharacter>,
    currentPage: Int = 1,
    pageSize: Int = CharacterListState.DEFAULT_PAGE_SIZE,
    hasNextPage: Boolean = false,
): CharacterPage =
    CharacterPage(
        characters = characters,
        currentPage = currentPage,
        pageSize = pageSize,
        totalPages = currentPage,
        hasNextPage = hasNextPage,
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
    videoGames = listOf("Kingdom Hearts"),
    parkAttractions = listOf("Mickey's PhilharMagic"),
    allies = listOf("Minnie Mouse"),
    enemies = listOf("Pete"),
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
