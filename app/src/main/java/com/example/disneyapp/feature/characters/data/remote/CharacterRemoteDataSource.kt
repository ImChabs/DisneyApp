package com.example.disneyapp.feature.characters.data.remote

import com.example.disneyapp.core.domain.DataError
import com.example.disneyapp.core.domain.Result
import com.example.disneyapp.feature.characters.domain.model.DisneyCharacter

interface CharacterRemoteDataSource {
    suspend fun getCharacters(): Result<List<DisneyCharacter>, DataError.Network>

    suspend fun getCharacter(id: Int): Result<DisneyCharacter, DataError.Network>

    suspend fun searchCharacters(name: String): Result<List<DisneyCharacter>, DataError.Network>
}
