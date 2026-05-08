package com.example.disneycast.feature.characters.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface CharacterCacheDao {
    @Query(
        """
        SELECT * FROM cached_characters
        WHERE page = :page AND pageSize = :pageSize
        ORDER BY pagePosition ASC
        """
    )
    suspend fun getCharacters(
        page: Int,
        pageSize: Int,
    ): List<CharacterCacheEntity>

    @Query("SELECT * FROM cached_characters WHERE id = :id")
    suspend fun getCharacter(id: Int): CharacterCacheEntity?

    @Upsert
    suspend fun upsertCharacters(characters: List<CharacterCacheEntity>)

    @Upsert
    suspend fun upsertCharacter(character: CharacterCacheEntity)

    @Query("DELETE FROM cached_characters WHERE page = :page AND pageSize = :pageSize")
    suspend fun deleteCharactersForPage(
        page: Int,
        pageSize: Int,
    )
}
