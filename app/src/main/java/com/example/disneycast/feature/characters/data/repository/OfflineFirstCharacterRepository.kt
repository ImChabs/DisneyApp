package com.example.disneycast.feature.characters.data.repository

import com.example.disneycast.core.domain.DataError
import com.example.disneycast.core.domain.Result
import com.example.disneycast.feature.characters.data.remote.CharacterRemoteDataSource
import com.example.disneycast.feature.characters.domain.CharacterLocalDataSource
import com.example.disneycast.feature.characters.domain.model.CharacterDetail
import com.example.disneycast.feature.characters.domain.model.CharacterPage
import com.example.disneycast.feature.characters.domain.repository.CharacterRepository

class OfflineFirstCharacterRepository(
    private val remoteDataSource: CharacterRemoteDataSource,
    private val localDataSource: CharacterLocalDataSource,
) : CharacterRepository {
    override suspend fun getCharacters(
        page: Int,
        pageSize: Int,
    ): Result<CharacterPage, DataError> =
        when (val remoteResult = remoteDataSource.getCharacters(page = page, pageSize = pageSize)) {
            is Result.Success -> {
                localDataSource.upsertCharacters(remoteResult.data)
                Result.Success(remoteResult.data.copy(isFromCache = false))
            }
            is Result.Failure -> {
                when (val localResult = localDataSource.getCharacters(page = page, pageSize = pageSize)) {
                    is Result.Success -> Result.Success(localResult.data.copy(isFromCache = true))
                    is Result.Failure -> Result.Failure(remoteResult.error)
                }
            }
        }

    override suspend fun getCharacter(id: Int): Result<CharacterDetail, DataError> =
        when (val remoteResult = remoteDataSource.getCharacter(id)) {
            is Result.Success -> {
                localDataSource.upsertCharacter(remoteResult.data)
                Result.Success(CharacterDetail(character = remoteResult.data))
            }
            is Result.Failure -> {
                when (val localResult = localDataSource.getCharacter(id)) {
                    is Result.Success -> Result.Success(
                        CharacterDetail(
                            character = localResult.data,
                            isFromCache = true,
                        )
                    )
                    is Result.Failure -> Result.Failure(remoteResult.error)
                }
            }
        }

    override suspend fun searchCharacters(
        name: String,
        page: Int,
        pageSize: Int,
    ): Result<CharacterPage, DataError> =
        remoteDataSource.searchCharacters(name = name, page = page, pageSize = pageSize)
}
