package com.example.disneycast.feature.films.presentation

import com.example.disneycast.core.presentation.UiText
import com.example.disneycast.feature.characters.domain.model.DisneyCharacter

data class FilmsState(
    val searchQuery: String = "",
    val submittedQuery: String = "",
    val results: List<FilmCharacterUi> = emptyList(),
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val pageSize: Int = DEFAULT_PAGE_SIZE,
) {
    val isIdle: Boolean
        get() = searchQuery.isBlank() && submittedQuery.isBlank() && !isLoading && error == null

    val isEmptyResult: Boolean
        get() = submittedQuery.isNotBlank() && !isLoading && error == null && results.isEmpty()

    companion object {
        const val DEFAULT_PAGE_SIZE = 30
    }
}

data class FilmCharacterUi(
    val characterId: Int,
    val characterName: String,
    val imageUrl: String?,
    val films: List<String>,
    val shortFilms: List<String>,
) {
    val appearanceCount: Int
        get() = films.size + shortFilms.size
}

fun DisneyCharacter.toFilmCharacterUi(): FilmCharacterUi =
    FilmCharacterUi(
        characterId = id,
        characterName = name?.takeIf { it.isNotBlank() } ?: "Unknown character",
        imageUrl = imageUrl,
        films = films,
        shortFilms = shortFilms,
    )

val DisneyCharacter.hasFilmAppearances: Boolean
    get() = films.isNotEmpty() || shortFilms.isNotEmpty()
