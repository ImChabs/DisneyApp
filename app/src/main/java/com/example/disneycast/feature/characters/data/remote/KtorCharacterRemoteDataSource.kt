package com.example.disneycast.feature.characters.data.remote

import com.example.disneycast.core.data.network.safeGet
import com.example.disneycast.core.domain.DataError
import com.example.disneycast.core.domain.Result
import com.example.disneycast.core.domain.map
import com.example.disneycast.feature.characters.data.mapper.toDisneyCharacter
import com.example.disneycast.feature.characters.data.remote.dto.CharacterDetailResponseDto
import com.example.disneycast.feature.characters.data.remote.dto.CharacterListResponseDto
import com.example.disneycast.feature.characters.domain.model.CharacterPage
import com.example.disneycast.feature.characters.domain.model.DisneyCharacter
import io.ktor.client.HttpClient

class KtorCharacterRemoteDataSource(
    private val httpClient: HttpClient,
) : CharacterRemoteDataSource {
    override suspend fun getCharacters(
        page: Int,
        pageSize: Int,
    ): Result<CharacterPage, DataError.Network> =
        httpClient.safeGet<CharacterListResponseDto>(
            route = "/character",
            queryParameters = mapOf(
                "page" to page,
                "pageSize" to pageSize,
            ),
        ).map { response ->
            response.toCharacterPage(page = page, pageSize = pageSize)
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
        page: Int,
        pageSize: Int,
    ): Result<CharacterPage, DataError.Network> =
        httpClient.safeGet<CharacterListResponseDto>(
            route = "/character",
            queryParameters = mapOf(
                "name" to name,
                "page" to page,
                "pageSize" to pageSize,
            ),
        ).map { response ->
            response.toCharacterPage(page = page, pageSize = pageSize)
        }
}

private fun CharacterListResponseDto.toCharacterPage(
    page: Int,
    pageSize: Int,
): CharacterPage {
    val totalPages = info?.totalPages
    val hasNextPage = !info?.nextPage.isNullOrBlank() || (totalPages != null && page < totalPages)

    return CharacterPage(
        characters = data.orEmpty().map { it.toDisneyCharacter() },
        currentPage = page,
        pageSize = pageSize,
        totalPages = totalPages,
        hasNextPage = hasNextPage,
    )
}
