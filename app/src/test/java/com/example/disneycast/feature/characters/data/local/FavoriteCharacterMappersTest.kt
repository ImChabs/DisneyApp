package com.example.disneycast.feature.characters.data.local

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.example.disneycast.feature.characters.domain.model.DisneyCharacter
import org.junit.jupiter.api.Test

class FavoriteCharacterMappersTest {
    @Test
    fun `toFavoriteCharacterEntity stores full character snapshot`() {
        val entity = mickey.toFavoriteCharacterEntity(favoritedAtMillis = 123L)

        assertThat(entity).isEqualTo(
            FavoriteCharacterEntity(
                id = 4703,
                name = "Mickey Mouse",
                alignment = "Good",
                imageUrl = "https://example.com/mickey.jpg",
                sourceUrl = "https://disney.fandom.com/wiki/Mickey_Mouse",
                apiUrl = "https://api.disneyapi.dev/characters/4703",
                films = listOf("Fantasia"),
                shortFilms = listOf("Steamboat Willie"),
                tvShows = listOf("Mickey Mouse Clubhouse"),
                videoGames = listOf("Kingdom Hearts"),
                parkAttractions = listOf("Mickey's PhilharMagic"),
                allies = listOf("Minnie Mouse"),
                enemies = listOf("Pete"),
                favoritedAtMillis = 123L,
            ),
        )
    }

    @Test
    fun `toDisneyCharacter restores domain character`() {
        val character = mickey.toFavoriteCharacterEntity(favoritedAtMillis = 123L).toDisneyCharacter()

        assertThat(character).isEqualTo(mickey)
    }
}

private val mickey = DisneyCharacter(
    id = 4703,
    name = "Mickey Mouse",
    alignment = "Good",
    imageUrl = "https://example.com/mickey.jpg",
    sourceUrl = "https://disney.fandom.com/wiki/Mickey_Mouse",
    apiUrl = "https://api.disneyapi.dev/characters/4703",
    films = listOf("Fantasia"),
    shortFilms = listOf("Steamboat Willie"),
    tvShows = listOf("Mickey Mouse Clubhouse"),
    videoGames = listOf("Kingdom Hearts"),
    parkAttractions = listOf("Mickey's PhilharMagic"),
    allies = listOf("Minnie Mouse"),
    enemies = listOf("Pete"),
)
