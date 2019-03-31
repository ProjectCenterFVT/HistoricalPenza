package com.projectcenterfvt.historicalpenza.landmarks_list

import com.google.android.gms.maps.model.LatLng
import com.projectcenterfvt.historicalpenza.data.Landmark
import com.projectcenterfvt.historicalpenza.data.Mapper
import com.projectcenterfvt.historicalpenza.utils.distanceTo

open class LandmarkMapper(private val lastKnownPosition: LatLng?) : Mapper<Landmark, LandmarkAdapterItem> {

    override fun mapFromDomain(type: Landmark): LandmarkAdapterItem {
        with (type) {
            val distance = lastKnownPosition?.distanceTo(position) ?: -1
            return LandmarkAdapterItem(
                    id = id,
                    title = title,
                    distance = distance,
                    type = this.type,
                    isOpened = isOpened
            )
        }
    }

    override fun mapToDomain(type: LandmarkAdapterItem): Landmark {
        throw NotImplementedError()
    }

}