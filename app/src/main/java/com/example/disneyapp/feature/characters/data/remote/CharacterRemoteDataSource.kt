package com.example.disneyapp.feature.characters.data.remote

import com.example.disneyapp.core.domain.DataError
import com.example.disneyapp.core.domain.Result
import com.example.disneyapp.feature.characters.domain.model.CharacterPage
import com.example.disneyapp.feature.characters.domain.model.DisneyCharacter

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
