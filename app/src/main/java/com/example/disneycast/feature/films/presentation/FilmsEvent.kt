package com.example.disneycast.feature.films.presentation

sealed interface FilmsEvent {
    data class NavigateToCharacterDetail(val characterId: Int) : FilmsEvent
}
