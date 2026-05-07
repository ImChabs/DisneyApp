package com.example.disneyapp.feature.characters.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.disneyapp.core.domain.Result
import com.example.disneyapp.core.presentation.toUiText
import com.example.disneyapp.feature.characters.domain.usecase.GetCharacterDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CharacterDetailViewModel(
    private val characterId: Int,
    private val getCharacterDetailUseCase: GetCharacterDetailUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(CharacterDetailState())
    val state = _state.asStateFlow()

    init {
        loadCharacter()
    }

    fun onAction(action: CharacterDetailAction) {
        when (action) {
            CharacterDetailAction.OnRetryClick -> loadCharacter()
        }
    }

    private fun loadCharacter() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            when (val result = getCharacterDetailUseCase(characterId)) {
                is Result.Success -> {
                    _state.update {
                        it.copy(
                            character = result.data.toCharacterDetailUi(),
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
}
