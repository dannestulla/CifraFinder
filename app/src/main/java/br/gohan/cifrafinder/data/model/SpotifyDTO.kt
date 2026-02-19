package br.gohan.cifrafinder.data.model

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class SpotifyJson(
    val item: Item?,
    val progress_ms: Long
)

@Keep
data class Item(
    val artists: List<ArtistX>,
    val name: String,
    val duration_ms: Long,
    val album: Album,
    )

@Keep
data class ArtistX(
    val name: String,
)

@Keep
data class Album(
    val images: List<Image>,
    val name: String,
) : Serializable

@Keep
data class Image(
    val url: String,
) : Serializable