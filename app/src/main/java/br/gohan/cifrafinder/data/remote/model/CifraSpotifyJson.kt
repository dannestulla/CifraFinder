package br.gohan.cifrafinder.data.remote.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class SpotifyJson(
    val item: Item,
    @SerializedName("progress_ms")
    val progressMs: Long
)

@Keep
data class Item(
    val artists: List<ArtistX>,
    val name: String,
    @SerializedName("duration_ms")
    val durationMs: Long
)

@Keep
data class ArtistX(
    val name: String,
)