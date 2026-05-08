package com.example.disneycast

import android.app.Application
import com.example.disneycast.di.charactersDataModule
import com.example.disneycast.di.charactersDomainModule
import com.example.disneycast.di.charactersPresentationModule
import com.example.disneycast.di.coreDataModule
import com.example.disneycast.di.filmsPresentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class DisneyCastApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@DisneyCastApplication)
            modules(
                coreDataModule,
                charactersDataModule,
                charactersDomainModule,
                charactersPresentationModule,
                filmsPresentationModule,
            )
        }
    }
}
