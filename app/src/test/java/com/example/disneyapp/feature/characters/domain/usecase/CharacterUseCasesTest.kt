package com.example.disneyapp.feature.characters.domain.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.example.disneyapp.core.domain.DataError
import com.example.disneyapp.core.domain.Result
import com.example.disneyapp.feature.characters.domain.model.DisneyCharacter
import com.example.disneyapp.feature.characters.domain.repository.CharacterRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class CharacterUseCasesTest {
    @Test
    fun `get characters returns repository result`() = runTest {
        val characters = listOf(mickey)
        val repository = FakeCharacterRepository(
            getCharactersResult = Result.Success(characters),
        )
        val useCase = GetCharactersUseCase(repository)

        val result = useCase()

        assertThat(result).isEqualTo(Result.Success(characters))
        assertThat(repository.getCharactersCalled).isTrue()
    }

    @Test
    fun `get character detail returns repository result`() = runTest {
        val repository = FakeCharacterRepository(
            getCharacterResult = Result.Success(mickey),
        )
        val useCase = GetCharacterDetailUseCase(repository)

        val result = useCase(id = 4703)

        assertThat(result).isEqualTo(Result.Success(mickey))
        assertThat(repository.requestedCharacterId).isEqualTo(4703)
    }

    @Test
    fun `search characters trims query before searching`() = runTest {
        val characters = listOf(mickey)
        val repository = FakeCharacterRepository(
            searchCharactersResult = Result.Success(characters),
        )
        val useCase = SearchCharactersUseCase(repository)

        val result = useCase("  Mickey Mouse  ")

        assertThat(result).isEqualTo(Result.Success(characters))
        assertThat(repository.requestedSearchQuery).isEqualTo("Mickey Mouse")
    }

    @Test
    fun `search characters returns empty success for blank query without searching`() = runTest {
        val repository = FakeCharacterRepository()
        val useCase = SearchCharactersUseCase(repository)

        val result = useCase("   ")

        assertThat(result).isEqualTo(Result.Success(emptyList()))
        assertThat(repository.searchCharactersCalled).isFalse()
    }

    @Test
    fun `search characters preserves repository failure`() = runTest {
        val failure = Result.Failure(DataError.Network.NO_INTERNET)
        val repository = FakeCharacterRepository(
            searchCharactersResult = failure,
        )
        val useCase = SearchCharactersUseCase(repository)

        val result = useCase("Mickey")

        assertThat(result).isEqualTo(failure)
    }
}

private class FakeCharacterRepository(
    private val getCharactersResult: Result<List<DisneyCharacter>, DataError.Network> =
        Result.Success(emptyList()),
    private val getCharacterResult: Result<DisneyCharacter, DataError.Network> =
        Result.Failure(DataError.Network.UNKNOWN),
    private val searchCharactersResult: Result<List<DisneyCharacter>, DataError.Network> =
        Result.Success(emptyList()),
) : CharacterRepository {
    var getCharactersCalled = false
        private set

    var requestedCharacterId: Int? = null
        private set

    var searchCharactersCalled = false
        private set

    var requestedSearchQuery: String? = null
        private set

    override suspend fun getCharacters(): Result<List<DisneyCharacter>, DataError.Network> {
        getCharactersCalled = true
        return getCharactersResult
    }

    override suspend fun getCharacter(id: Int): Result<DisneyCharacter, DataError.Network> {
        requestedCharacterId = id
        return getCharacterResult
    }

    override suspend fun searchCharacters(
        name: String,
    ): Result<List<DisneyCharacter>, DataError.Network> {
        searchCharactersCalled = true
        requestedSearchQuery = name
        return searchCharactersResult
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
