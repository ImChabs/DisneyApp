package com.example.disneycast.feature.characters.domain.usecase

import com.example.disneycast.feature.characters.domain.FavoriteCharacterLocalDataSource

class RemoveFavoriteCharacterUseCase(
    private val localDataSource: FavoriteCharacterLocalDataSource,
) {
    suspend operator fun invoke(characterId: Int) =
        localDataSource.removeFavorite(characterId)
}
