package com.example.disneyapp.core.data.local

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

class StringListTypeConverters {
    private val serializer = ListSerializer(String.serializer())

    @TypeConverter
    fun fromStringList(value: List<String>): String =
        Json.encodeToString(serializer, value)

    @TypeConverter
    fun toStringList(value: String): List<String> =
        runCatching {
            Json.decodeFromString(serializer, value)
        }.getOrDefault(emptyList())
}
