package com.example.disneyapp.feature.characters.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.disneyapp.core.domain.Result
import com.example.disneyapp.core.presentation.UiText
import com.example.disneyapp.core.presentation.toUiText
import com.example.disneyapp.feature.characters.domain.model.DisneyCharacter
import com.example.disneyapp.feature.characters.domain.usecase.GetCharacterDetailUseCase
import com.example.disneyapp.feature.characters.domain.usecase.ObserveIsFavoriteCharacterUseCase
import com.example.disneyapp.feature.characters.domain.usecase.ToggleFavoriteCharacterUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CharacterDetailViewModel(
    private val characterId: Int,
    private val getCharacterDetailUseCase: GetCharacterDetailUseCase,
    private val observeIsFavoriteCharacterUseCase: ObserveIsFavoriteCharacterUseCase,
    private val toggleFavoriteCharacterUseCase: ToggleFavoriteCharacterUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(CharacterDetailState())
    val state = _state.asStateFlow()

    private val _events = Channel<CharacterDetailEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var character: DisneyCharacter? = null
    private var isFavorite = false

    init {
        observeFavoriteState()
        loadCharacter()
    }

    fun onAction(action: CharacterDetailAction) {
        when (action) {
            CharacterDetailAction.OnRetryClick -> loadCharacter()
            CharacterDetailAction.OnFavoriteClick -> toggleFavorite()
        }
    }

    private fun observeFavoriteState() {
        viewModelScope.launch {
            observeIsFavoriteCharacterUseCase(characterId).collect { favorite ->
                isFavorite = favorite
                _state.update { state ->
                    state.copy(
                        character = state.character?.copy(isFavorite = favorite),
                    )
                }
            }
        }
    }

    private fun loadCharacter() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            when (val result = getCharacterDetailUseCase(characterId)) {
                is Result.Success -> {
                    val detail = result.data
                    character = detail.character
                    if (detail.isFromCache) {
                        _events.send(CharacterDetailEvent.ShowSnackbar(CACHE_MESSAGE))
                    }
                    _state.update {
                        it.copy(
                            character = detail.character.toCharacterDetailUi(isFavorite = isFavorite),
                            isLoading = false,
                            error = null,
                        )
                    }
                }
                is Result.Failure -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.error.toUiText(),
                        )
                    }
                }
            }
        }
    }

    private fun toggleFavorite() {
        val currentCharacter = character ?: return
        viewModelScope.launch {
            when (val result = toggleFavoriteCharacterUseCase(currentCharacter)) {
                is Result.Success -> Unit
                is Result.Failure -> _events.send(
                    CharacterDetailEvent.ShowSnackbar(result.error.toUiText()),
                )
            }
        }
    }

    companion object {
        private val CACHE_MESSAGE = UiText.DynamicString("Showing saved character.")
    }
}
