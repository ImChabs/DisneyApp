package com.example.disneyapp.feature.characters.domain.usecase

import com.example.disneyapp.core.domain.DataError
import com.example.disneyapp.core.domain.Result
import com.example.disneyapp.feature.characters.domain.model.CharacterPage
import com.example.disneyapp.feature.characters.domain.repository.CharacterRepository

class GetCharactersUseCase(
    private val repository: CharacterRepository,
) {
    suspend operator fun invoke(
        page: Int,
        pageSize: Int,
    ): Result<CharacterPage, DataError.Network> =
        repository.getCharacters(page = page, pageSize = pageSize)
}
