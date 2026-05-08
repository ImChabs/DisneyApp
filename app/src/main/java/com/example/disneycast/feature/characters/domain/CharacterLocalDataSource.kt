package com.example.disneycast.feature.characters.domain

import com.example.disneycast.core.domain.DataError
import com.example.disneycast.core.domain.EmptyResult
import com.example.disneycast.core.domain.Result
import com.example.disneycast.feature.characters.domain.model.CharacterPage
import com.example.disneycast.feature.characters.domain.model.DisneyCharacter

interface CharacterLocalDataSource {
    suspend fun getCharacters(
        page: Int,
        pageSize: Int,
    ): Result<CharacterPage, DataError.Local>

    suspend fun getCharacter(id: Int): Result<DisneyCharacter, DataError.Local>

    suspend fun upsertCharacters(
        page: CharacterPage,
    ): EmptyResult<DataError.Local>

    suspend fun upsertCharacter(
        character: DisneyCharacter,
    ): EmptyResult<DataError.Local>
}
