package com.example.disneyapp.feature.characters.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CharacterListResponseDto(
    val info: CharacterResponseInfoDto? = null,
    val data: List<CharacterDto>? = null,
)

@Serializable
data class CharacterDetailResponseDto(
    val info: CharacterResponseInfoDto? = null,
    val data: CharacterDto? = null,
)

@Serializable
data class CharacterResponseInfoDto(
    val count: Int? = null,
    val totalPages: Int? = null,
    val previousPage: String? = null,
    val nextPage: String? = null,
)
