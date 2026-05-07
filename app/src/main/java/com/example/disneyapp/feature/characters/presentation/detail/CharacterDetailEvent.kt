package com.example.disneyapp.feature.characters.presentation.detail

import com.example.disneyapp.core.presentation.UiText

sealed interface CharacterDetailEvent {
    data class ShowSnackbar(val message: UiText) : CharacterDetailEvent
}
