package br.gohan.cifrafinder.data.remote.model

import androidx.annotation.Keep

@Keep
data class GoogleJson(
    val items: List<VItems>,
)

@Keep
data class VItems(
    val link: String,
)