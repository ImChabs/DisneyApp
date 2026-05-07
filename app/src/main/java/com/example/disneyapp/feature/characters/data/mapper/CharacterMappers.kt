package com.example.disneyapp.feature.characters.data.mapper

import com.example.disneyapp.feature.characters.data.remote.dto.CharacterDto
import com.example.disneyapp.feature.characters.domain.model.DisneyCharacter

fun CharacterDto.toDisneyCharacter(): DisneyCharacter =
    DisneyCharacter(
        id = id ?: 0,
        name = name,
        alignment = alignment,
        imageUrl = imageUrl,
        sourceUrl = sourceUrl,
        apiUrl = url,
        films = films.orEmpty(),
        shortFilms = shortFilms.orEmpty(),
        tvShows = tvShows.orEmpty(),
        videoGames = videoGames.orEmpty(),
        parkAttractions = parkAttractions.orEmpty(),
        allies = allies.orEmpty(),
        enemies = enemies.orEmpty(),
    )
