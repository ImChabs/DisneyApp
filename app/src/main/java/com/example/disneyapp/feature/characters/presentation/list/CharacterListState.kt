package com.example.disneyapp.feature.characters.presentation.list

import com.example.disneyapp.core.presentation.UiText
import com.example.disneyapp.feature.characters.domain.model.DisneyCharacter

data class CharacterListState(
    val characters: List<CharacterListItemUi> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: UiText? = null,
) {
    val isEmpty: Boolean
        get() = !isLoading && error == null && characters.isEmpty()
}

data class CharacterListItemUi(
    val id: Int,
    val name: String,
    val imageUrl: String?,
)

fun DisneyCharacter.toCharacterListItemUi(): CharacterListItemUi =
    CharacterListItemUi(
        id = id,
        name = name?.takeIf { it.isNotBlank() } ?: "Unknown character",
        imageUrl = imageUrl,
    )
