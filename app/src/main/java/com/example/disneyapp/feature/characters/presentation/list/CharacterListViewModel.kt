package com.example.disneyapp.feature.characters.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.disneyapp.core.domain.Result
import com.example.disneyapp.core.presentation.UiText
import com.example.disneyapp.core.presentation.toUiText
import com.example.disneyapp.feature.characters.domain.model.DisneyCharacter
import com.example.disneyapp.feature.characters.domain.usecase.GetCharactersUseCase
import com.example.disneyapp.feature.characters.domain.usecase.ObserveFavoriteCharacterIdsUseCase
import com.example.disneyapp.feature.characters.domain.usecase.SearchCharactersUseCase
import com.example.disneyapp.feature.characters.domain.usecase.ToggleFavoriteCharacterUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CharacterListViewModel(
    private val getCharactersUseCase: GetCharactersUseCase,
    private val searchCharactersUseCase: SearchCharactersUseCase,
    private val observeFavoriteCharacterIdsUseCase: ObserveFavoriteCharacterIdsUseCase,
    private val toggleFavoriteCharacterUseCase: ToggleFavoriteCharacterUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(CharacterListState())
    val state = _state.asStateFlow()

    private val _events = Channel<CharacterListEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var searchDebounceJob: Job? = null
    private var requestJob: Job? = null
    private var lastSubmittedQuery: String? = null
    private val loadedCharacters = mutableMapOf<Int, DisneyCharacter>()
    private var favoriteCharacterIds = emptySet<Int>()

    init {
        observeFavoriteCharacterIds()
        submitQuery(query = "", force = true)
    }

    fun onAction(action: CharacterListAction) {
        when (action) {
            is CharacterListAction.OnSearchQueryChange -> onSearchQueryChange(action.query)
            is CharacterListAction.OnFavoriteClick -> toggleFavorite(action.characterId)
            CharacterListAction.OnRetryClick -> retry()
            CharacterListAction.OnLoadMore -> loadMore()
        }
    }

    private fun observeFavoriteCharacterIds() {
        viewModelScope.launch {
            observeFavoriteCharacterIdsUseCase().collect { ids ->
                favoriteCharacterIds = ids
                _state.update { state ->
                    state.copy(
                        characters = state.characters.map { character ->
                            character.copy(isFavorite = character.id in ids)
                        },
                    )
                }
            }
        }
    }

    private fun onSearchQueryChange(query: String) {
        requestJob?.cancel()
        _state.update {
            it.copy(
                searchQuery = query,
                isLoading = false,
                isLoadingMore = false,
                currentPage = 0,
                canLoadMore = false,
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
        if (state.value.characters.isNotEmpty() && state.value.canLoadMore) {
            loadMore()
        } else {
            submitQuery(query = state.value.searchQuery, force = true)
        }
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
            loadPage(query = trimmedQuery, page = FIRST_PAGE, replace = true)
        }
    }

    private fun loadMore() {
        val currentState = state.value
        if (
            currentState.isLoading ||
            currentState.isLoadingMore ||
            !currentState.canLoadMore ||
            currentState.characters.isEmpty()
        ) {
            return
        }

        _state.update { it.copy(isLoadingMore = true, error = null) }
        requestJob = viewModelScope.launch {
            loadPage(
                query = currentState.searchQuery.trim(),
                page = currentState.currentPage + 1,
                replace = false,
            )
        }
    }

    private suspend fun loadPage(
        query: String,
        page: Int,
        replace: Boolean,
    ) {
        _state.update {
            if (replace) {
                it.copy(
                    isLoading = true,
                    isLoadingMore = false,
                    currentPage = 0,
                    canLoadMore = false,
                )
            } else {
                it.copy(isLoadingMore = true, error = null)
            }
        }

        when (
            val result = if (query.isBlank()) {
                getCharactersUseCase(page = page, pageSize = state.value.pageSize)
            } else {
                searchCharactersUseCase(
                    query = query,
                    page = page,
                    pageSize = state.value.pageSize,
                )
            }
        ) {
            is Result.Success -> {
                if (result.data.isFromCache) {
                    _events.send(CharacterListEvent.ShowSnackbar(CACHE_MESSAGE))
                }
                _state.update {
                    result.data.characters.forEach { character ->
                        loadedCharacters[character.id] = character
                    }
                    val nextCharacters = if (replace) {
                        result.data.characters.map { character ->
                            character.toCharacterListItemUi(
                                isFavorite = character.id in favoriteCharacterIds,
                            )
                        }
                    } else {
                        val loadedCharacters = result.data.characters.map { character ->
                            character.toCharacterListItemUi(
                                isFavorite = character.id in favoriteCharacterIds,
                            )
                        }
                        (it.characters + loadedCharacters).distinctBy { character -> character.id }
                    }
                    it.copy(
                        characters = nextCharacters,
                        isLoading = false,
                        isLoadingMore = false,
                        currentPage = result.data.currentPage,
                        canLoadMore = result.data.hasNextPage,
                        error = null,
                    )
                }
            }
            is Result.Failure -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        error = result.error.toUiText(),
                    )
                }
            }
        }
    }

    private fun toggleFavorite(characterId: Int) {
        val character = loadedCharacters[characterId] ?: return
        viewModelScope.launch {
            when (val result = toggleFavoriteCharacterUseCase(character)) {
                is Result.Success -> Unit
                is Result.Failure -> _events.send(
                    CharacterListEvent.ShowSnackbar(result.error.toUiText()),
                )
            }
        }
    }

    companion object {
        private const val FIRST_PAGE = 1
        private const val SEARCH_DEBOUNCE_MILLIS = 300L
        private val CACHE_MESSAGE = UiText.DynamicString("Showing saved characters.")
    }
}
