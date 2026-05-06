package com.example.disneyapp.feature.characters.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.disneyapp.core.domain.Result
import com.example.disneyapp.core.presentation.toUiText
import com.example.disneyapp.feature.characters.domain.model.DisneyCharacter
import com.example.disneyapp.feature.characters.domain.usecase.ObserveFavoriteCharactersUseCase
import com.example.disneyapp.feature.characters.domain.usecase.RemoveFavoriteCharacterUseCase
import com.example.disneyapp.feature.characters.presentation.list.toCharacterListItemUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoriteCharactersViewModel(
    private val observeFavoriteCharactersUseCase: ObserveFavoriteCharactersUseCase,
    private val removeFavoriteCharacterUseCase: RemoveFavoriteCharacterUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(FavoriteCharactersState())
    val state = _state.asStateFlow()

    private val _events = Channel<FavoriteCharactersEvent>()
    val events = _events.receiveAsFlow()

    private val favorites = mutableMapOf<Int, DisneyCharacter>()

    init {
        observeFavorites()
    }

    fun onAction(action: FavoriteCharactersAction) {
        when (action) {
            is FavoriteCharactersAction.OnFavoriteClick -> removeFavorite(action.characterId)
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            observeFavoriteCharactersUseCase().collect { characters ->
                favorites.clear()
                favorites.putAll(characters.associateBy { it.id })
                _state.update {
                    it.copy(
                        favorites = characters.map { character ->
                            character.toCharacterListItemUi(isFavorite = true)
                        },
                    )
                }
            }
        }
    }

    private fun removeFavorite(characterId: Int) {
        if (favorites[characterId] == null) return
        viewModelScope.launch {
            when (val result = removeFavoriteCharacterUseCase(characterId)) {
                is Result.Success -> Unit
                is Result.Failure -> _events.send(
                    FavoriteCharactersEvent.ShowSnackbar(result.error.toUiText()),
                )
            }
        }
    }
}
