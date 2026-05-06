package com.example.disneyapp.feature.characters.presentation.favorites

import androidx.compose.runtime.Stable
import com.example.disneyapp.feature.characters.presentation.list.CharacterListItemUi

@Stable
data class FavoriteCharactersState(
    val favorites: List<CharacterListItemUi> = emptyList(),
) {
    val isEmpty: Boolean
        get() = favorites.isEmpty()
}
