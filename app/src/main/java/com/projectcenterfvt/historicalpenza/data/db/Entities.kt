package com.projectcenterfvt.historicalpenza.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Landmarks")
data class LandmarkEntity (
        @PrimaryKey val id: Long,
        val title: String,
        val description: String,
        val photoUrl: String,
        val latitude: Double,
        val longitude: Double,
        val range: Long,
        val type: Int,
        val isOpened: Boolean
)