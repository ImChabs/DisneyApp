package com.example.disneyapp.feature.characters.presentation.favorites

sealed interface FavoriteCharactersAction {
    data class OnFavoriteClick(val characterId: Int) : FavoriteCharactersAction
}
