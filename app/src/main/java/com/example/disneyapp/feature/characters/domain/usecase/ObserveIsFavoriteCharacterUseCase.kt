package com.example.disneyapp.feature.characters.domain.usecase

import com.example.disneyapp.feature.characters.domain.FavoriteCharacterLocalDataSource

class ObserveIsFavoriteCharacterUseCase(
    private val localDataSource: FavoriteCharacterLocalDataSource,
) {
    operator fun invoke(characterId: Int) = localDataSource.observeIsFavorite(characterId)
}
