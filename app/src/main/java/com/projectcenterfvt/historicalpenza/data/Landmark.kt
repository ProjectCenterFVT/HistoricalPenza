package com.projectcenterfvt.historicalpenza.data

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize
import java.net.URL

@Parcelize
data class Landmark(
        val id: Long,
        val title: String,
        val description: String,
        val photoUrl: URL,
        val position: LatLng,
        val type: Type,
        val isOpened: Boolean
) : Parcelable {

    enum class Type { USUAL, EXTRA }

}