package com.example.disneycast.feature.characters.domain.usecase

import com.example.disneycast.feature.characters.domain.FavoriteCharacterLocalDataSource
import com.example.disneycast.feature.characters.domain.model.DisneyCharacter

class SaveFavoriteCharacterUseCase(
    private val localDataSource: FavoriteCharacterLocalDataSource,
) {
    suspend operator fun invoke(character: DisneyCharacter) =
        localDataSource.saveFavorite(character)
}
