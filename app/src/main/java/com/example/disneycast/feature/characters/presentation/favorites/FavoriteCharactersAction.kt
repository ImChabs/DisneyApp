package com.example.disneycast.feature.characters.presentation.favorites

sealed interface FavoriteCharactersAction {
    data class OnSearchQueryChange(val query: String) : FavoriteCharactersAction
    data class OnFavoriteClick(val characterId: Int) : FavoriteCharactersAction
}
