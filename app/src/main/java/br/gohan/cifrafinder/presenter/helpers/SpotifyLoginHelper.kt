package br.gohan.cifrafinder.presenter.helpers

import android.content.Intent
import br.gohan.cifrafinder.BuildConfig
import br.gohan.cifrafinder.Constants
import br.gohan.cifrafinder.presenter.MainActivity
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

class SpotifyLoginHelper(
    private val mainActivity: MainActivity?
) {

    fun logIn() {
        val request = AuthorizationRequest.Builder(
            BuildConfig.SPOTIFY_CLIENT_ID,
            AuthorizationResponse.Type.TOKEN,
            Constants.spotifyRedirectURI
        ).setScopes(
            arrayOf(USER_CURRENTLY_READ_PLAYING)
        ).build()
        AuthorizationClient.openLoginActivity(mainActivity, Constants.REQUEST_CODE, request)
    }

    fun handleLoginResponse(
        requestCode: Int,
        resultCode: Int,
        intent: Intent?,
        accessToken: (String?) -> Unit
    ) {
        if (requestCode == Constants.REQUEST_CODE) {
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

    fun logOff() {
        AuthorizationClient.stopLoginActivity(mainActivity, Constants.REQUEST_CODE)
        //AuthorizationClient.clearCookies(cifraActivity)
    }

    companion object {
        private val USER_CURRENTLY_READ_PLAYING = "user-read-currently-playing"
    }
}
