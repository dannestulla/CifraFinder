package br.gohan.cifrafinder.presenter.helpers

import android.content.Intent
import android.util.Log
import br.gohan.cifrafinder.BuildConfig
import br.gohan.cifrafinder.Constants
import br.gohan.cifrafinder.presenter.MainActivity
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

sealed class SpotifyLoginResult {
    data class Success(val token: String) : SpotifyLoginResult()
    data class Error(
        val error: String,
        val type: AuthorizationResponse.Type,
        val resultCode: Int,
        val rawExtras: String
    ) : SpotifyLoginResult()
}

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
        onResult: (SpotifyLoginResult) -> Unit
    ) {
        if (requestCode == Constants.REQUEST_CODE) {
            // Debug: Log raw intent data
            val rawExtras = intent?.extras?.keySet()?.joinToString { key ->
                "$key=${intent.extras?.get(key)}"
            } ?: "no extras"
            Log.d(TAG, "Spotify auth - resultCode: $resultCode, extras: $rawExtras")

            val response = AuthorizationClient.getResponse(resultCode, intent)
            Log.d(TAG, "Spotify response - type: ${response.type}, error: ${response.error}, code: ${response.code}, state: ${response.state}")

            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    Log.d(TAG, "Spotify auth SUCCESS - token received")
                    onResult(SpotifyLoginResult.Success(response.accessToken))
                }

                else -> {
                    val errorMessage = response.error ?: "Unknown Spotify login error"
                    Log.e(TAG, "Spotify auth FAILED - error: $errorMessage, type: ${response.type}")
                    onResult(SpotifyLoginResult.Error(errorMessage, response.type, resultCode, rawExtras))
                }
            }
        }
    }

    fun logOff() {
        AuthorizationClient.stopLoginActivity(mainActivity, Constants.REQUEST_CODE)
    }

    companion object {
        private const val TAG = "SpotifyAuth"
        private const val USER_CURRENTLY_READ_PLAYING = "user-read-currently-playing"
    }
}
