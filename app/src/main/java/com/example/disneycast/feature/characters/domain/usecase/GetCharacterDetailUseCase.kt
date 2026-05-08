package com.example.disneycast.feature.characters.domain.usecase

import com.example.disneycast.core.domain.DataError
import com.example.disneycast.core.domain.Result
import com.example.disneycast.feature.characters.domain.model.CharacterDetail
import com.example.disneycast.feature.characters.domain.repository.CharacterRepository

class GetCharacterDetailUseCase(
    private val repository: CharacterRepository,
) {
    suspend operator fun invoke(id: Int): Result<CharacterDetail, DataError> =
        repository.getCharacter(id)
}
