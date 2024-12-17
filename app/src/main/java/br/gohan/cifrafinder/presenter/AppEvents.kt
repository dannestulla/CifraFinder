package br.gohan.cifrafinder.presenter

sealed class AppEvents {
    data object Initial : AppEvents()
    data object LogOff : AppEvents()
    data object MusicFetch : AppEvents()
    data object Settings : AppEvents()
    data object SpotifyLogin : AppEvents()
    data object BackScreen : AppEvents()
    data class ShowSnackbar(val message: String?) : AppEvents()
}
