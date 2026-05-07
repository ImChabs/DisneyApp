package com.example.disneyapp.feature.characters.data.local

import com.example.disneyapp.feature.characters.domain.model.DisneyCharacter

fun DisneyCharacter.toCharacterCacheEntity(
    cachedAtMillis: Long,
    page: Int?,
    pageSize: Int?,
    pagePosition: Int?,
): CharacterCacheEntity =
    CharacterCacheEntity(
        id = id,
        name = name,
        alignment = alignment,
        imageUrl = imageUrl,
        sourceUrl = sourceUrl,
        apiUrl = apiUrl,
        films = films,
        shortFilms = shortFilms,
        tvShows = tvShows,
        videoGames = videoGames,
        parkAttractions = parkAttractions,
        allies = allies,
        enemies = enemies,
        page = page,
        pageSize = pageSize,
        pagePosition = pagePosition,
        cachedAtMillis = cachedAtMillis,
    )

fun CharacterCacheEntity.toDisneyCharacter(): DisneyCharacter =
    DisneyCharacter(
        id = id,
        name = name,
        alignment = alignment,
        imageUrl = imageUrl,
        sourceUrl = sourceUrl,
        apiUrl = apiUrl,
        films = films,
        shortFilms = shortFilms,
        tvShows = tvShows,
        videoGames = videoGames,
        parkAttractions = parkAttractions,
        allies = allies,
        enemies = enemies,
    )
