package com.example.disneycast.feature.characters.domain.model

data class DisneyCharacter(
    val id: Int,
    val name: String?,
    val alignment: String?,
    val imageUrl: String?,
    val sourceUrl: String?,
    val apiUrl: String?,
    val films: List<String>,
    val shortFilms: List<String>,
    val tvShows: List<String>,
    val videoGames: List<String>,
    val parkAttractions: List<String>,
    val allies: List<String>,
    val enemies: List<String>,
)

val DisneyCharacter.hasVisibleContent: Boolean
    get() = films.isNotEmpty() ||
        shortFilms.isNotEmpty() ||
        tvShows.isNotEmpty() ||
        videoGames.isNotEmpty() ||
        parkAttractions.isNotEmpty()
