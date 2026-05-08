package com.example.disneycast.feature.characters.presentation.detail

import com.example.disneycast.core.presentation.UiText

sealed interface CharacterDetailEvent {
    data class ShowSnackbar(val message: UiText) : CharacterDetailEvent
}
