package com.example.disneyapp.feature.characters.presentation.favorites

import com.example.disneyapp.core.presentation.UiText

sealed interface FavoriteCharactersEvent {
    data class ShowSnackbar(val message: UiText) : FavoriteCharactersEvent
}
