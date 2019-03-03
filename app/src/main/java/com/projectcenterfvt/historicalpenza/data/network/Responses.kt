package com.projectcenterfvt.historicalpenza.data.network

data class LandmarksResponse(
        val result: List<LandmarkEntity>
)

data class LandmarkEntity(
        val _id: Long,
        val title: String,
        val description: String,
        val img: String,
        val coordinates: String,
        val type: Int,
        val flag: Int
)

data class LogInResponse(val result: List<Token>)

data class Token(val enc_id: String)