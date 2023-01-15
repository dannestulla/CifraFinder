package br.gohan.cifrafinder.data.remote.model

import androidx.annotation.Keep

@Keep
data class SpotifyJson(
    val item: Item,
)
@Keep
data class Item(
    val artists: List<ArtistX>,
    val name: String,
)
@Keep
data class ArtistX(
    val name: String,
)