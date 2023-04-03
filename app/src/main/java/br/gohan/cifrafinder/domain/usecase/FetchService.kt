package br.gohan.cifrafinder.domain.usecase

import retrofit2.Response

interface  FetchService<in I, out O> {
    suspend fun invoke(params: I) : O
    fun handleResponse(response: Response<*>) : O
    fun <T> createModel(apiModel: T): O
}