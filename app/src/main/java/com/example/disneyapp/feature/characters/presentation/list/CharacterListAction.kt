package com.example.disneyapp.feature.characters.presentation.list

sealed interface CharacterListAction {
    data class OnSearchQueryChange(val query: String) : CharacterListAction
    data object OnRetryClick : CharacterListAction
    data object OnLoadMore : CharacterListAction
}
