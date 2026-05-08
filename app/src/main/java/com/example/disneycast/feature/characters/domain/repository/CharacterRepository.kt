package com.example.disneycast.feature.characters.domain.repository

import com.example.disneycast.core.domain.DataError
import com.example.disneycast.core.domain.Result
import com.example.disneycast.feature.characters.domain.model.CharacterDetail
import com.example.disneycast.feature.characters.domain.model.CharacterPage

interface CharacterRepository {
    suspend fun getCharacters(
        page: Int,
        pageSize: Int,
    ): Result<CharacterPage, DataError>

    suspend fun getCharacter(id: Int): Result<CharacterDetail, DataError>

    suspend fun searchCharacters(
        name: String,
        page: Int,
        pageSize: Int,
    ): Result<CharacterPage, DataError>
}
