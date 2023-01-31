package br.gohan.cifrafinder.domain.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class CurrentSongModel(
    val searchString: String?,
    @SerializedName("duration_ms")
    val durationMs: Long,
    @SerializedName("progress_ms")
    val progressMs: Long
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readLong(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(searchString)
        parcel.writeLong(durationMs)
        parcel.writeLong(progressMs)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CurrentSongModel> {
        override fun createFromParcel(parcel: Parcel): CurrentSongModel {
            return CurrentSongModel(parcel)
        }

        override fun newArray(size: Int): Array<CurrentSongModel?> {
            return arrayOfNulls(size)
        }
    }
}
