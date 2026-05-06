package com.example.disneyapp.feature.characters.data.remote

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import assertk.assertions.isInstanceOf
import com.example.disneyapp.core.data.network.HttpClientFactory
import com.example.disneyapp.core.domain.DataError
import com.example.disneyapp.core.domain.Result
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class KtorCharacterRemoteDataSourceTest {
    @Test
    fun `getCharacters decodes list response into domain models`() = runTest {
        val dataSource = KtorCharacterRemoteDataSource(
            httpClient = HttpClientFactory.create(
                MockEngine {
                    respondJson(
                        """
                            {
                              "info": {
                                "count": 2,
                                "totalPages": 1,
                                "previousPage": null,
                                "nextPage": null
                              },
                              "data": [
                                {
                                  "_id": 4703,
                                  "films": ["Fantasia"],
                                  "shortFilms": ["Steamboat Willie"],
                                  "tvShows": ["Mickey Mouse Clubhouse"],
                                  "videoGames": ["Kingdom Hearts"],
                                  "parkAttractions": ["Mickey's PhilharMagic"],
                                  "allies": ["Minnie Mouse"],
                                  "enemies": ["Pete"],
                                  "sourceUrl": "https://disney.fandom.com/wiki/Mickey_Mouse",
                                  "name": "Mickey Mouse",
                                  "imageUrl": "https://example.com/mickey.jpg",
                                  "url": "https://api.disneyapi.dev/characters/4703",
                                  "createdAt": "2021-04-12T01:33:34.458Z",
                                  "updatedAt": "2021-04-12T01:33:34.458Z",
                                  "__v": 0
                                },
                                {
                                  "_id": 293,
                                  "films": null,
                                  "shortFilms": [],
                                  "name": "Arabella",
                                  "url": "https://api.disneyapi.dev/characters/293"
                                }
                              ]
                            }
                        """.trimIndent(),
                    )
                },
            ),
        )

        val result = dataSource.getCharacters(page = 1, pageSize = 30)

        val page = result.successData()
        val characters = page.characters
        assertThat(characters).hasSize(2)
        assertThat(page.currentPage).isEqualTo(1)
        assertThat(page.pageSize).isEqualTo(30)
        assertThat(page.totalPages).isEqualTo(1)
        assertThat(characters.first().id).isEqualTo(4703)
        assertThat(characters.first().name).isEqualTo("Mickey Mouse")
        assertThat(characters.first().films).isEqualTo(listOf("Fantasia"))
        assertThat(characters[1].id).isEqualTo(293)
        assertThat(characters[1].imageUrl).isEqualTo(null)
        assertThat(characters[1].films).isEqualTo(emptyList())
        assertThat(characters[1].tvShows).isEqualTo(emptyList())
    }

    @Test
    fun `searchCharacters decodes single object data response into domain list`() = runTest {
        val dataSource = KtorCharacterRemoteDataSource(
            httpClient = HttpClientFactory.create(
                MockEngine {
                    respondJson(
                        """
                            {
                              "info": {
                                "count": 1,
                                "totalPages": 1,
                                "previousPage": null,
                                "nextPage": null
                              },
                              "data": {
                                "_id": 18,
                                "films": ["The Fox and the Hound", "The Fox and the Hound 2"],
                                "shortFilms": [],
                                "tvShows": [],
                                "videoGames": [],
                                "parkAttractions": [],
                                "allies": [],
                                "enemies": [],
                                "name": "Abigail the Cow",
                                "imageUrl": "https://example.com/abigail.jpg",
                                "url": "https://api.disneyapi.dev/characters/18"
                              }
                            }
                        """.trimIndent(),
                    )
                },
            ),
        )

        val page = dataSource.searchCharacters(name = "the cow", page = 1, pageSize = 30)
            .successData()

        assertThat(page.characters).hasSize(1)
        assertThat(page.currentPage).isEqualTo(1)
        assertThat(page.pageSize).isEqualTo(30)
        assertThat(page.totalPages).isEqualTo(1)
        assertThat(page.hasNextPage).isEqualTo(false)
        assertThat(page.characters.first().id).isEqualTo(18)
        assertThat(page.characters.first().name).isEqualTo("Abigail the Cow")
        assertThat(page.characters.first().films).isEqualTo(
            listOf("The Fox and the Hound", "The Fox and the Hound 2")
        )
    }

    @Test
    fun `searchCharacters decodes empty response into empty page`() = runTest {
        val dataSource = KtorCharacterRemoteDataSource(
            httpClient = HttpClientFactory.create(
                MockEngine {
                    respondJson(
                        """
                            {
                              "info": {
                                "count": 0,
                                "totalPages": 0,
                                "previousPage": null,
                                "nextPage": null
                              },
                              "data": []
                            }
                        """.trimIndent(),
                    )
                },
            ),
        )

        val page = dataSource.searchCharacters(
            name = "zzzz-no-character-hopefully",
            page = 1,
            pageSize = 30,
        ).successData()

        assertThat(page.characters).isEqualTo(emptyList())
        assertThat(page.currentPage).isEqualTo(1)
        assertThat(page.pageSize).isEqualTo(30)
        assertThat(page.totalPages).isEqualTo(0)
        assertThat(page.hasNextPage).isEqualTo(false)
    }

    @Test
    fun `getCharacter decodes detail response into one domain model`() = runTest {
        val dataSource = KtorCharacterRemoteDataSource(
            httpClient = HttpClientFactory.create(
                MockEngine {
                    respondJson(
                        """
                            {
                              "info": {
                                "count": 1
                              },
                              "data": {
                                "_id": 308,
                                "films": ["Tangled"],
                                "name": "Queen Arianna",
                                "imageUrl": "https://example.com/arianna.jpg",
                                "url": "https://api.disneyapi.dev/characters/308"
                              }
                            }
                        """.trimIndent(),
                    )
                },
            ),
        )

        val character = dataSource.getCharacter(id = 308).successData()

        assertThat(character.id).isEqualTo(308)
        assertThat(character.name).isEqualTo("Queen Arianna")
        assertThat(character.films).isEqualTo(listOf("Tangled"))
    }

    @Test
    fun `getCharacters returns serialization failure for malformed json`() = runTest {
        val dataSource = KtorCharacterRemoteDataSource(
            httpClient = HttpClientFactory.create(
                MockEngine {
                    respondJson("""{"data": [""")
                },
            ),
        )

        val result = dataSource.getCharacters(page = 1, pageSize = 30)

        assertThat(result).isEqualTo(Result.Failure(DataError.Network.SERIALIZATION))
    }

    @Test
    fun `getCharacters sends page query parameters`() = runTest {
        var requestedUrl = ""
        val dataSource = KtorCharacterRemoteDataSource(
            httpClient = HttpClientFactory.create(
                MockEngine { request ->
                    requestedUrl = request.url.toString()
                    respondJson(
                        """
                            {
                              "info": {
                                "count": 30,
                                "totalPages": 2,
                                "previousPage": null,
                                "nextPage": "https://api.disneyapi.dev/character?page=2&pageSize=30"
                              },
                              "data": []
                            }
                        """.trimIndent(),
                    )
                },
            ),
        )

        val result = dataSource.getCharacters(page = 1, pageSize = 30)

        assertThat(result.successData().hasNextPage).isTrue()
        assertThat(requestedUrl).contains("/character")
        assertThat(requestedUrl).contains("page=1")
        assertThat(requestedUrl).contains("pageSize=30")
    }

    @Test
    fun `searchCharacters sends name and page query parameters`() = runTest {
        var requestedUrl = ""
        val dataSource = KtorCharacterRemoteDataSource(
            httpClient = HttpClientFactory.create(
                MockEngine { request ->
                    requestedUrl = request.url.toString()
                    respondJson(
                        """
                            {
                              "info": {
                                "count": 0,
                                "totalPages": 1,
                                "previousPage": null,
                                "nextPage": null
                              },
                              "data": []
                            }
                        """.trimIndent(),
                    )
                },
            ),
        )

        val result = dataSource.searchCharacters(name = "Mickey Mouse", page = 2, pageSize = 20)

        assertThat(result.successData().characters).isEqualTo(emptyList())
        assertThat(requestedUrl).contains("/character")
        assertThat(requestedUrl).contains("name=Mickey+Mouse")
        assertThat(requestedUrl).contains("page=2")
        assertThat(requestedUrl).contains("pageSize=20")
    }

    @Test
    fun `getCharacter returns serialization failure when detail data is missing`() = runTest {
        val dataSource = KtorCharacterRemoteDataSource(
            httpClient = HttpClientFactory.create(
                MockEngine {
                    respondJson("""{"info":{"count":1}}""")
                },
            ),
        )

        val result = dataSource.getCharacter(id = 308)

        assertThat(result).isEqualTo(Result.Failure(DataError.Network.SERIALIZATION))
    }
}

private fun MockRequestHandleScope.respondJson(content: String) =
    respond(
        content = content,
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
    )

private fun <D> Result<D, DataError.Network>.successData(): D {
    assertThat(this).isInstanceOf(Result.Success::class)
    return (this as Result.Success).data
}
