package br.gohan.cifrafinder.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CurrentSong(
    val searchString: String,
    @SerializedName("duration_ms")
    val durationMs: Long,
    @SerializedName("progress_ms")
    val progressMs: Long
) : Parcelable
