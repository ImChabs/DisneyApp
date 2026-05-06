package com.example.disneyapp.feature.characters.domain.usecase

import com.example.disneyapp.feature.characters.domain.FavoriteCharacterLocalDataSource

class ObserveFavoriteCharacterIdsUseCase(
    private val localDataSource: FavoriteCharacterLocalDataSource,
) {
    operator fun invoke() = localDataSource.observeFavoriteIds()
}
