package br.gohan.cifrafinder.presenter

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import br.gohan.cifrafinder.CifraConstants
import br.gohan.cifrafinder.R
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: MusicFetchViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_cifra)
        logInSpotify()
        toastObserver()
        supportActionBar?.hide()
    }

    private fun logInSpotify() {
        val request = AuthorizationRequest.Builder(
            CifraConstants.spotifyClientId,
            AuthorizationResponse.Type.TOKEN,
            CifraConstants.spotifyRedirectURI
        ).setScopes(
            arrayOf("user-read-currently-playing")
        ).build()
        AuthorizationClient.openLoginActivity(this, CifraConstants.REQUEST_CODE, request)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == CifraConstants.REQUEST_CODE) {
            val response = AuthorizationClient.getResponse(resultCode, intent)
            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> viewModel.setSpotifyToken(response.accessToken)
                AuthorizationResponse.Type.ERROR -> Toast.makeText(
                    this,
                    response.error,
                    Toast.LENGTH_SHORT
                )
                    .show()
                else -> {}
            }
        }
    }

    private fun toastObserver() {
        viewModel.toastMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }
    }
}
