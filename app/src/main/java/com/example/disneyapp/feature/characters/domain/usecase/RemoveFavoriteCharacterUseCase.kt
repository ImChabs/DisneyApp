package com.example.disneyapp.feature.characters.domain.usecase

import com.example.disneyapp.feature.characters.domain.FavoriteCharacterLocalDataSource

class RemoveFavoriteCharacterUseCase(
    private val localDataSource: FavoriteCharacterLocalDataSource,
) {
    suspend operator fun invoke(characterId: Int) =
        localDataSource.removeFavorite(characterId)
}
