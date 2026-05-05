package com.example.disneyapp.feature.characters.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.disneyapp.core.domain.DataError
import com.example.disneyapp.core.domain.Result
import com.example.disneyapp.core.presentation.toUiText
import com.example.disneyapp.feature.characters.domain.model.DisneyCharacter
import com.example.disneyapp.feature.characters.domain.usecase.GetCharactersUseCase
import com.example.disneyapp.feature.characters.domain.usecase.SearchCharactersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CharacterListViewModel(
    private val getCharactersUseCase: GetCharactersUseCase,
    private val searchCharactersUseCase: SearchCharactersUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(CharacterListState())
    val state = _state.asStateFlow()

    init {
        loadCharacters()
    }

    fun onAction(action: CharacterListAction) {
        when (action) {
            is CharacterListAction.OnSearchQueryChange -> onSearchQueryChange(action.query)
            CharacterListAction.OnRetryClick -> retry()
        }
    }

    private fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }

        if (query.isBlank()) {
            loadCharacters()
        } else {
            searchCharacters(query)
        }
    }

    private fun retry() {
        val query = state.value.searchQuery

        if (query.isBlank()) {
            loadCharacters()
        } else {
            searchCharacters(query)
        }
    }

    private fun loadCharacters() {
        viewModelScope.launch {
            handleCharactersResult {
                getCharactersUseCase()
            }
        }
    }

    private fun searchCharacters(query: String) {
        viewModelScope.launch {
            handleCharactersResult {
                searchCharactersUseCase(query)
            }
        }
    }

    private suspend fun handleCharactersResult(
        request: suspend () -> Result<List<DisneyCharacter>, DataError.Network>,
    ) {
        _state.update { it.copy(isLoading = true, error = null) }

        when (val result = request()) {
            is Result.Success -> {
                _state.update {
                    it.copy(
                        characters = result.data.map(DisneyCharacter::toCharacterListItemUi),
                        isLoading = false,
                        error = null,
                    )
                }
            }
            is Result.Failure -> {
                _state.update {
                    it.copy(
                        characters = emptyList(),
                        isLoading = false,
                        error = result.error.toUiText(),
                    )
                }
            }
        }
    }
}
