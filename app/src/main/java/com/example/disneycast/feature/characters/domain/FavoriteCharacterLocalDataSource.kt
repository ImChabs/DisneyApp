package com.example.disneycast.feature.characters.domain

import com.example.disneycast.core.domain.DataError
import com.example.disneycast.core.domain.EmptyResult
import com.example.disneycast.core.domain.Result
import com.example.disneycast.feature.characters.domain.model.DisneyCharacter
import kotlinx.coroutines.flow.Flow

interface FavoriteCharacterLocalDataSource {
    fun observeFavorites(): Flow<List<DisneyCharacter>>
    fun observeFavoriteIds(): Flow<Set<Int>>
    fun observeIsFavorite(characterId: Int): Flow<Boolean>
    suspend fun isFavorite(characterId: Int): Result<Boolean, DataError.Local>
    suspend fun saveFavorite(character: DisneyCharacter): EmptyResult<DataError.Local>
    suspend fun removeFavorite(characterId: Int): EmptyResult<DataError.Local>
}
