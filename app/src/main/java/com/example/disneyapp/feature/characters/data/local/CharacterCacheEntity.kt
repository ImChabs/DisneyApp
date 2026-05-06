package com.example.disneyapp.feature.characters.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_characters")
data class CharacterCacheEntity(
    @PrimaryKey val id: Int,
    val name: String?,
    val alignment: String?,
    val imageUrl: String?,
    val sourceUrl: String?,
    val apiUrl: String?,
    val films: List<String>,
    val shortFilms: List<String>,
    val tvShows: List<String>,
    val videoGames: List<String>,
    val parkAttractions: List<String>,
    val allies: List<String>,
    val enemies: List<String>,
    val page: Int?,
    val pageSize: Int?,
    val pagePosition: Int?,
    val cachedAtMillis: Long,
)
