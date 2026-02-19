package br.gohan.cifrafinder.data.model

import androidx.annotation.Keep

@Keep
data class GoogleJson(
    val items: List<VItems>? = null,
)

@Keep
data class VItems(
    val link: String,
)

// Serper.dev request model
@Keep
data class SerperRequest(
    val q: String
)

// Serper.dev response model
@Keep
data class SerpApiResponse(
    val organic: List<SerpApiResult>? = null,
)

@Keep
data class SerpApiResult(
    val link: String,
)