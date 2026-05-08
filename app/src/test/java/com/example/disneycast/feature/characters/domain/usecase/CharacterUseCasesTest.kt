package com.example.disneycast.feature.characters.domain.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.example.disneycast.core.domain.DataError
import com.example.disneycast.core.domain.Result
import com.example.disneycast.feature.characters.domain.model.CharacterDetail
import com.example.disneycast.feature.characters.domain.model.CharacterPage
import com.example.disneycast.feature.characters.domain.model.DisneyCharacter
import com.example.disneycast.feature.characters.domain.repository.CharacterRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class CharacterUseCasesTest {
    @Test
    fun `get characters returns filtered repository result`() = runTest {
        val characters = listOf(mickey, arabella, alliesOnlyCharacter)
        val repository = FakeCharacterRepository(
            getCharactersResult = Result.Success(
                characterPage(
                    characters = characters,
                    currentPage = 2,
                    pageSize = 25,
                    hasNextPage = true,
                    totalPages = 5,
                    isFromCache = true,
                )
            ),
        )
        val useCase = GetCharactersUseCase(repository)

        val result = useCase(page = 2, pageSize = 25)

        assertThat(result).isEqualTo(
            Result.Success(
                characterPage(
                    characters = listOf(mickey),
                    currentPage = 2,
                    pageSize = 25,
                    hasNextPage = true,
                    totalPages = 5,
                    isFromCache = true,
                )
            )
        )
        assertThat(repository.getCharactersCalled).isTrue()
        assertThat(repository.requestedPage).isEqualTo(2)
        assertThat(repository.requestedPageSize).isEqualTo(25)
    }

    @Test
    fun `get characters keeps characters with any visible content type`() = runTest {
        val visibleCharacters = listOf(
            arabella.copy(id = 1, films = listOf("Fantasia")),
            arabella.copy(id = 2, shortFilms = listOf("Steamboat Willie")),
            arabella.copy(id = 3, tvShows = listOf("Mickey Mouse Clubhouse")),
            arabella.copy(id = 4, videoGames = listOf("Kingdom Hearts")),
            arabella.copy(id = 5, parkAttractions = listOf("Mickey's PhilharMagic")),
        )
        val repository = FakeCharacterRepository(
            getCharactersResult = Result.Success(characterPage(visibleCharacters)),
        )
        val useCase = GetCharactersUseCase(repository)

        val result = useCase(page = 1, pageSize = 30)

        assertThat(result).isEqualTo(Result.Success(characterPage(visibleCharacters)))
    }

    @Test
    fun `get character detail returns repository result`() = runTest {
        val detail = CharacterDetail(mickey)
        val repository = FakeCharacterRepository(
            getCharacterResult = Result.Success(detail),
        )
        val useCase = GetCharacterDetailUseCase(repository)

        val result = useCase(id = 4703)

        assertThat(result).isEqualTo(Result.Success(detail))
        assertThat(repository.requestedCharacterId).isEqualTo(4703)
    }

    @Test
    fun `search characters trims query before searching`() = runTest {
        val characters = listOf(mickey, arabella)
        val repository = FakeCharacterRepository(
            searchCharactersResult = Result.Success(characterPage(characters)),
        )
        val useCase = SearchCharactersUseCase(repository)

        val result = useCase("  Mickey Mouse  ", page = 3, pageSize = 10)

        assertThat(result).isEqualTo(Result.Success(characterPage(listOf(mickey))))
        assertThat(repository.requestedSearchQuery).isEqualTo("Mickey Mouse")
        assertThat(repository.requestedPage).isEqualTo(3)
        assertThat(repository.requestedPageSize).isEqualTo(10)
    }

    @Test
    fun `search characters returns empty success for blank query without searching`() = runTest {
        val repository = FakeCharacterRepository()
        val useCase = SearchCharactersUseCase(repository)

        val result = useCase("   ", page = 1, pageSize = 30)

        assertThat(result).isEqualTo(
            Result.Success(
                CharacterPage(
                    characters = emptyList(),
                    currentPage = 1,
                    pageSize = 30,
                    totalPages = 0,
                    hasNextPage = false,
                )
            )
        )
        assertThat(repository.searchCharactersCalled).isFalse()
    }

    @Test
    fun `search characters preserves repository failure`() = runTest {
        val failure = Result.Failure(DataError.Network.NO_INTERNET)
        val repository = FakeCharacterRepository(
            searchCharactersResult = failure,
        )
        val useCase = SearchCharactersUseCase(repository)

        val result = useCase("Mickey", page = 1, pageSize = 30)

        assertThat(result).isEqualTo(failure)
    }
}

private class FakeCharacterRepository(
    private val getCharactersResult: Result<CharacterPage, DataError.Network> =
        Result.Success(characterPage(emptyList())),
    private val getCharacterResult: Result<CharacterDetail, DataError> =
        Result.Failure(DataError.Network.UNKNOWN),
    private val searchCharactersResult: Result<CharacterPage, DataError.Network> =
        Result.Success(characterPage(emptyList())),
) : CharacterRepository {
    var getCharactersCalled = false
        private set

    var requestedCharacterId: Int? = null
        private set

    var searchCharactersCalled = false
        private set

    var requestedSearchQuery: String? = null
        private set

    var requestedPage: Int? = null
        private set

    var requestedPageSize: Int? = null
        private set

    override suspend fun getCharacters(
        page: Int,
        pageSize: Int,
    ): Result<CharacterPage, DataError.Network> {
        getCharactersCalled = true
        requestedPage = page
        requestedPageSize = pageSize
        return getCharactersResult
    }

    override suspend fun getCharacter(id: Int): Result<CharacterDetail, DataError> {
        requestedCharacterId = id
        return getCharacterResult
    }

    override suspend fun searchCharacters(
        name: String,
        page: Int,
        pageSize: Int,
    ): Result<CharacterPage, DataError.Network> {
        searchCharactersCalled = true
        requestedSearchQuery = name
        requestedPage = page
        requestedPageSize = pageSize
        return searchCharactersResult
    }
}

private fun characterPage(
    characters: List<DisneyCharacter>,
    currentPage: Int = 1,
    pageSize: Int = 30,
    hasNextPage: Boolean = false,
    totalPages: Int? = currentPage,
    isFromCache: Boolean = false,
): CharacterPage =
    CharacterPage(
        characters = characters,
        currentPage = currentPage,
        pageSize = pageSize,
        totalPages = totalPages,
        hasNextPage = hasNextPage,
        isFromCache = isFromCache,
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

private val arabella = DisneyCharacter(
    id = 293,
    name = "Arabella",
    alignment = null,
    imageUrl = null,
    sourceUrl = null,
    apiUrl = "https://api.disneyapi.dev/characters/293",
    films = emptyList(),
    shortFilms = emptyList(),
    tvShows = emptyList(),
    videoGames = emptyList(),
    parkAttractions = emptyList(),
    allies = emptyList(),
    enemies = emptyList(),
)

private val alliesOnlyCharacter = arabella.copy(
    id = 294,
    name = "Allies Only",
    allies = listOf("Mickey Mouse"),
    enemies = listOf("Pete"),
)
