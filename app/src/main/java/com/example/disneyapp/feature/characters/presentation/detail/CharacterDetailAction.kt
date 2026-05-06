package com.example.disneyapp.feature.characters.presentation.detail

sealed interface CharacterDetailAction {
    data object OnRetryClick : CharacterDetailAction
    data object OnFavoriteClick : CharacterDetailAction
}
