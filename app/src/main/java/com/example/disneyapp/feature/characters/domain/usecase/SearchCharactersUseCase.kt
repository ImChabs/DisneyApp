package com.example.disneyapp.feature.characters.domain.usecase

import com.example.disneyapp.core.domain.DataError
import com.example.disneyapp.core.domain.Result
import com.example.disneyapp.feature.characters.domain.model.DisneyCharacter
import com.example.disneyapp.feature.characters.domain.repository.CharacterRepository

class SearchCharactersUseCase(
    private val repository: CharacterRepository,
) {
    suspend operator fun invoke(query: String): Result<List<DisneyCharacter>, DataError.Network> {
        val trimmedQuery = query.trim()

        if (trimmedQuery.isBlank()) {
            return Result.Success(emptyList())
        }

        return repository.searchCharacters(trimmedQuery)
    }
}
