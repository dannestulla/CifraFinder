package br.gohan.cifrafinder.presenter

sealed class Events {
    object FirstScreen : Events()
    object SecondScreen : Events()
    object ThirdScreen : Events()
    object WebScreen : Events()
    object LogOff : Events()
    object MusicFetch : Events()
    object Settings : Events()
    object SpotifyLogin : Events()
    data class ShowSnackbar(val id: Int, val extension: String? = null) : Events()
}
