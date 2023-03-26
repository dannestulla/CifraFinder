package br.gohan.cifrafinder.presenter

sealed class NavigationActions {

    // Asks for user to log in Spotify
    object FirstStep : NavigationActions()

    // Shows loading screen and opens Spotify Log in
    object SecondStep : NavigationActions()

    // Asks for user to play a song and shows get playing song button
    object ThirdStep : NavigationActions()

    // Shows what song is being played and shows get tablature button
    object FourthStep : NavigationActions()

    // Open Webview with the tablature
    object LastStep : NavigationActions()
}