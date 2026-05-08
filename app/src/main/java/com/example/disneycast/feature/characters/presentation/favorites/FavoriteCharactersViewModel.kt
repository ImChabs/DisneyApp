package com.example.disneycast.feature.characters.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.disneycast.core.domain.Result
import com.example.disneycast.core.presentation.toUiText
import com.example.disneycast.feature.characters.domain.model.DisneyCharacter
import com.example.disneycast.feature.characters.domain.usecase.ObserveFavoriteCharactersUseCase
import com.example.disneycast.feature.characters.domain.usecase.RemoveFavoriteCharacterUseCase
import com.example.disneycast.feature.characters.presentation.list.toCharacterListItemUi
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
    private var favoriteCharacters = emptyList<DisneyCharacter>()

    init {
        observeFavorites()
    }

    fun onAction(action: FavoriteCharactersAction) {
        when (action) {
            is FavoriteCharactersAction.OnSearchQueryChange -> onSearchQueryChange(action.query)
            is FavoriteCharactersAction.OnFavoriteClick -> removeFavorite(action.characterId)
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            observeFavoriteCharactersUseCase().collect { characters ->
                favoriteCharacters = characters
                favorites.clear()
                favorites.putAll(characters.associateBy { it.id })
                updateFilteredFavorites()
            }
        }
    }

    private fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        updateFilteredFavorites()
    }

    private fun updateFilteredFavorites() {
        _state.update { state ->
            val query = state.searchQuery.trim()
            val filteredFavorites = if (query.isBlank()) {
                favoriteCharacters
            } else {
                favoriteCharacters.filter { character ->
                    character.name.orEmpty().contains(query, ignoreCase = true)
                }
            }

            state.copy(
                favorites = filteredFavorites.map { character ->
                    character.toCharacterListItemUi(isFavorite = true)
                },
                totalFavoritesCount = favoriteCharacters.size,
            )
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
