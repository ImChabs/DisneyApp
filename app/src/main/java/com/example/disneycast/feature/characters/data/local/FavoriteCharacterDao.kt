package com.example.disneycast.feature.characters.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteCharacterDao {
    @Query("SELECT * FROM favorite_characters ORDER BY favoritedAtMillis DESC")
    fun observeFavorites(): Flow<List<FavoriteCharacterEntity>>

    @Query("SELECT id FROM favorite_characters")
    fun observeFavoriteIds(): Flow<List<Int>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_characters WHERE id = :characterId)")
    fun observeIsFavorite(characterId: Int): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_characters WHERE id = :characterId)")
    suspend fun isFavorite(characterId: Int): Boolean

    @Upsert
    suspend fun upsertFavorite(character: FavoriteCharacterEntity)

    @Query("DELETE FROM favorite_characters WHERE id = :characterId")
    suspend fun deleteFavorite(characterId: Int)
}
