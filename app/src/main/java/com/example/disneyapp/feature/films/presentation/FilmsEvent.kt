package com.example.disneyapp.feature.films.presentation

sealed interface FilmsEvent {
    data class NavigateToCharacterDetail(val characterId: Int) : FilmsEvent
}
