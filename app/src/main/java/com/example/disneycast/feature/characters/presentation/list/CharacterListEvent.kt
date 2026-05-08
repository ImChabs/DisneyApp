package com.example.disneycast.feature.characters.presentation.list

import com.example.disneycast.core.presentation.UiText

sealed interface CharacterListEvent {
    data class ShowSnackbar(val message: UiText) : CharacterListEvent
}
