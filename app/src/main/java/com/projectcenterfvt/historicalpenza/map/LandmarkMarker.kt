package com.projectcenterfvt.historicalpenza.map

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.projectcenterfvt.historicalpenza.data.Landmark

data class LandmarkMarker(
        val id: Long,
        val name: String,
        val pos: LatLng,
        val type: Landmark.Type,
        val isOpened: Boolean
) : ClusterItem {

    override fun getSnippet(): String {
        return name
    }

    override fun getTitle(): String {
        return name
    }

    override fun getPosition(): LatLng {
        return pos
    }
}