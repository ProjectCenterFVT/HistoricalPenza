package com.projectcenterfvt.historicalpenza.data.network

import com.google.android.gms.maps.model.LatLng
import com.projectcenterfvt.historicalpenza.BuildConfig
import com.projectcenterfvt.historicalpenza.data.Landmark
import com.projectcenterfvt.historicalpenza.data.Mapper
import java.net.URL

open class LandMarkMapper : Mapper<Landmark, LandmarkEntity> {

    override fun mapFromDomain(type: Landmark): LandmarkEntity {
        throw NotImplementedError()
    }

    override fun mapToDomain(type: LandmarkEntity): Landmark {
        val x = type.coordinates.split(",")[0].toDouble()
        val y = type.coordinates.split(",")[1].toDouble()
        val latLng = LatLng(x, y)
        val landmarkType = when(type.type) {
            1 -> Landmark.Type.EXTRA
            else -> Landmark.Type.USUAL
        }

        return Landmark(
                type._id,
                type.title,
                type.description,
                URL("${BuildConfig.API_ENDPOINT}img/${type.img}"),
                latLng,
                landmarkType,
                type.flag
        )
    }


}