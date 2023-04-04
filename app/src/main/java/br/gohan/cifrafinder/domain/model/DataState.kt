package br.gohan.cifrafinder.domain.model

import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope

data class DataState(
    val spotifyToken: String = "",
    val songData: SongData? = null,
    val snackBarScope: CoroutineScope? = null,
    val snackBarHost: SnackbarHostState? = null
)
