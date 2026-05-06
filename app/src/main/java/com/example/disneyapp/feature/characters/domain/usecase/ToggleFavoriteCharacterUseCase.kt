package com.example.disneyapp.feature.characters.domain.usecase

import com.example.disneyapp.core.domain.DataError
import com.example.disneyapp.core.domain.EmptyResult
import com.example.disneyapp.core.domain.Result
import com.example.disneyapp.feature.characters.domain.FavoriteCharacterLocalDataSource
import com.example.disneyapp.feature.characters.domain.model.DisneyCharacter

class ToggleFavoriteCharacterUseCase(
    private val localDataSource: FavoriteCharacterLocalDataSource,
) {
    suspend operator fun invoke(character: DisneyCharacter): EmptyResult<DataError.Local> =
        when (val result = localDataSource.isFavorite(character.id)) {
            is Result.Failure -> Result.Failure(result.error)
            is Result.Success -> {
                if (result.data) {
                    localDataSource.removeFavorite(character.id)
                } else {
                    localDataSource.saveFavorite(character)
                }
            }
        }
}
