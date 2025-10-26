package com.proyecto.uniandes.vynils.data.network

import retrofit2.Response

suspend inline fun <T> safeApiCall(
    crossinline call: suspend () -> Response<T>
): Result<T> = runCatching {
    val response = call()

    if (!response.isSuccessful)
        error("Error en el servidor: ${response.code()} ${response.message()}")

    response.body() ?: error("No se encontro resultados")
}
