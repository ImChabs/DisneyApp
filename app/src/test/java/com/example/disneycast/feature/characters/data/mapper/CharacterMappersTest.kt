package com.example.disneycast.feature.characters.data.mapper

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.example.disneycast.feature.characters.data.remote.dto.CharacterDto
import org.junit.jupiter.api.Test

class CharacterMappersTest {
    @Test
    fun `toDisneyCharacter maps nullable lists to empty lists`() {
        val character = CharacterDto().toDisneyCharacter()

        assertThat(character.id).isEqualTo(0)
        assertThat(character.name).isNull()
        assertThat(character.alignment).isNull()
        assertThat(character.imageUrl).isNull()
        assertThat(character.films).isEmpty()
        assertThat(character.shortFilms).isEmpty()
        assertThat(character.tvShows).isEmpty()
        assertThat(character.videoGames).isEmpty()
        assertThat(character.parkAttractions).isEmpty()
        assertThat(character.allies).isEmpty()
        assertThat(character.enemies).isEmpty()
    }

    @Test
    fun `toDisneyCharacter maps api fields to domain model`() {
        val character = CharacterDto(
            id = 4703,
            films = listOf("Fantasia"),
            shortFilms = listOf("Steamboat Willie"),
            tvShows = listOf("Mickey Mouse Clubhouse"),
            videoGames = listOf("Kingdom Hearts"),
            parkAttractions = listOf("Mickey's PhilharMagic"),
            allies = listOf("Minnie Mouse"),
            enemies = listOf("Pete"),
            sourceUrl = "https://disney.fandom.com/wiki/Mickey_Mouse",
            name = "Mickey Mouse",
            alignment = "Good",
            imageUrl = "https://example.com/mickey.jpg",
            url = "https://api.disneyapi.dev/characters/4703",
        ).toDisneyCharacter()

        assertThat(character.id).isEqualTo(4703)
        assertThat(character.name).isEqualTo("Mickey Mouse")
        assertThat(character.alignment).isEqualTo("Good")
        assertThat(character.imageUrl).isEqualTo("https://example.com/mickey.jpg")
        assertThat(character.sourceUrl).isEqualTo("https://disney.fandom.com/wiki/Mickey_Mouse")
        assertThat(character.apiUrl).isEqualTo("https://api.disneyapi.dev/characters/4703")
        assertThat(character.films).isEqualTo(listOf("Fantasia"))
        assertThat(character.shortFilms).isEqualTo(listOf("Steamboat Willie"))
        assertThat(character.tvShows).isEqualTo(listOf("Mickey Mouse Clubhouse"))
        assertThat(character.videoGames).isEqualTo(listOf("Kingdom Hearts"))
        assertThat(character.parkAttractions).isEqualTo(listOf("Mickey's PhilharMagic"))
        assertThat(character.allies).isEqualTo(listOf("Minnie Mouse"))
        assertThat(character.enemies).isEqualTo(listOf("Pete"))
    }
}
