package com.example.disneyapp.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.disneyapp.feature.characters.data.local.FavoriteCharacterDao
import com.example.disneyapp.feature.characters.data.local.FavoriteCharacterEntity

@Database(
    entities = [FavoriteCharacterEntity::class],
    version = 1,
)
@TypeConverters(StringListTypeConverters::class)
abstract class DisneyAppDatabase : RoomDatabase() {
    abstract fun favoriteCharacterDao(): FavoriteCharacterDao
}
