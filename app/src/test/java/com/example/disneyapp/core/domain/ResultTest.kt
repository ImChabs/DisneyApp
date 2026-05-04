package com.example.disneyapp.core.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class ResultTest {
    @Test
    fun `map transforms success value`() {
        val result = Result.Success(2).map { it * 3 }

        assertThat(result).isEqualTo(Result.Success(6))
    }

    @Test
    fun `map keeps failure error`() {
        val result = Result.Failure(DataError.Network.NO_INTERNET).map { value: Int -> value * 3 }

        assertThat(result).isEqualTo(Result.Failure(DataError.Network.NO_INTERNET))
    }

    @Test
    fun `asEmptyResult converts success data to unit`() {
        val result = Result.Success("saved").asEmptyResult()

        assertThat(result).isEqualTo(Result.Success(Unit))
    }
}
