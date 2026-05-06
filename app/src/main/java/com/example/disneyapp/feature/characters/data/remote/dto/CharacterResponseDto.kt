package com.example.disneyapp.feature.characters.data.remote.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement

@Serializable
data class CharacterListResponseDto(
    val info: CharacterResponseInfoDto? = null,
    @Serializable(with = CharacterListDataSerializer::class)
    val data: List<CharacterDto> = emptyList(),
)

@Serializable
data class CharacterDetailResponseDto(
    val info: CharacterResponseInfoDto? = null,
    val data: CharacterDto? = null,
)

@Serializable
data class CharacterResponseInfoDto(
    val count: Int? = null,
    val totalPages: Int? = null,
    val previousPage: String? = null,
    val nextPage: String? = null,
)

private object CharacterListDataSerializer : KSerializer<List<CharacterDto>> {
    private val listSerializer = ListSerializer(CharacterDto.serializer())

    override val descriptor = listSerializer.descriptor

    override fun deserialize(decoder: Decoder): List<CharacterDto> {
        val jsonDecoder = decoder as? JsonDecoder
            ?: return listSerializer.deserialize(decoder)

        return when (val element = jsonDecoder.decodeJsonElement()) {
            is JsonArray -> jsonDecoder.json.decodeFromJsonElement(listSerializer, element)
            is JsonObject -> listOf(jsonDecoder.json.decodeFromJsonElement(element))
            JsonNull -> emptyList()
            else -> throw SerializationException(
                "Expected character list data to be an array, object, or null"
            )
        }
    }

    override fun serialize(encoder: Encoder, value: List<CharacterDto>) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: return listSerializer.serialize(encoder, value)

        val element: JsonElement = jsonEncoder.json.encodeToJsonElement(listSerializer, value)
        jsonEncoder.encodeJsonElement(element)
    }
}
