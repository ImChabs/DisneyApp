package com.example.disneyapp.core.data.network

import com.example.disneyapp.core.domain.DataError
import com.example.disneyapp.core.domain.Result
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.statement.HttpResponse
import java.io.IOException
import java.net.SocketTimeoutException
import java.nio.channels.UnresolvedAddressException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException

suspend inline fun <reified Response : Any> safeApiCall(
    execute: () -> HttpResponse,
): Result<Response, DataError.Network> =
    try {
        responseToResult(execute())
    } catch (exception: UnresolvedAddressException) {
        Result.Failure(DataError.Network.NO_INTERNET)
    } catch (exception: HttpRequestTimeoutException) {
        Result.Failure(DataError.Network.REQUEST_TIMEOUT)
    } catch (exception: SocketTimeoutException) {
        Result.Failure(DataError.Network.REQUEST_TIMEOUT)
    } catch (exception: SerializationException) {
        Result.Failure(DataError.Network.SERIALIZATION)
    } catch (exception: IOException) {
        Result.Failure(DataError.Network.NO_INTERNET)
    } catch (exception: Exception) {
        if (exception is CancellationException) throw exception
        Result.Failure(DataError.Network.UNKNOWN)
    }

suspend inline fun <reified Response : Any> responseToResult(
    response: HttpResponse,
): Result<Response, DataError.Network> =
    when (response.status.value) {
        in 200..299 -> Result.Success(response.body<Response>())
        400 -> Result.Failure(DataError.Network.BAD_REQUEST)
        401 -> Result.Failure(DataError.Network.UNAUTHORIZED)
        403 -> Result.Failure(DataError.Network.FORBIDDEN)
        404 -> Result.Failure(DataError.Network.NOT_FOUND)
        408 -> Result.Failure(DataError.Network.REQUEST_TIMEOUT)
        409 -> Result.Failure(DataError.Network.CONFLICT)
        413 -> Result.Failure(DataError.Network.PAYLOAD_TOO_LARGE)
        429 -> Result.Failure(DataError.Network.TOO_MANY_REQUESTS)
        503 -> Result.Failure(DataError.Network.SERVICE_UNAVAILABLE)
        in 500..599 -> Result.Failure(DataError.Network.SERVER_ERROR)
        else -> Result.Failure(DataError.Network.UNKNOWN)
    }
