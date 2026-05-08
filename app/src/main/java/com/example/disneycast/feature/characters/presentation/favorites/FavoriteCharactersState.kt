package com.example.disneycast.feature.characters.presentation.favorites

import androidx.compose.runtime.Stable
import com.example.disneycast.feature.characters.presentation.list.CharacterListItemUi

@Stable
data class FavoriteCharactersState(
    val favorites: List<CharacterListItemUi> = emptyList(),
    val searchQuery: String = "",
    val totalFavoritesCount: Int = favorites.size,
) {
    val isEmpty: Boolean
        get() = totalFavoritesCount == 0

    val isEmptySearchResult: Boolean
        get() = totalFavoritesCount > 0 && searchQuery.isNotBlank() && favorites.isEmpty()
}
