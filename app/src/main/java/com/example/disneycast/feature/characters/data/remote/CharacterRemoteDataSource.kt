package com.example.disneycast.feature.characters.data.remote

import com.example.disneycast.core.domain.DataError
import com.example.disneycast.core.domain.Result
import com.example.disneycast.feature.characters.domain.model.CharacterPage
import com.example.disneycast.feature.characters.domain.model.DisneyCharacter

interface CharacterRemoteDataSource {
    suspend fun getCharacters(
        page: Int,
        pageSize: Int,
    ): Result<CharacterPage, DataError.Network>

    suspend fun getCharacter(id: Int): Result<DisneyCharacter, DataError.Network>

    suspend fun searchCharacters(
        name: String,
        page: Int,
        pageSize: Int,
    ): Result<CharacterPage, DataError.Network>
}
