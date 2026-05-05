package com.example.disneyapp

import android.app.Application
import com.example.disneyapp.di.charactersDataModule
import com.example.disneyapp.di.charactersDomainModule
import com.example.disneyapp.di.charactersPresentationModule
import com.example.disneyapp.di.coreDataModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class DisneyAppApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@DisneyAppApplication)
            modules(
                coreDataModule,
                charactersDataModule,
                charactersDomainModule,
                charactersPresentationModule,
            )
        }
    }
}
