package br.gohan.cifrafinder.presenter.model

import java.io.Serializable

const val SCREEN_STATE = "screenState"

data class WhatIsPlayingState(
    val songName: String? = null,
    val searchUrl: String? = null,
    val loading: Boolean = false,
    val musicDurationInMiliSec: Int? = null,
    val artCover: String? = null
) : Serializable