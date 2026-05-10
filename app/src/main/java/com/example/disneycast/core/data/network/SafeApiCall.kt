package com.example.disneycast.core.data.network

import android.util.Log
import com.example.disneycast.BuildConfig
import com.example.disneycast.core.domain.DataError
import com.example.disneycast.core.domain.Result
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.JsonConvertException
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
    } catch (exception: JsonConvertException) {
        logSafeApiException(DataError.Network.SERIALIZATION, exception)
        Result.Failure(DataError.Network.SERIALIZATION)
    } catch (exception: SerializationException) {
        logSafeApiException(DataError.Network.SERIALIZATION, exception)
        Result.Failure(DataError.Network.SERIALIZATION)
    } catch (exception: SecurityException) {
        logSafeApiException(DataError.Network.NO_INTERNET, exception)
        Result.Failure(DataError.Network.NO_INTERNET)
    } catch (exception: IOException) {
        Result.Failure(DataError.Network.NO_INTERNET)
    } catch (exception: Exception) {
        if (exception is CancellationException) throw exception
        logSafeApiException(DataError.Network.UNKNOWN, exception)
        Result.Failure(DataError.Network.UNKNOWN)
    }

private const val SAFE_API_LOG_TAG = "DisneyCast"

@PublishedApi
internal fun logSafeApiException(error: DataError.Network, exception: Exception) {
    if (!BuildConfig.DEBUG) return
    val message =
        "safeApiCall mapped ${exception::class.qualifiedName} to $error: " +
            exception.message.orEmpty()
    try {
        Log.d(SAFE_API_LOG_TAG, message)
    } catch (_: RuntimeException) {
        // JVM unit tests use a stub Log that throws unless mocked (see Android Gradle Plugin docs).
        System.err.println("$SAFE_API_LOG_TAG: $message")
    }
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
