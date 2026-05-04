package com.example.disneyapp.feature.characters.data.remote

import com.example.disneyapp.core.data.network.safeGet
import com.example.disneyapp.core.domain.DataError
import com.example.disneyapp.core.domain.Result
import com.example.disneyapp.core.domain.map
import com.example.disneyapp.feature.characters.data.mapper.toDisneyCharacter
import com.example.disneyapp.feature.characters.data.remote.dto.CharacterDetailResponseDto
import com.example.disneyapp.feature.characters.data.remote.dto.CharacterListResponseDto
import com.example.disneyapp.feature.characters.domain.model.DisneyCharacter
import io.ktor.client.HttpClient

class KtorCharacterRemoteDataSource(
    private val httpClient: HttpClient,
) : CharacterRemoteDataSource {
    override suspend fun getCharacters(): Result<List<DisneyCharacter>, DataError.Network> =
        httpClient.safeGet<CharacterListResponseDto>(route = "/character")
            .map { response ->
                response.data.orEmpty().map { it.toDisneyCharacter() }
            }

    override suspend fun getCharacter(id: Int): Result<DisneyCharacter, DataError.Network> =
        when (
            val result = httpClient.safeGet<CharacterDetailResponseDto>(
                route = "/character/$id",
            )
        ) {
            is Result.Failure -> result
            is Result.Success -> {
                val character = result.data.data
                    ?: return Result.Failure(DataError.Network.SERIALIZATION)

                Result.Success(character.toDisneyCharacter())
            }
        }

    override suspend fun searchCharacters(
        name: String,
    ): Result<List<DisneyCharacter>, DataError.Network> =
        httpClient.safeGet<CharacterListResponseDto>(
            route = "/character",
            queryParameters = mapOf("name" to name),
        ).map { response ->
            response.data.orEmpty().map { it.toDisneyCharacter() }
        }
}
