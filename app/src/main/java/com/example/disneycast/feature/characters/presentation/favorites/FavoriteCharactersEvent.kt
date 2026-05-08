package com.example.disneycast.feature.characters.presentation.favorites

import com.example.disneycast.core.presentation.UiText

sealed interface FavoriteCharactersEvent {
    data class ShowSnackbar(val message: UiText) : FavoriteCharactersEvent
}
