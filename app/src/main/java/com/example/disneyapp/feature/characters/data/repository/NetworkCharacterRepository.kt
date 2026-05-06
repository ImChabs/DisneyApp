package com.example.disneyapp.feature.characters.data.repository

import com.example.disneyapp.core.domain.DataError
import com.example.disneyapp.core.domain.Result
import com.example.disneyapp.feature.characters.data.remote.CharacterRemoteDataSource
import com.example.disneyapp.feature.characters.domain.model.CharacterPage
import com.example.disneyapp.feature.characters.domain.model.DisneyCharacter
import com.example.disneyapp.feature.characters.domain.repository.CharacterRepository

class NetworkCharacterRepository(
    private val remoteDataSource: CharacterRemoteDataSource,
) : CharacterRepository {
    override suspend fun getCharacters(
        page: Int,
        pageSize: Int,
    ): Result<CharacterPage, DataError.Network> =
        remoteDataSource.getCharacters(page = page, pageSize = pageSize)

    override suspend fun getCharacter(id: Int): Result<DisneyCharacter, DataError.Network> =
        remoteDataSource.getCharacter(id)

    override suspend fun searchCharacters(
        name: String,
        page: Int,
        pageSize: Int,
    ): Result<CharacterPage, DataError.Network> =
        remoteDataSource.searchCharacters(name = name, page = page, pageSize = pageSize)
}
