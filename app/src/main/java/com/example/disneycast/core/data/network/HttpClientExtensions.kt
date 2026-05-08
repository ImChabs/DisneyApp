package com.example.disneycast.core.data.network

import com.example.disneycast.core.domain.DataError
import com.example.disneycast.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url

suspend inline fun <reified Response : Any> HttpClient.safeGet(
    route: String,
    queryParameters: Map<String, Any?> = emptyMap(),
): Result<Response, DataError.Network> =
    safeApiCall {
        get {
            url(constructDisneyApiRoute(route))
            queryParameters.forEach { (key, value) ->
                if (value != null) {
                    parameter(key, value)
                }
            }
        }
    }

fun constructDisneyApiRoute(route: String): String =
    when {
        route.startsWith("http://") || route.startsWith("https://") -> route
        route.startsWith("/") -> DISNEY_API_BASE_URL + route
        else -> "$DISNEY_API_BASE_URL/$route"
    }
