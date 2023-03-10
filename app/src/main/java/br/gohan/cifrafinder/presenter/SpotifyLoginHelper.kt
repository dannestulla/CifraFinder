package br.gohan.cifrafinder.presenter

import android.content.Intent
import br.gohan.cifrafinder.CifraConstants
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

class SpotifyLoginHelper(
    private val mainActivity: MainActivity?) {
    private val USER_CURRENTLY_READ_PLAYING = "user-read-currently-playing"

    fun logInSpotify() {
        val request = AuthorizationRequest.Builder(
            CifraConstants.spotifyClientId,
            AuthorizationResponse.Type.TOKEN,
            CifraConstants.spotifyRedirectURI
        ).setScopes(
            arrayOf(USER_CURRENTLY_READ_PLAYING)
        ).build()
        AuthorizationClient.openLoginActivity(mainActivity, CifraConstants.REQUEST_CODE, request)
    }

    fun handleLoginResponse(
        requestCode: Int,
        resultCode: Int,
        intent: Intent?,
        accessToken: (String?) -> Unit
    ) {
        if (requestCode == CifraConstants.REQUEST_CODE) {
            val response = AuthorizationClient.getResponse(resultCode, intent)
            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    accessToken.invoke(response.accessToken)
                }
                else -> {
                    accessToken.invoke(null)
                }
            }
        }
    }
}
