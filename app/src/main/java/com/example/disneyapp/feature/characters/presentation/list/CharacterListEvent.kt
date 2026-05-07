package com.example.disneyapp.feature.characters.presentation.list

import com.example.disneyapp.core.presentation.UiText

sealed interface CharacterListEvent {
    data class ShowSnackbar(val message: UiText) : CharacterListEvent
}
