package com.example.disneycast.feature.characters.data.repository

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.example.disneycast.core.domain.DataError
import com.example.disneycast.core.domain.EmptyResult
import com.example.disneycast.core.domain.Result
import com.example.disneycast.feature.characters.data.remote.CharacterRemoteDataSource
import com.example.disneycast.feature.characters.domain.CharacterLocalDataSource
import com.example.disneycast.feature.characters.domain.model.CharacterDetail
import com.example.disneycast.feature.characters.domain.model.CharacterPage
import com.example.disneycast.feature.characters.domain.model.DisneyCharacter
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class OfflineFirstCharacterRepositoryTest {
    @Test
    fun `get characters returns remote data and saves it locally`() = runTest {
        val page = characterPage(listOf(mickey))
        val remote = FakeCharacterRemoteDataSource(getCharactersResult = Result.Success(page))
        val local = FakeCharacterLocalDataSource()
        val repository = OfflineFirstCharacterRepository(remote, local)

        val result = repository.getCharacters(page = 1, pageSize = 30)

        assertThat(result).isEqualTo(Result.Success(page))
        assertThat(local.savedPages).containsExactly(page)
    }

    @Test
    fun `get characters returns cached page when remote fails`() = runTest {
        val cachedPage = characterPage(listOf(mickey), isFromCache = true)
        val repository = OfflineFirstCharacterRepository(
            remoteDataSource = FakeCharacterRemoteDataSource(
                getCharactersResult = Result.Failure(DataError.Network.NO_INTERNET),
            ),
            localDataSource = FakeCharacterLocalDataSource(
                getCharactersResult = Result.Success(cachedPage),
            ),
        )

        val result = repository.getCharacters(page = 1, pageSize = 30)

        assertThat(result).isEqualTo(Result.Success(cachedPage))
        assertThat((result as Result.Success).data.isFromCache).isTrue()
    }

    @Test
    fun `get characters returns remote error when remote fails and no cache exists`() = runTest {
        val repository = OfflineFirstCharacterRepository(
            remoteDataSource = FakeCharacterRemoteDataSource(
                getCharactersResult = Result.Failure(DataError.Network.NO_INTERNET),
            ),
            localDataSource = FakeCharacterLocalDataSource(
                getCharactersResult = Result.Failure(DataError.Local.NOT_FOUND),
            ),
        )

        val result = repository.getCharacters(page = 1, pageSize = 30)

        assertThat(result).isEqualTo(Result.Failure(DataError.Network.NO_INTERNET))
    }

    @Test
    fun `get character returns remote detail and saves it locally`() = runTest {
        val remote = FakeCharacterRemoteDataSource(getCharacterResult = Result.Success(mickey))
        val local = FakeCharacterLocalDataSource()
        val repository = OfflineFirstCharacterRepository(remote, local)

        val result = repository.getCharacter(4703)

        assertThat(result).isEqualTo(Result.Success(CharacterDetail(mickey)))
        assertThat(local.savedCharacters).containsExactly(mickey)
    }

    @Test
    fun `get character returns cached detail when remote fails`() = runTest {
        val repository = OfflineFirstCharacterRepository(
            remoteDataSource = FakeCharacterRemoteDataSource(
                getCharacterResult = Result.Failure(DataError.Network.NO_INTERNET),
            ),
            localDataSource = FakeCharacterLocalDataSource(
                getCharacterResult = Result.Success(mickey),
            ),
        )

        val result = repository.getCharacter(4703)

        assertThat(result).isEqualTo(Result.Success(CharacterDetail(mickey, isFromCache = true)))
        assertThat((result as Result.Success).data.isFromCache).isTrue()
    }

    @Test
    fun `search remains remote only`() = runTest {
        val page = characterPage(listOf(minnie))
        val local = FakeCharacterLocalDataSource()
        val repository = OfflineFirstCharacterRepository(
            remoteDataSource = FakeCharacterRemoteDataSource(
                searchCharactersResult = Result.Success(page),
            ),
            localDataSource = local,
        )

        val result = repository.searchCharacters(name = "Minnie", page = 1, pageSize = 30)

        assertThat(result).isEqualTo(Result.Success(page))
        assertThat(local.getCharactersCalled).isFalse()
    }
}

private class FakeCharacterRemoteDataSource(
    private val getCharactersResult: Result<CharacterPage, DataError.Network> =
        Result.Success(characterPage(emptyList())),
    private val getCharacterResult: Result<DisneyCharacter, DataError.Network> =
        Result.Success(mickey),
    private val searchCharactersResult: Result<CharacterPage, DataError.Network> =
        Result.Success(characterPage(emptyList())),
) : CharacterRemoteDataSource {
    override suspend fun getCharacters(
        page: Int,
        pageSize: Int,
    ): Result<CharacterPage, DataError.Network> =
        getCharactersResult

    override suspend fun getCharacter(id: Int): Result<DisneyCharacter, DataError.Network> =
        getCharacterResult

    override suspend fun searchCharacters(
        name: String,
        page: Int,
        pageSize: Int,
    ): Result<CharacterPage, DataError.Network> =
        searchCharactersResult
}

private class FakeCharacterLocalDataSource(
    private val getCharactersResult: Result<CharacterPage, DataError.Local> =
        Result.Failure(DataError.Local.NOT_FOUND),
    private val getCharacterResult: Result<DisneyCharacter, DataError.Local> =
        Result.Failure(DataError.Local.NOT_FOUND),
) : CharacterLocalDataSource {
    val savedPages = mutableListOf<CharacterPage>()
    val savedCharacters = mutableListOf<DisneyCharacter>()
    var getCharactersCalled = false
        private set

    override suspend fun getCharacters(
        page: Int,
        pageSize: Int,
    ): Result<CharacterPage, DataError.Local> {
        getCharactersCalled = true
        return getCharactersResult
    }

    override suspend fun getCharacter(id: Int): Result<DisneyCharacter, DataError.Local> =
        getCharacterResult

    override suspend fun upsertCharacters(page: CharacterPage): EmptyResult<DataError.Local> {
        savedPages.add(page)
        return Result.Success(Unit)
    }

    override suspend fun upsertCharacter(character: DisneyCharacter): EmptyResult<DataError.Local> {
        savedCharacters.add(character)
        return Result.Success(Unit)
    }
}

private fun characterPage(
    characters: List<DisneyCharacter>,
    isFromCache: Boolean = false,
): CharacterPage =
    CharacterPage(
        characters = characters,
        currentPage = 1,
        pageSize = 30,
        totalPages = 1,
        hasNextPage = false,
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

private val minnie = mickey.copy(
    id = 1949,
    name = "Minnie Mouse",
    imageUrl = null,
)
