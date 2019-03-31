package com.projectcenterfvt.historicalpenza.map

import com.projectcenterfvt.historicalpenza.data.Landmark
import com.projectcenterfvt.historicalpenza.data.Mapper

open class MarkerMapper : Mapper<Landmark, LandmarkMarker> {

    override fun mapFromDomain(type: Landmark): LandmarkMarker {
        with (type) {
            return LandmarkMarker(
                    id = id,
                    name = title,
                    pos = position,
                    type = this.type,
                    isOpened = isOpened
            )
        }
    }

    override fun mapToDomain(type: LandmarkMarker): Landmark {
        throw NotImplementedError()
    }

}