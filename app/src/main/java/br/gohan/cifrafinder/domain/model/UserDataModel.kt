package br.gohan.cifrafinder.presenter

data class UserDataState(
    val currentSongName: String = "",
    val searchUrl: String = "",
    val spotifyToken: String = "",
    val navigationActions: NavigationActions = NavigationActions.FirstStep
)
