package com.example.disneycast.feature.characters.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.disneycast.core.data.local.DisneyCastDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteCharacterDaoTest {
    private lateinit var database: DisneyCastDatabase
    private lateinit var dao: FavoriteCharacterDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, DisneyCastDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.favoriteCharacterDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun favorites_areObservedMostRecentFirst() = runBlocking {
        dao.upsertFavorite(mickey.copy(favoritedAtMillis = 100L))
        dao.upsertFavorite(minnie.copy(favoritedAtMillis = 200L))

        assertEquals(listOf(minnie, mickey), dao.observeFavorites().first())
    }

    @Test
    fun upsert_replacesExistingFavorite() = runBlocking {
        dao.upsertFavorite(mickey.copy(name = "Old name", favoritedAtMillis = 100L))
        dao.upsertFavorite(mickey.copy(name = "Mickey Mouse", favoritedAtMillis = 200L))

        assertEquals(listOf(mickey.copy(favoritedAtMillis = 200L)), dao.observeFavorites().first())
    }

    @Test
    fun delete_removesFavorite() = runBlocking {
        dao.upsertFavorite(mickey)

        dao.deleteFavorite(4703)

        assertEquals(emptyList<FavoriteCharacterEntity>(), dao.observeFavorites().first())
        assertFalse(dao.isFavorite(4703))
    }

    @Test
    fun favoriteIdsAndSingleFavoriteAreObserved() = runBlocking {
        dao.upsertFavorite(mickey)

        assertEquals(listOf(4703), dao.observeFavoriteIds().first())
        assertTrue(dao.observeIsFavorite(4703).first())
        assertFalse(dao.observeIsFavorite(1949).first())
    }
}

private val mickey = FavoriteCharacterEntity(
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
    favoritedAtMillis = 100L,
)

private val minnie = FavoriteCharacterEntity(
    id = 1949,
    name = "Minnie Mouse",
    alignment = null,
    imageUrl = null,
    sourceUrl = null,
    apiUrl = "https://api.disneyapi.dev/characters/1949",
    films = emptyList(),
    shortFilms = emptyList(),
    tvShows = emptyList(),
    videoGames = emptyList(),
    parkAttractions = emptyList(),
    allies = emptyList(),
    enemies = emptyList(),
    favoritedAtMillis = 200L,
)
