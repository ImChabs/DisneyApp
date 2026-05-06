package com.example.disneyapp.feature.characters.domain.usecase

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import com.example.disneyapp.core.domain.DataError
import com.example.disneyapp.core.domain.Result
import com.example.disneyapp.feature.characters.FakeFavoriteCharacterLocalDataSource
import com.example.disneyapp.feature.characters.domain.model.DisneyCharacter
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class FavoriteCharacterUseCasesTest {
    @Test
    fun `save favorite delegates to local data source`() = runTest {
        val localDataSource = FakeFavoriteCharacterLocalDataSource()
        val useCase = SaveFavoriteCharacterUseCase(localDataSource)

        val result = useCase(mickey)

        assertThat(result).isEqualTo(Result.Success(Unit))
        assertThat(localDataSource.savedFavorites).containsExactly(mickey)
    }

    @Test
    fun `remove favorite delegates to local data source`() = runTest {
        val localDataSource = FakeFavoriteCharacterLocalDataSource(initialFavorites = listOf(mickey))
        val useCase = RemoveFavoriteCharacterUseCase(localDataSource)

        val result = useCase(4703)

        assertThat(result).isEqualTo(Result.Success(Unit))
        assertThat(localDataSource.removedFavoriteIds).containsExactly(4703)
    }

    @Test
    fun `toggle saves when character is not favorite`() = runTest {
        val localDataSource = FakeFavoriteCharacterLocalDataSource()
        val useCase = ToggleFavoriteCharacterUseCase(localDataSource)

        val result = useCase(mickey)

        assertThat(result).isEqualTo(Result.Success(Unit))
        assertThat(localDataSource.savedFavorites).containsExactly(mickey)
    }

    @Test
    fun `toggle removes when character is favorite`() = runTest {
        val localDataSource = FakeFavoriteCharacterLocalDataSource(initialFavorites = listOf(mickey))
        val useCase = ToggleFavoriteCharacterUseCase(localDataSource)

        val result = useCase(mickey)

        assertThat(result).isEqualTo(Result.Success(Unit))
        assertThat(localDataSource.removedFavoriteIds).containsExactly(4703)
    }

    @Test
    fun `toggle returns local error when favorite lookup fails`() = runTest {
        val localDataSource = FakeFavoriteCharacterLocalDataSource().apply {
            shouldReturnError = true
        }
        val useCase = ToggleFavoriteCharacterUseCase(localDataSource)

        val result = useCase(mickey)

        assertThat(result).isEqualTo(Result.Failure(DataError.Local.UNKNOWN))
    }
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
