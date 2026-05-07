package com.example.disneyapp.feature.characters.domain.usecase

import com.example.disneyapp.feature.characters.domain.FavoriteCharacterLocalDataSource
import com.example.disneyapp.feature.characters.domain.model.DisneyCharacter

class SaveFavoriteCharacterUseCase(
    private val localDataSource: FavoriteCharacterLocalDataSource,
) {
    suspend operator fun invoke(character: DisneyCharacter) =
        localDataSource.saveFavorite(character)
}
