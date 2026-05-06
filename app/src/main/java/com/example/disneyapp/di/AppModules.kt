package com.example.disneyapp.di

import com.example.disneyapp.core.data.network.HttpClientFactory
import com.example.disneyapp.feature.characters.data.remote.CharacterRemoteDataSource
import com.example.disneyapp.feature.characters.data.remote.KtorCharacterRemoteDataSource
import com.example.disneyapp.feature.characters.data.repository.NetworkCharacterRepository
import com.example.disneyapp.feature.characters.domain.repository.CharacterRepository
import com.example.disneyapp.feature.characters.domain.usecase.GetCharacterDetailUseCase
import com.example.disneyapp.feature.characters.domain.usecase.GetCharactersUseCase
import com.example.disneyapp.feature.characters.domain.usecase.SearchCharactersUseCase
import com.example.disneyapp.feature.characters.presentation.detail.CharacterDetailViewModel
import com.example.disneyapp.feature.characters.presentation.list.CharacterListViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val coreDataModule = module {
    single { HttpClientFactory.create() }
}

val charactersDataModule = module {
    singleOf(::KtorCharacterRemoteDataSource) { bind<CharacterRemoteDataSource>() }
    singleOf(::NetworkCharacterRepository) { bind<CharacterRepository>() }
}

val charactersDomainModule = module {
    factoryOf(::GetCharactersUseCase)
    factoryOf(::SearchCharactersUseCase)
    factoryOf(::GetCharacterDetailUseCase)
}

val charactersPresentationModule = module {
    viewModelOf(::CharacterListViewModel)
    viewModelOf(::CharacterDetailViewModel)
}
