package com.example.disneycast.feature.characters.domain.usecase

import com.example.disneycast.core.domain.DataError
import com.example.disneycast.core.domain.Result
import com.example.disneycast.core.domain.map
import com.example.disneycast.feature.characters.domain.model.CharacterPage
import com.example.disneycast.feature.characters.domain.model.hasVisibleContent
import com.example.disneycast.feature.characters.domain.repository.CharacterRepository

class GetCharactersUseCase(
    private val repository: CharacterRepository,
) {
    suspend operator fun invoke(
        page: Int,
        pageSize: Int,
    ): Result<CharacterPage, DataError> =
        repository.getCharacters(page = page, pageSize = pageSize)
            .map { characterPage ->
                characterPage.copy(
                    characters = characterPage.characters.filter { it.hasVisibleContent },
                )
            }
}
