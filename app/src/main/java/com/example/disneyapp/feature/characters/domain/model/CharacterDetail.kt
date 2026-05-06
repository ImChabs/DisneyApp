package com.example.disneyapp.feature.characters.domain.model

data class CharacterDetail(
    val character: DisneyCharacter,
    val isFromCache: Boolean = false,
)
