package com.example.disneycast.feature.characters.domain.usecase

import com.example.disneycast.feature.characters.domain.FavoriteCharacterLocalDataSource

class ObserveIsFavoriteCharacterUseCase(
    private val localDataSource: FavoriteCharacterLocalDataSource,
) {
    operator fun invoke(characterId: Int) = localDataSource.observeIsFavorite(characterId)
}
