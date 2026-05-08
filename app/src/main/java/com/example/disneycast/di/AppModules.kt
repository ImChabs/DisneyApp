package com.example.disneycast.di

import androidx.room.Room
import com.example.disneycast.core.data.local.DisneyCastDatabase
import com.example.disneycast.core.data.network.HttpClientFactory
import com.example.disneycast.feature.characters.data.local.RoomCharacterLocalDataSource
import com.example.disneycast.feature.characters.data.local.RoomFavoriteCharacterLocalDataSource
import com.example.disneycast.feature.characters.data.remote.CharacterRemoteDataSource
import com.example.disneycast.feature.characters.data.remote.KtorCharacterRemoteDataSource
import com.example.disneycast.feature.characters.data.repository.OfflineFirstCharacterRepository
import com.example.disneycast.feature.characters.domain.CharacterLocalDataSource
import com.example.disneycast.feature.characters.domain.FavoriteCharacterLocalDataSource
import com.example.disneycast.feature.characters.domain.repository.CharacterRepository
import com.example.disneycast.feature.characters.domain.usecase.GetCharacterDetailUseCase
import com.example.disneycast.feature.characters.domain.usecase.GetCharactersUseCase
import com.example.disneycast.feature.characters.domain.usecase.ObserveFavoriteCharacterIdsUseCase
import com.example.disneycast.feature.characters.domain.usecase.ObserveFavoriteCharactersUseCase
import com.example.disneycast.feature.characters.domain.usecase.ObserveIsFavoriteCharacterUseCase
import com.example.disneycast.feature.characters.domain.usecase.RemoveFavoriteCharacterUseCase
import com.example.disneycast.feature.characters.domain.usecase.SaveFavoriteCharacterUseCase
import com.example.disneycast.feature.characters.domain.usecase.SearchCharactersUseCase
import com.example.disneycast.feature.characters.domain.usecase.ToggleFavoriteCharacterUseCase
import com.example.disneycast.feature.characters.presentation.detail.CharacterDetailViewModel
import com.example.disneycast.feature.characters.presentation.favorites.FavoriteCharactersViewModel
import com.example.disneycast.feature.characters.presentation.list.CharacterListViewModel
import com.example.disneycast.feature.films.presentation.FilmsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val coreDataModule = module {
    single { HttpClientFactory.create() }
    single {
        Room.databaseBuilder(
            androidContext(),
            DisneyCastDatabase::class.java,
            "disney_cast.db",
        ).build()
    }
    single { get<DisneyCastDatabase>().favoriteCharacterDao() }
    single { get<DisneyCastDatabase>().characterCacheDao() }
}

val charactersDataModule = module {
    singleOf(::KtorCharacterRemoteDataSource) { bind<CharacterRemoteDataSource>() }
    singleOf(::RoomCharacterLocalDataSource) { bind<CharacterLocalDataSource>() }
    singleOf(::OfflineFirstCharacterRepository) { bind<CharacterRepository>() }
    singleOf(::RoomFavoriteCharacterLocalDataSource) { bind<FavoriteCharacterLocalDataSource>() }
}

val charactersDomainModule = module {
    factoryOf(::GetCharactersUseCase)
    factoryOf(::SearchCharactersUseCase)
    factoryOf(::GetCharacterDetailUseCase)
    factoryOf(::ObserveFavoriteCharactersUseCase)
    factoryOf(::ObserveFavoriteCharacterIdsUseCase)
    factoryOf(::ObserveIsFavoriteCharacterUseCase)
    factoryOf(::SaveFavoriteCharacterUseCase)
    factoryOf(::RemoveFavoriteCharacterUseCase)
    factoryOf(::ToggleFavoriteCharacterUseCase)
}

val charactersPresentationModule = module {
    viewModelOf(::CharacterListViewModel)
    viewModelOf(::CharacterDetailViewModel)
    viewModelOf(::FavoriteCharactersViewModel)
}

val filmsPresentationModule = module {
    viewModelOf(::FilmsViewModel)
}
