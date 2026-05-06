package com.example.disneyapp.feature.characters.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.disneyapp.core.domain.DataError
import com.example.disneyapp.core.domain.Result
import com.example.disneyapp.core.presentation.toUiText
import com.example.disneyapp.feature.characters.domain.model.DisneyCharacter
import com.example.disneyapp.feature.characters.domain.usecase.GetCharactersUseCase
import com.example.disneyapp.feature.characters.domain.usecase.SearchCharactersUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

    private var searchDebounceJob: Job? = null
    private var requestJob: Job? = null
    private var lastSubmittedQuery: String? = null

    init {
        submitQuery(query = "", force = true)
    }

    fun onAction(action: CharacterListAction) {
        when (action) {
            is CharacterListAction.OnSearchQueryChange -> onSearchQueryChange(action.query)
            CharacterListAction.OnRetryClick -> retry()
        }
    }

    private fun onSearchQueryChange(query: String) {
        requestJob?.cancel()
        _state.update {
            it.copy(
                searchQuery = query,
                isLoading = false,
                error = null,
            )
        }

        searchDebounceJob?.cancel()
        searchDebounceJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MILLIS)
            submitQuery(query = state.value.searchQuery)
        }
    }

    private fun retry() {
        searchDebounceJob?.cancel()
        submitQuery(query = state.value.searchQuery, force = true)
    }

    private fun submitQuery(
        query: String,
        force: Boolean = false,
    ) {
        val trimmedQuery = query.trim()
        if (!force && trimmedQuery == lastSubmittedQuery) return

        lastSubmittedQuery = trimmedQuery
        requestJob?.cancel()
        requestJob = viewModelScope.launch {
            handleCharactersResult {
                if (trimmedQuery.isBlank()) {
                    getCharactersUseCase()
                } else {
                    searchCharactersUseCase(trimmedQuery)
                }
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
                    it.copy(isLoading = false, error = result.error.toUiText())
                }
            }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MILLIS = 300L
    }
}
