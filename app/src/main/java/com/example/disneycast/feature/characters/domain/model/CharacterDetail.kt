package com.example.disneycast.feature.characters.domain.model

data class CharacterDetail(
    val character: DisneyCharacter,
    val isFromCache: Boolean = false,
)
