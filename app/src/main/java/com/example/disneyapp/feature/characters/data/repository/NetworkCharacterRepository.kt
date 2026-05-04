package com.example.disneyapp.feature.characters.data.repository

import com.example.disneyapp.core.domain.DataError
import com.example.disneyapp.core.domain.Result
import com.example.disneyapp.feature.characters.data.remote.CharacterRemoteDataSource
import com.example.disneyapp.feature.characters.domain.model.DisneyCharacter
import com.example.disneyapp.feature.characters.domain.repository.CharacterRepository

class NetworkCharacterRepository(
    private val remoteDataSource: CharacterRemoteDataSource,
) : CharacterRepository {
    override suspend fun getCharacters(): Result<List<DisneyCharacter>, DataError.Network> =
        remoteDataSource.getCharacters()

    override suspend fun getCharacter(id: Int): Result<DisneyCharacter, DataError.Network> =
        remoteDataSource.getCharacter(id)

    override suspend fun searchCharacters(
        name: String,
    ): Result<List<DisneyCharacter>, DataError.Network> =
        remoteDataSource.searchCharacters(name)
}
