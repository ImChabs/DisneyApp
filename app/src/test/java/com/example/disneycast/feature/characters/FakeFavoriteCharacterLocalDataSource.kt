package com.example.disneycast.feature.characters

import com.example.disneycast.core.domain.DataError
import com.example.disneycast.core.domain.EmptyResult
import com.example.disneycast.core.domain.Result
import com.example.disneycast.feature.characters.domain.FavoriteCharacterLocalDataSource
import com.example.disneycast.feature.characters.domain.model.DisneyCharacter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeFavoriteCharacterLocalDataSource(
    initialFavorites: List<DisneyCharacter> = emptyList(),
) : FavoriteCharacterLocalDataSource {
    private val favorites = MutableStateFlow(initialFavorites.associateBy { it.id })

    var shouldReturnError = false
    val savedFavorites = mutableListOf<DisneyCharacter>()
    val removedFavoriteIds = mutableListOf<Int>()

    override fun observeFavorites(): Flow<List<DisneyCharacter>> =
        favorites.map { it.values.toList() }

    override fun observeFavoriteIds(): Flow<Set<Int>> =
        favorites.map { it.keys }

    override fun observeIsFavorite(characterId: Int): Flow<Boolean> =
        favorites.map { characterId in it.keys }

    override suspend fun isFavorite(characterId: Int): Result<Boolean, DataError.Local> =
        if (shouldReturnError) {
            Result.Failure(DataError.Local.UNKNOWN)
        } else {
            Result.Success(characterId in favorites.value.keys)
        }

    override suspend fun saveFavorite(character: DisneyCharacter): EmptyResult<DataError.Local> =
        if (shouldReturnError) {
            Result.Failure(DataError.Local.UNKNOWN)
        } else {
            savedFavorites.add(character)
            favorites.update { current -> current + (character.id to character) }
            Result.Success(Unit)
        }

    override suspend fun removeFavorite(characterId: Int): EmptyResult<DataError.Local> =
        if (shouldReturnError) {
            Result.Failure(DataError.Local.UNKNOWN)
        } else {
            removedFavoriteIds.add(characterId)
            favorites.update { current -> current - characterId }
            Result.Success(Unit)
        }
}
