package com.projectcenterfvt.historicalpenza.landmarksList

import com.projectcenterfvt.historicalpenza.data.Landmark

data class LandmarkAdapterItem(
        val id: Long,
        val title: String,
        val distance: Long,
        val type: Landmark.Type,
        val isOpened: Boolean
) {
    fun getPrettyDistance(): String {
        if (distance == -1L) return "ERROR"

        return if (distance < 1000){
            "$distance м"
        } else {
            val dist = distance / 1000L
            "$dist км"
        }
    }
}