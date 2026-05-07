package com.example.disneyapp.di

import androidx.room.Room
import com.example.disneyapp.core.data.local.DisneyAppDatabase
import com.example.disneyapp.core.data.network.HttpClientFactory
import com.example.disneyapp.feature.characters.data.local.RoomFavoriteCharacterLocalDataSource
import com.example.disneyapp.feature.characters.data.remote.CharacterRemoteDataSource
import com.example.disneyapp.feature.characters.data.remote.KtorCharacterRemoteDataSource
import com.example.disneyapp.feature.characters.data.repository.NetworkCharacterRepository
import com.example.disneyapp.feature.characters.domain.FavoriteCharacterLocalDataSource
import com.example.disneyapp.feature.characters.domain.repository.CharacterRepository
import com.example.disneyapp.feature.characters.domain.usecase.GetCharacterDetailUseCase
import com.example.disneyapp.feature.characters.domain.usecase.GetCharactersUseCase
import com.example.disneyapp.feature.characters.domain.usecase.ObserveFavoriteCharacterIdsUseCase
import com.example.disneyapp.feature.characters.domain.usecase.ObserveFavoriteCharactersUseCase
import com.example.disneyapp.feature.characters.domain.usecase.ObserveIsFavoriteCharacterUseCase
import com.example.disneyapp.feature.characters.domain.usecase.RemoveFavoriteCharacterUseCase
import com.example.disneyapp.feature.characters.domain.usecase.SaveFavoriteCharacterUseCase
import com.example.disneyapp.feature.characters.domain.usecase.SearchCharactersUseCase
import com.example.disneyapp.feature.characters.domain.usecase.ToggleFavoriteCharacterUseCase
import com.example.disneyapp.feature.characters.presentation.detail.CharacterDetailViewModel
import com.example.disneyapp.feature.characters.presentation.favorites.FavoriteCharactersViewModel
import com.example.disneyapp.feature.characters.presentation.list.CharacterListViewModel
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
            DisneyAppDatabase::class.java,
            "disney_app.db",
        ).build()
    }
    single { get<DisneyAppDatabase>().favoriteCharacterDao() }
}

val charactersDataModule = module {
    singleOf(::KtorCharacterRemoteDataSource) { bind<CharacterRemoteDataSource>() }
    singleOf(::NetworkCharacterRepository) { bind<CharacterRepository>() }
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
