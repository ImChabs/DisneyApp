package com.example.disneyapp.feature.characters.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.disneyapp.core.data.local.DisneyAppDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CharacterCacheDaoTest {
    private lateinit var database: DisneyAppDatabase
    private lateinit var dao: CharacterCacheDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, DisneyAppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.characterCacheDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getCharacters_returnsRequestedPageInPositionOrder() = runBlocking {
        dao.upsertCharacters(
            listOf(
                minnie.copy(page = 1, pageSize = 30, pagePosition = 1),
                mickey.copy(page = 1, pageSize = 30, pagePosition = 0),
                donald.copy(page = 2, pageSize = 30, pagePosition = 0),
            )
        )

        assertEquals(listOf(mickey, minnie), dao.getCharacters(page = 1, pageSize = 30))
    }

    @Test
    fun upsertCharacter_preservesSingleCachedCharacterById() = runBlocking {
        dao.upsertCharacter(mickey.copy(name = "Old name"))

        dao.upsertCharacter(mickey.copy(name = "Mickey Mouse"))

        assertEquals(mickey, dao.getCharacter(4703))
    }

    @Test
    fun deleteCharactersForPage_removesOnlyRequestedPage() = runBlocking {
        dao.upsertCharacters(
            listOf(
                mickey.copy(page = 1, pageSize = 30),
                minnie.copy(page = 1, pageSize = 30),
                donald.copy(page = 2, pageSize = 30),
            )
        )

        dao.deleteCharactersForPage(page = 1, pageSize = 30)

        assertEquals(emptyList<CharacterCacheEntity>(), dao.getCharacters(page = 1, pageSize = 30))
        assertEquals(listOf(donald), dao.getCharacters(page = 2, pageSize = 30))
    }

    @Test
    fun getCharacter_returnsNullForMissingId() = runBlocking {
        assertNull(dao.getCharacter(4703))
    }
}

private val mickey = CharacterCacheEntity(
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
    page = 1,
    pageSize = 30,
    pagePosition = 0,
    cachedAtMillis = 100L,
)

private val minnie = mickey.copy(
    id = 1949,
    name = "Minnie Mouse",
    pagePosition = 1,
)

private val donald = mickey.copy(
    id = 1122,
    name = "Donald Duck",
    page = 2,
)
