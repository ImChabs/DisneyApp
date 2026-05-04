package com.example.disneyapp.feature.characters.domain.repository

import com.example.disneyapp.core.domain.DataError
import com.example.disneyapp.core.domain.Result
import com.example.disneyapp.feature.characters.domain.model.DisneyCharacter

interface CharacterRepository {
    suspend fun getCharacters(): Result<List<DisneyCharacter>, DataError.Network>

    suspend fun getCharacter(id: Int): Result<DisneyCharacter, DataError.Network>

    suspend fun searchCharacters(name: String): Result<List<DisneyCharacter>, DataError.Network>
}
