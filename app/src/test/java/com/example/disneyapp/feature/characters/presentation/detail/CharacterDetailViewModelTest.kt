package com.example.disneyapp.feature.characters.presentation.detail

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.example.disneyapp.core.domain.DataError
import com.example.disneyapp.core.domain.Result
import com.example.disneyapp.core.presentation.toUiText
import com.example.disneyapp.feature.characters.domain.model.CharacterPage
import com.example.disneyapp.feature.characters.domain.model.DisneyCharacter
import com.example.disneyapp.feature.characters.domain.repository.CharacterRepository
import com.example.disneyapp.feature.characters.domain.usecase.GetCharacterDetailUseCase
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
        val pendingResult = CompletableDeferred<Result<DisneyCharacter, DataError.Network>>()
        val repository = FakeCharacterRepository(
            getCharacterRequest = { pendingResult.await() },
        )
        val viewModel = createViewModel(repository)

        runCurrent()
        assertThat(viewModel.state.value.isLoading).isTrue()

        pendingResult.complete(Result.Success(mickey))
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

        repository.getCharacterResult = Result.Success(mickey)
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
            getCharacterResult = Result.Success(emptyCharacter),
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

    private fun createViewModel(
        repository: CharacterRepository,
    ): CharacterDetailViewModel =
        CharacterDetailViewModel(
            characterId = 4703,
            getCharacterDetailUseCase = GetCharacterDetailUseCase(repository),
        )
}

private class FakeCharacterRepository(
    var getCharacterResult: Result<DisneyCharacter, DataError.Network> =
        Result.Success(mickey),
    var getCharacterRequest: (suspend (Int) -> Result<DisneyCharacter, DataError.Network>)? = null,
) : CharacterRepository {
    val requestedCharacterIds = mutableListOf<Int>()

    override suspend fun getCharacters(
        page: Int,
        pageSize: Int,
    ): Result<CharacterPage, DataError.Network> =
        Result.Failure(DataError.Network.UNKNOWN)

    override suspend fun getCharacter(id: Int): Result<DisneyCharacter, DataError.Network> {
        requestedCharacterIds.add(id)
        return getCharacterRequest?.invoke(id) ?: getCharacterResult
    }

    override suspend fun searchCharacters(
        name: String,
        page: Int,
        pageSize: Int,
    ): Result<CharacterPage, DataError.Network> =
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
