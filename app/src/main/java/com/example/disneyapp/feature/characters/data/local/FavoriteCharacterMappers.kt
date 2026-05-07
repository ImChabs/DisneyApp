package com.example.disneyapp.feature.characters.data.local

import com.example.disneyapp.feature.characters.domain.model.DisneyCharacter

fun DisneyCharacter.toFavoriteCharacterEntity(favoritedAtMillis: Long): FavoriteCharacterEntity =
    FavoriteCharacterEntity(
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
        favoritedAtMillis = favoritedAtMillis,
    )

fun FavoriteCharacterEntity.toDisneyCharacter(): DisneyCharacter =
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
