package br.gohan.cifrafinder.domain.model

import androidx.compose.material3.SnackbarHostState
import br.gohan.cifrafinder.presenter.model.SnackBarMessage
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

data class DataState(
    val spotifyToken: String = "",
    val songData: SongData? = null,
    val snackBarScope: CoroutineScope? = null,
    val snackBarHost: SnackbarHostState? = null
)

data class SongData(
    val songName: String,
    @SerializedName("duration_ms")
    val durationMs: Long,
    @SerializedName("progress_ms")
    val progressMs: Long
)
