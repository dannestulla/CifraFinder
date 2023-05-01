package br.gohan.cifrafinder.domain.model

import java.io.Serializable

const val DATA_STATE = "dataState"

data class DataState(
    val spotifyToken: String = "",
    val songData: SongData? = null
) : Serializable
