package com.example.disneyapp.feature.characters.data.local

import com.example.disneyapp.core.domain.DataError
import com.example.disneyapp.core.domain.EmptyResult
import com.example.disneyapp.core.domain.Result
import com.example.disneyapp.feature.characters.domain.CharacterLocalDataSource
import com.example.disneyapp.feature.characters.domain.model.CharacterPage
import com.example.disneyapp.feature.characters.domain.model.DisneyCharacter

class RoomCharacterLocalDataSource(
    private val dao: CharacterCacheDao,
) : CharacterLocalDataSource {
    override suspend fun getCharacters(
        page: Int,
        pageSize: Int,
    ): Result<CharacterPage, DataError.Local> =
        try {
            val characters = dao.getCharacters(page = page, pageSize = pageSize)
            if (characters.isEmpty()) {
                Result.Failure(DataError.Local.NOT_FOUND)
            } else {
                Result.Success(
                    CharacterPage(
                        characters = characters.map { it.toDisneyCharacter() },
                        currentPage = page,
                        pageSize = pageSize,
                        totalPages = null,
                        hasNextPage = false,
                        isFromCache = true,
                    )
                )
            }
        } catch (e: Exception) {
            Result.Failure(DataError.Local.UNKNOWN)
        }

    override suspend fun getCharacter(id: Int): Result<DisneyCharacter, DataError.Local> =
        try {
            val character = dao.getCharacter(id)
                ?: return Result.Failure(DataError.Local.NOT_FOUND)

            Result.Success(character.toDisneyCharacter())
        } catch (e: Exception) {
            Result.Failure(DataError.Local.UNKNOWN)
        }

    override suspend fun upsertCharacters(
        page: CharacterPage,
    ): EmptyResult<DataError.Local> =
        try {
            val cachedAtMillis = System.currentTimeMillis()
            val entities = page.characters.mapIndexed { index, character ->
                character.toCharacterCacheEntity(
                    cachedAtMillis = cachedAtMillis,
                    page = page.currentPage,
                    pageSize = page.pageSize,
                    pagePosition = index,
                )
            }
            dao.deleteCharactersForPage(page = page.currentPage, pageSize = page.pageSize)
            dao.upsertCharacters(entities)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(DataError.Local.UNKNOWN)
        }

    override suspend fun upsertCharacter(
        character: DisneyCharacter,
    ): EmptyResult<DataError.Local> =
        try {
            val existing = dao.getCharacter(character.id)
            dao.upsertCharacter(
                character.toCharacterCacheEntity(
                    cachedAtMillis = System.currentTimeMillis(),
                    page = existing?.page,
                    pageSize = existing?.pageSize,
                    pagePosition = existing?.pagePosition,
                )
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(DataError.Local.UNKNOWN)
        }
}
