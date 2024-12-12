package br.gohan.cifrafinder.domain.usecase

import retrofit2.Response

interface FetchService<in Params, out O> {
    suspend fun invoke(params: Params): Result<O>
    fun handleResponse(response: Response<*>): O
    fun <T> createModel(apiModel: T): O
}

typealias Params = String
