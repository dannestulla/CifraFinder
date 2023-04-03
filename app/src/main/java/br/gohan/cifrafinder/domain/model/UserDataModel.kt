package br.gohan.cifrafinder.domain.model

import com.google.gson.annotations.SerializedName

data class ScreenState(
    val songName: String = "",
    val searchUrl: String = ""
)

data class DataState(
    val spotifyToken: String = "",
    val songData: SongData? = null,
)

data class SongData(
    val songName: String,
    @SerializedName("duration_ms")
    val durationMs: Long,
    @SerializedName("progress_ms")
    val progressMs: Long
)
