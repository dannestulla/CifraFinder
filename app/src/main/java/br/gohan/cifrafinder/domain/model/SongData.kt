package br.gohan.cifrafinder.domain.model

import java.io.Serializable

data class SongData(
    val name: String,
    val duration: Long?,
    val progress: Long,
    val songImage: String?
) : Serializable
