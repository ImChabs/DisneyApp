package com.example.disneyapp.feature.characters.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CharacterListResponseDto(
    val data: List<CharacterDto>? = null,
)

@Serializable
data class CharacterDetailResponseDto(
    val data: CharacterDto? = null,
)
