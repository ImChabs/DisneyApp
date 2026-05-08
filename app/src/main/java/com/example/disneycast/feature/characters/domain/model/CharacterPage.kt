package com.example.disneycast.feature.characters.domain.model

data class CharacterPage(
    val characters: List<DisneyCharacter>,
    val currentPage: Int,
    val pageSize: Int,
    val totalPages: Int?,
    val hasNextPage: Boolean,
    val isFromCache: Boolean = false,
)
