package com.projectcenterfvt.historicalpenza.data.db

import com.google.android.gms.maps.model.LatLng
import com.projectcenterfvt.historicalpenza.data.Landmark
import com.projectcenterfvt.historicalpenza.data.Mapper
import java.net.URL

open class LandMarkMapper : Mapper<Landmark, LandmarkEntity> {

    override fun mapFromDomain(type: Landmark): LandmarkEntity {
        with (type) {
            val landmarkType = when (this.type) {
                Landmark.Type.USUAL -> 0
                Landmark.Type.EXTRA -> 1
            }
            return LandmarkEntity(
                    id = id,
                    title = title,
                    description = description,
                    photoUrl = photoUrl.toString(),
                    latitude = position.latitude,
                    longitude = position.longitude,
                    range = range,
                    type = landmarkType,
                    isOpened = isOpened
            )
        }
    }

    override fun mapToDomain(type: LandmarkEntity): Landmark {
        with (type) {
            val landmarkType = when(type.type) {
                1 -> Landmark.Type.EXTRA
                else -> Landmark.Type.USUAL
            }
            return Landmark(
                    id = id,
                    title = title,
                    description = description,
                    photoUrl = URL(photoUrl),
                    position = LatLng(latitude, longitude),
                    range = range,
                    type = landmarkType,
                    isOpened = isOpened
            )
        }
    }

}