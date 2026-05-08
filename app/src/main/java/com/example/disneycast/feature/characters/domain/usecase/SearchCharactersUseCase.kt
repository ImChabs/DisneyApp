package com.example.disneycast.feature.characters.domain.usecase

import com.example.disneycast.core.domain.DataError
import com.example.disneycast.core.domain.Result
import com.example.disneycast.core.domain.map
import com.example.disneycast.feature.characters.domain.model.CharacterPage
import com.example.disneycast.feature.characters.domain.model.hasVisibleContent
import com.example.disneycast.feature.characters.domain.repository.CharacterRepository

class SearchCharactersUseCase(
    private val repository: CharacterRepository,
) {
    suspend operator fun invoke(
        query: String,
        page: Int,
        pageSize: Int,
    ): Result<CharacterPage, DataError> {
        val trimmedQuery = query.trim()

        if (trimmedQuery.isBlank()) {
            return Result.Success(
                CharacterPage(
                    characters = emptyList(),
                    currentPage = page,
                    pageSize = pageSize,
                    totalPages = 0,
                    hasNextPage = false,
                )
            )
        }

        return repository.searchCharacters(
            name = trimmedQuery,
            page = page,
            pageSize = pageSize,
        ).map { characterPage ->
            characterPage.copy(
                characters = characterPage.characters.filter { it.hasVisibleContent },
            )
        }
    }
}
