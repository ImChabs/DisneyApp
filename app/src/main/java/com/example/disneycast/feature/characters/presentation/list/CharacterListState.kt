package com.example.disneycast.feature.characters.presentation.list

import com.example.disneycast.core.presentation.UiText
import com.example.disneycast.feature.characters.domain.model.DisneyCharacter

data class CharacterListState(
    val characters: List<CharacterListItemUi> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val currentPage: Int = 0,
    val pageSize: Int = DEFAULT_PAGE_SIZE,
    val canLoadMore: Boolean = false,
    val error: UiText? = null,
) {
    val isEmpty: Boolean
        get() = !isLoading && !isLoadingMore && error == null && characters.isEmpty()

    companion object {
        const val DEFAULT_PAGE_SIZE = 30
    }
}

data class CharacterListItemUi(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val metadataBadges: List<String> = emptyList(),
    val isFavorite: Boolean = false,
)

fun DisneyCharacter.toCharacterListItemUi(isFavorite: Boolean = false): CharacterListItemUi =
    CharacterListItemUi(
        id = id,
        name = name?.takeIf { it.isNotBlank() } ?: "Unknown character",
        imageUrl = imageUrl,
        metadataBadges = buildMetadataBadges(),
        isFavorite = isFavorite,
    )

private fun DisneyCharacter.buildMetadataBadges(): List<String> =
    buildList {
        addCountBadge(films.size, singular = "film", plural = "films")
        addCountBadge(shortFilms.size, singular = "short", plural = "shorts")
        addCountBadge(tvShows.size, singular = "show", plural = "shows")
        addCountBadge(videoGames.size, singular = "game", plural = "games")
        addCountBadge(parkAttractions.size, singular = "park", plural = "parks")
    }.take(3)

private fun MutableList<String>.addCountBadge(
    count: Int,
    singular: String,
    plural: String,
) {
    if (count > 0) {
        add("$count ${if (count == 1) singular else plural}")
    }
}
