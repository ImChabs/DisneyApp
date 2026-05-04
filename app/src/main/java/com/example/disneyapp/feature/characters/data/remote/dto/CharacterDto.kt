package com.example.disneyapp.feature.characters.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CharacterDto(
    @SerialName("_id")
    val id: Int? = null,
    val films: List<String>? = null,
    val shortFilms: List<String>? = null,
    val tvShows: List<String>? = null,
    val videoGames: List<String>? = null,
    val parkAttractions: List<String>? = null,
    val allies: List<String>? = null,
    val enemies: List<String>? = null,
    val sourceUrl: String? = null,
    val name: String? = null,
    val imageUrl: String? = null,
    val url: String? = null,
)
