package com.example.disneyapp.core.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.disneyapp.feature.characters.data.local.CharacterCacheDao
import com.example.disneyapp.feature.characters.data.local.CharacterCacheEntity
import com.example.disneyapp.feature.characters.data.local.FavoriteCharacterDao
import com.example.disneyapp.feature.characters.data.local.FavoriteCharacterEntity

@Database(
    entities = [
        FavoriteCharacterEntity::class,
        CharacterCacheEntity::class,
    ],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
    ],
)
@TypeConverters(StringListTypeConverters::class)
abstract class DisneyAppDatabase : RoomDatabase() {
    abstract fun favoriteCharacterDao(): FavoriteCharacterDao

    abstract fun characterCacheDao(): CharacterCacheDao
}
