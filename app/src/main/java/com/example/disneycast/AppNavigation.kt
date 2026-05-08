package com.example.disneycast

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.disneycast.feature.characters.presentation.detail.CharacterDetailRoot
import com.example.disneycast.feature.characters.presentation.favorites.FavoriteCharactersRoot
import com.example.disneycast.feature.characters.presentation.list.CharacterListRoot
import com.example.disneycast.feature.films.presentation.FilmsRoot
import kotlinx.serialization.Serializable

@Serializable
data object CharacterListRoute : NavKey

@Serializable
data object FavoriteCharactersRoute : NavKey

@Serializable
data object FilmsRoute : NavKey

@Serializable
data class CharacterDetailRoute(
    val characterId: Int,
) : NavKey

private fun NavKey.shouldResetCharactersOnReturn(): Boolean =
    when (this) {
        FilmsRoute -> true
        else -> false
    }

@Composable
fun DisneyCastRoot() {
    val backStack = rememberNavBackStack(CharacterListRoute)
    var characterListResetRequest by remember { mutableIntStateOf(0) }

    fun popToCharacters() {
        while (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }

    fun resetCharactersFromCatalog() {
        popToCharacters()
        characterListResetRequest += 1
    }

    fun navigateToFilms() {
        if (backStack.lastOrNull() == FilmsRoute) return

        popToCharacters()
        backStack.add(FilmsRoute)
    }

    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
        ),
        onBack = {
            if (backStack.size > 1) {
                val leavingRoute = backStack.lastOrNull()
                backStack.removeAt(backStack.lastIndex)
                if (leavingRoute?.shouldResetCharactersOnReturn() == true &&
                    backStack.lastOrNull() == CharacterListRoute
                ) {
                    characterListResetRequest += 1
                }
            }
        },
        entryProvider = entryProvider {
            entry<CharacterListRoute> {
                CharacterListRoot(
                    resetRequest = characterListResetRequest,
                    onCharacterClick = { characterId ->
                        backStack.add(CharacterDetailRoute(characterId = characterId))
                    },
                    onFavoritesClick = {
                        backStack.add(FavoriteCharactersRoute)
                    },
                    onFilmsClick = ::navigateToFilms,
                )
            }
            entry<FilmsRoute> {
                FilmsRoot(
                    onCharactersClick = ::resetCharactersFromCatalog,
                    onFavoritesClick = {
                        backStack.add(FavoriteCharactersRoute)
                    },
                    onCharacterClick = { characterId ->
                        backStack.add(CharacterDetailRoute(characterId = characterId))
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
