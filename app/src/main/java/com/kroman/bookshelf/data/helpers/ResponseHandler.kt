package com.kroman.bookshelf.data.helpers

import com.kroman.bookshelf.domain.model.Result
import retrofit2.HttpException
import retrofit2.Response

fun <T, R> handleResponse(
    response: Response<T>,
    mapper: (T) -> R
): Result<R> {
    return when {
        response.isSuccessful -> {
            response.body()?.let { body ->
                Result.Success(data = mapper(body))
            } ?: Result.Error(
                exception = NullPointerException("Response body is null"),
            )
        }

        else -> {
            Result.Error(
                exception = HttpException(response),
            )
        }
    }
}