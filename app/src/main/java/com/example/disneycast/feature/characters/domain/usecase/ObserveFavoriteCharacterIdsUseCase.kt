package com.example.disneycast.feature.characters.domain.usecase

import com.example.disneycast.feature.characters.domain.FavoriteCharacterLocalDataSource

class ObserveFavoriteCharacterIdsUseCase(
    private val localDataSource: FavoriteCharacterLocalDataSource,
) {
    operator fun invoke() = localDataSource.observeFavoriteIds()
}
