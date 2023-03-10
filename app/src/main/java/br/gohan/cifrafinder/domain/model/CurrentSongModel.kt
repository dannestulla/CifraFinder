package br.gohan.cifrafinder.domain.model

import com.google.gson.annotations.SerializedName

data class CurrentSongModel(
    val songAndArtist: String,
    @SerializedName("duration_ms")
    val durationMs: Long,
    @SerializedName("progress_ms")
    val progressMs: Long
)