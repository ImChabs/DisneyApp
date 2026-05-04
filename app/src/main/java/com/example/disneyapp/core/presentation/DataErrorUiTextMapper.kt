package com.example.disneyapp.core.presentation

import com.example.disneyapp.core.domain.DataError

fun DataError.toUiText(): UiText =
    when (this) {
        DataError.Network.NO_INTERNET -> UiText.DynamicString("No internet connection.")
        DataError.Network.NOT_FOUND -> UiText.DynamicString("The requested Disney character was not found.")
        DataError.Network.SERIALIZATION -> UiText.DynamicString("The Disney API response could not be read.")
        DataError.Network.REQUEST_TIMEOUT -> UiText.DynamicString("The request timed out.")
        DataError.Network.SERVER_ERROR,
        DataError.Network.SERVICE_UNAVAILABLE,
        -> UiText.DynamicString("The Disney API is unavailable right now.")
        DataError.Local.DISK_FULL -> UiText.DynamicString("There is not enough storage space.")
        DataError.Local.NOT_FOUND -> UiText.DynamicString("The saved character was not found.")
        else -> UiText.DynamicString("Something went wrong.")
    }
