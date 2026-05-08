package com.example.disneycast.feature.characters.data.local

import com.example.disneycast.core.domain.DataError
import com.example.disneycast.core.domain.EmptyResult
import com.example.disneycast.core.domain.Result
import com.example.disneycast.feature.characters.domain.FavoriteCharacterLocalDataSource
import com.example.disneycast.feature.characters.domain.model.DisneyCharacter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class RoomFavoriteCharacterLocalDataSource(
    private val dao: FavoriteCharacterDao,
) : FavoriteCharacterLocalDataSource {
    override fun observeFavorites(): Flow<List<DisneyCharacter>> =
        dao.observeFavorites()
            .map { favorites -> favorites.map { it.toDisneyCharacter() } }
            .catch { emit(emptyList()) }

    override fun observeFavoriteIds(): Flow<Set<Int>> =
        dao.observeFavoriteIds()
            .map { it.toSet() }
            .catch { emit(emptySet()) }

    override fun observeIsFavorite(characterId: Int): Flow<Boolean> =
        dao.observeIsFavorite(characterId)
            .catch { emit(false) }

    override suspend fun isFavorite(characterId: Int): Result<Boolean, DataError.Local> =
        try {
            Result.Success(dao.isFavorite(characterId))
        } catch (e: Exception) {
            Result.Failure(DataError.Local.UNKNOWN)
        }

    override suspend fun saveFavorite(character: DisneyCharacter): EmptyResult<DataError.Local> =
        try {
            dao.upsertFavorite(
                character.toFavoriteCharacterEntity(
                    favoritedAtMillis = System.currentTimeMillis(),
                ),
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(DataError.Local.UNKNOWN)
        }

    override suspend fun removeFavorite(characterId: Int): EmptyResult<DataError.Local> =
        try {
            dao.deleteFavorite(characterId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(DataError.Local.UNKNOWN)
        }
}
