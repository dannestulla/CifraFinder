package br.gohan.cifrafinder.presenter

sealed class NavigationActions {
    object GetCurrentlyPlaying : NavigationActions()
    object WebView : NavigationActions()
    object LogInSpotify : NavigationActions()
    object LogOffSpotify : NavigationActions()
    object ToastMessage : NavigationActions()
}