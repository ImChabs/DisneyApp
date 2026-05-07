package com.example.disneyapp.feature.characters.presentation.detail

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
import com.example.disneyapp.feature.characters.domain.usecase.GetCharacterDetailUseCase
import com.example.disneyapp.feature.characters.domain.usecase.ObserveIsFavoriteCharacterUseCase
import com.example.disneyapp.feature.characters.domain.usecase.ToggleFavoriteCharacterUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterDetailViewModelTest {
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
    fun `initial load shows loading then character detail`() = runTest(testDispatcher) {
        val pendingResult = CompletableDeferred<Result<CharacterDetail, DataError>>()
        val repository = FakeCharacterRepository(
            getCharacterRequest = { pendingResult.await() },
        )
        val viewModel = createViewModel(repository)

        runCurrent()
        assertThat(viewModel.state.value.isLoading).isTrue()

        pendingResult.complete(Result.Success(CharacterDetail(mickey)))
        advanceUntilIdle()

        assertThat(repository.requestedCharacterIds).containsExactly(4703)
        assertThat(viewModel.state.value).isEqualTo(
            CharacterDetailState(
                character = mickeyDetail,
                isLoading = false,
            ),
        )
    }

    @Test
    fun `initial load failure shows mapped error`() = runTest(testDispatcher) {
        val repository = FakeCharacterRepository(
            getCharacterResult = Result.Failure(DataError.Network.NO_INTERNET),
        )
        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        assertThat(viewModel.state.value).isEqualTo(
            CharacterDetailState(
                error = DataError.Network.NO_INTERNET.toUiText(),
            ),
        )
    }

    @Test
    fun `retry loads the same character id again`() = runTest(testDispatcher) {
        val repository = FakeCharacterRepository(
            getCharacterResult = Result.Failure(DataError.Network.SERVER_ERROR),
        )
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        repository.getCharacterResult = Result.Success(CharacterDetail(mickey))
        viewModel.onAction(CharacterDetailAction.OnRetryClick)
        advanceUntilIdle()

        assertThat(repository.requestedCharacterIds).containsExactly(4703, 4703)
        assertThat(viewModel.state.value.character).isEqualTo(mickeyDetail)
        assertThat(viewModel.state.value.error).isEqualTo(null)
        assertThat(viewModel.state.value.isLoading).isFalse()
    }

    @Test
    fun `missing fields produce safe detail ui`() = runTest(testDispatcher) {
        val repository = FakeCharacterRepository(
            getCharacterResult = Result.Success(CharacterDetail(emptyCharacter)),
        )
        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        assertThat(viewModel.state.value.character).isEqualTo(
            CharacterDetailUi(
                id = 0,
                name = "Unknown character",
                alignment = null,
                imageUrl = null,
                metadataBadges = emptyList(),
                sections = emptyList(),
            ),
        )
    }

    @Test
    fun `favorite state marks loaded detail as favorite`() = runTest(testDispatcher) {
        val repository = FakeCharacterRepository(
            getCharacterResult = Result.Success(CharacterDetail(mickey)),
        )
        val favorites = FakeFavoriteCharacterLocalDataSource(initialFavorites = listOf(mickey))
        val viewModel = createViewModel(repository, favorites)

        advanceUntilIdle()

        assertThat(viewModel.state.value.character).isEqualTo(mickeyDetail.copy(isFavorite = true))
    }

    @Test
    fun `favorite click saves loaded character`() = runTest(testDispatcher) {
        val repository = FakeCharacterRepository(
            getCharacterResult = Result.Success(CharacterDetail(mickey)),
        )
        val favorites = FakeFavoriteCharacterLocalDataSource()
        val viewModel = createViewModel(repository, favorites)
        advanceUntilIdle()

        viewModel.onAction(CharacterDetailAction.OnFavoriteClick)
        advanceUntilIdle()

        assertThat(favorites.savedFavorites).containsExactly(mickey)
        assertThat(viewModel.state.value.character).isEqualTo(mickeyDetail.copy(isFavorite = true))
    }

    @Test
    fun `cached detail emits snackbar and displays character`() = runTest(testDispatcher) {
        val repository = FakeCharacterRepository(
            getCharacterResult = Result.Success(CharacterDetail(mickey, isFromCache = true)),
        )
        val viewModel = createViewModel(repository)

        viewModel.events.test {
            advanceUntilIdle()

            val event = awaitItem()
            assertThat(event).isEqualTo(
                CharacterDetailEvent.ShowSnackbar(
                    com.example.disneyapp.core.presentation.UiText.DynamicString(
                        "Showing saved character.",
                    )
                )
            )
        }
        assertThat(viewModel.state.value.character).isEqualTo(mickeyDetail)
    }

    private fun createViewModel(
        repository: CharacterRepository,
        favoriteLocalDataSource: FakeFavoriteCharacterLocalDataSource = FakeFavoriteCharacterLocalDataSource(),
    ): CharacterDetailViewModel =
        CharacterDetailViewModel(
            characterId = 4703,
            getCharacterDetailUseCase = GetCharacterDetailUseCase(repository),
            observeIsFavoriteCharacterUseCase = ObserveIsFavoriteCharacterUseCase(favoriteLocalDataSource),
            toggleFavoriteCharacterUseCase = ToggleFavoriteCharacterUseCase(favoriteLocalDataSource),
        )
}

private class FakeCharacterRepository(
    var getCharacterResult: Result<CharacterDetail, DataError> =
        Result.Success(CharacterDetail(mickey)),
    var getCharacterRequest: (suspend (Int) -> Result<CharacterDetail, DataError>)? = null,
) : CharacterRepository {
    val requestedCharacterIds = mutableListOf<Int>()

    override suspend fun getCharacters(
        page: Int,
        pageSize: Int,
    ): Result<CharacterPage, DataError> =
        Result.Failure(DataError.Network.UNKNOWN)

    override suspend fun getCharacter(id: Int): Result<CharacterDetail, DataError> {
        requestedCharacterIds.add(id)
        return getCharacterRequest?.invoke(id) ?: getCharacterResult
    }

    override suspend fun searchCharacters(
        name: String,
        page: Int,
        pageSize: Int,
    ): Result<CharacterPage, DataError> =
        Result.Failure(DataError.Network.UNKNOWN)
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

private val emptyCharacter = DisneyCharacter(
    id = 0,
    name = null,
    alignment = null,
    imageUrl = null,
    sourceUrl = null,
    apiUrl = null,
    films = emptyList(),
    shortFilms = emptyList(),
    tvShows = emptyList(),
    videoGames = emptyList(),
    parkAttractions = emptyList(),
    allies = emptyList(),
    enemies = emptyList(),
)

private val mickeyDetail = CharacterDetailUi(
    id = 4703,
    name = "Mickey Mouse",
    alignment = "Good",
    imageUrl = "https://example.com/mickey.jpg",
    metadataBadges = listOf("1 film", "1 short", "1 show", "1 game"),
    sections = listOf(
        CharacterDetailSectionUi("Films", listOf("Fantasia")),
        CharacterDetailSectionUi("Short films", listOf("Steamboat Willie")),
        CharacterDetailSectionUi("TV shows", listOf("Mickey Mouse Clubhouse")),
        CharacterDetailSectionUi("Video games", listOf("Kingdom Hearts")),
        CharacterDetailSectionUi("Park attractions", listOf("Mickey's PhilharMagic")),
        CharacterDetailSectionUi("Allies", listOf("Minnie Mouse")),
        CharacterDetailSectionUi("Enemies", listOf("Pete")),
    ),
)
