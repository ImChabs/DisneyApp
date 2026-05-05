package com.example.disneyapp.feature.characters.domain.usecase

import com.example.disneyapp.core.domain.DataError
import com.example.disneyapp.core.domain.Result
import com.example.disneyapp.feature.characters.domain.model.DisneyCharacter
import com.example.disneyapp.feature.characters.domain.repository.CharacterRepository

class GetCharacterDetailUseCase(
    private val repository: CharacterRepository,
) {
    suspend operator fun invoke(id: Int): Result<DisneyCharacter, DataError.Network> =
        repository.getCharacter(id)
}
