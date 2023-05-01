package br.gohan.cifrafinder.presenter.model

import java.io.Serializable

const val SCREEN_STATE = "screenState"
data class ScreenState(
    val songName: String = "",
    val searchUrl: String = "",
    val loading: Boolean = false
) : Serializable