package com.example.disneyapp.feature.films.presentation

sealed interface FilmsAction {
    data class OnSearchQueryChange(val query: String) : FilmsAction
    data class OnCharacterClick(val characterId: Int) : FilmsAction
    data object OnRetryClick : FilmsAction
}
