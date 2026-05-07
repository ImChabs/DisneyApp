package com.example.disneyapp

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.disneyapp.feature.characters.presentation.detail.CharacterDetailRoot
import com.example.disneyapp.feature.characters.presentation.favorites.FavoriteCharactersRoot
import com.example.disneyapp.feature.characters.presentation.list.CharacterListRoot
import kotlinx.serialization.Serializable

@Serializable
data object CharacterListRoute : NavKey

@Serializable
data object FavoriteCharactersRoute : NavKey

@Serializable
data class CharacterDetailRoute(
    val characterId: Int,
) : NavKey

@Composable
fun DisneyAppRoot() {
    val backStack = rememberNavBackStack(CharacterListRoute)

    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
        ),
        onBack = {
            if (backStack.size > 1) {
                backStack.removeAt(backStack.lastIndex)
            }
        },
        entryProvider = entryProvider {
            entry<CharacterListRoute> {
                CharacterListRoot(
                    onCharacterClick = { characterId ->
                        backStack.add(CharacterDetailRoute(characterId = characterId))
                    },
                    onFavoritesClick = {
                        backStack.add(FavoriteCharactersRoute)
                    },
                )
            }
            entry<FavoriteCharactersRoute> {
                FavoriteCharactersRoot(
                    onBackClick = {
                        if (backStack.size > 1) {
                            backStack.removeAt(backStack.lastIndex)
                        }
                    },
                    onCharacterClick = { characterId ->
                        backStack.add(CharacterDetailRoute(characterId = characterId))
                    },
                )
            }
            entry<CharacterDetailRoute> { route ->
                CharacterDetailRoot(
                    characterId = route.characterId,
                    onBackClick = {
                        if (backStack.size > 1) {
                            backStack.removeAt(backStack.lastIndex)
                        }
                    },
                )
            }
        },
    )
}
