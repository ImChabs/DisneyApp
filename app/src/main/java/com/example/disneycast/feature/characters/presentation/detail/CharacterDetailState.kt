package com.example.disneycast.feature.characters.presentation.detail

import androidx.compose.runtime.Stable
import com.example.disneycast.core.presentation.UiText
import com.example.disneycast.feature.characters.domain.model.DisneyCharacter

@Stable
data class CharacterDetailState(
    val character: CharacterDetailUi? = null,
    val isLoading: Boolean = false,
    val error: UiText? = null,
)

@Stable
data class CharacterDetailUi(
    val id: Int,
    val name: String,
    val alignment: String?,
    val imageUrl: String?,
    val metadataBadges: List<String>,
    val sections: List<CharacterDetailSectionUi>,
    val isFavorite: Boolean = false,
) {
    val hasProfileData: Boolean
        get() = sections.isNotEmpty()
}

@Stable
data class CharacterDetailSectionUi(
    val title: String,
    val items: List<String>,
)

fun DisneyCharacter.toCharacterDetailUi(isFavorite: Boolean = false): CharacterDetailUi =
    CharacterDetailUi(
        id = id,
        name = name?.takeIf { it.isNotBlank() } ?: "Unknown character",
        alignment = alignment?.takeIf { it.isNotBlank() },
        imageUrl = imageUrl?.takeIf { it.isNotBlank() },
        metadataBadges = buildDetailBadges(),
        sections = buildDetailSections(),
        isFavorite = isFavorite,
    )

private fun DisneyCharacter.buildDetailSections(): List<CharacterDetailSectionUi> =
    buildList {
        addSection("Films", films)
        addSection("Short films", shortFilms)
        addSection("TV shows", tvShows)
        addSection("Video games", videoGames)
        addSection("Park attractions", parkAttractions)
        addSection("Allies", allies)
        addSection("Enemies", enemies)
    }

private fun DisneyCharacter.buildDetailBadges(): List<String> =
    buildList {
        addCountBadge(films.size, singular = "film", plural = "films")
        addCountBadge(shortFilms.size, singular = "short", plural = "shorts")
        addCountBadge(tvShows.size, singular = "show", plural = "shows")
        addCountBadge(videoGames.size, singular = "game", plural = "games")
        addCountBadge(parkAttractions.size, singular = "park", plural = "parks")
    }.take(4)

private fun MutableList<CharacterDetailSectionUi>.addSection(
    title: String,
    items: List<String>,
) {
    val safeItems = items.mapNotNull { it.takeIf(String::isNotBlank) }
    if (safeItems.isNotEmpty()) {
        add(CharacterDetailSectionUi(title = title, items = safeItems))
    }
}

private fun MutableList<String>.addCountBadge(
    count: Int,
    singular: String,
    plural: String,
) {
    if (count > 0) {
        add("$count ${if (count == 1) singular else plural}")
    }
}
