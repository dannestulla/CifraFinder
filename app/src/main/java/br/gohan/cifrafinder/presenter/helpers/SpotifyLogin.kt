package br.gohan.cifrafinder.presenter.helpers

import android.content.Intent
import br.gohan.cifrafinder.CifraConstants
import br.gohan.cifrafinder.presenter.CifraActivity
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

class SpotifyLogin(
    private val cifraActivity: CifraActivity?
) {
    private val USER_CURRENTLY_READ_PLAYING = "user-read-currently-playing"

    fun logIn() {
        val request = AuthorizationRequest.Builder(
            CifraConstants.spotifyClientId,
            AuthorizationResponse.Type.TOKEN,
            CifraConstants.spotifyRedirectURI
        ).setScopes(
            arrayOf(USER_CURRENTLY_READ_PLAYING)
        ).build()
        AuthorizationClient.openLoginActivity(cifraActivity, CifraConstants.REQUEST_CODE, request)
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

    fun logOff() {
        AuthorizationClient.stopLoginActivity(cifraActivity, CifraConstants.REQUEST_CODE)
        AuthorizationClient.clearCookies(cifraActivity)
    }
}
