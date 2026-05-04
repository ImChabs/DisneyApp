package com.example.disneyapp.feature.characters.data.remote

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
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
                                  "url": "https://api.disneyapi.dev/characters/4703"
                                }
                              ]
                            }
                        """.trimIndent(),
                    )
                },
            ),
        )

        val result = dataSource.getCharacters()

        val characters = result.successData()
        assertThat(characters).hasSize(1)
        assertThat(characters.first().id).isEqualTo(4703)
        assertThat(characters.first().name).isEqualTo("Mickey Mouse")
        assertThat(characters.first().films).isEqualTo(listOf("Fantasia"))
    }

    @Test
    fun `getCharacter decodes detail response into one domain model`() = runTest {
        val dataSource = KtorCharacterRemoteDataSource(
            httpClient = HttpClientFactory.create(
                MockEngine {
                    respondJson(
                        """
                            {
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
    fun `searchCharacters sends name query parameter`() = runTest {
        var requestedUrl = ""
        val dataSource = KtorCharacterRemoteDataSource(
            httpClient = HttpClientFactory.create(
                MockEngine { request ->
                    requestedUrl = request.url.toString()
                    respondJson("""{"data": []}""")
                },
            ),
        )

        val result = dataSource.searchCharacters(name = "Mickey Mouse")

        assertThat(result.successData()).isEqualTo(emptyList())
        assertThat(requestedUrl).contains("/character")
        assertThat(requestedUrl).contains("name=Mickey+Mouse")
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
