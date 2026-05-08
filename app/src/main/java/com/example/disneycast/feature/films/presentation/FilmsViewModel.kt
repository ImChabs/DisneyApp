package com.example.disneycast.feature.films.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.disneycast.core.domain.Result
import com.example.disneycast.core.presentation.toUiText
import com.example.disneycast.feature.characters.domain.usecase.SearchCharactersUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FilmsViewModel(
    private val searchCharactersUseCase: SearchCharactersUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(FilmsState())
    val state = _state.asStateFlow()

    private val _events = Channel<FilmsEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var searchDebounceJob: Job? = null
    private var requestJob: Job? = null
    private var lastSubmittedQuery: String? = null

    fun onAction(action: FilmsAction) {
        when (action) {
            is FilmsAction.OnSearchQueryChange -> onSearchQueryChange(action.query)
            is FilmsAction.OnCharacterClick -> navigateToCharacterDetail(action.characterId)
            FilmsAction.OnRetryClick -> submitQuery(query = state.value.searchQuery, force = true)
        }
    }

    private fun navigateToCharacterDetail(characterId: Int) {
        viewModelScope.launch {
            _events.send(FilmsEvent.NavigateToCharacterDetail(characterId))
        }
    }

    private fun onSearchQueryChange(query: String) {
        requestJob?.cancel()
        searchDebounceJob?.cancel()

        val trimmedQuery = query.trim()
        if (trimmedQuery.isBlank()) {
            lastSubmittedQuery = null
            _state.update {
                FilmsState(
                    searchQuery = query,
                    pageSize = it.pageSize,
                )
            }
            return
        }

        _state.update {
            it.copy(
                searchQuery = query,
                submittedQuery = "",
                results = emptyList(),
                isLoading = false,
                error = null,
            )
        }
        searchDebounceJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MILLIS)
            submitQuery(query = state.value.searchQuery)
        }
    }

    private fun submitQuery(
        query: String,
        force: Boolean = false,
    ) {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isBlank()) {
            onSearchQueryChange(query = query)
            return
        }
        if (!force && trimmedQuery == lastSubmittedQuery) return

        lastSubmittedQuery = trimmedQuery
        requestJob?.cancel()
        requestJob = viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    submittedQuery = trimmedQuery,
                )
            }

            when (
                val result = searchCharactersUseCase(
                    query = trimmedQuery,
                    page = FIRST_PAGE,
                    pageSize = state.value.pageSize,
                )
            ) {
                is Result.Success -> {
                    val filmResults = result.data.characters
                        .filter { it.hasFilmAppearances }
                        .map { it.toFilmCharacterUi() }

                    _state.update {
                        it.copy(
                            results = filmResults,
                            isLoading = false,
                            error = null,
                        )
                    }
                }
                is Result.Failure -> {
                    _state.update {
                        it.copy(
                            results = emptyList(),
                            isLoading = false,
                            error = result.error.toUiText(),
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val FIRST_PAGE = 1
        private const val SEARCH_DEBOUNCE_MILLIS = 300L
    }
}
