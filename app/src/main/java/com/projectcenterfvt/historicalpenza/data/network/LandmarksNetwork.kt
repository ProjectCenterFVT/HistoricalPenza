package com.projectcenterfvt.historicalpenza.data.network

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.projectcenterfvt.historicalpenza.BuildConfig
import com.projectcenterfvt.historicalpenza.data.Preferences
import com.projectcenterfvt.historicalpenza.data.Landmark
import com.projectcenterfvt.historicalpenza.utils.Singleton
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.post
import kotlinx.io.IOException
import kotlinx.serialization.json.json

class LandmarksNetwork private constructor(context: Context) {

    private val client = HttpClient(Android) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
    }

    private var preferences = Preferences.getInstance(context)

    private val mapper = LandMarkMapper()

    suspend fun fetchLandmarks() : List<Landmark> {
        val str: String = client.post(URL_ENDPOINT) {
            body = json {
                "type" to "getCoordinates"
                "ver" to "0.0.0"
                "enc_id" to preferences.token
            }.toString()
        }

        val type = object : TypeToken<LandmarksResponse>() {}.type
        val response : LandmarksResponse = Gson().fromJson(str,type)

        return response.result.map {
            mapper.mapToDomain(it)
        }
    }

    suspend fun openLandmark(id: Long) {
        val str: String = client.post(URL_ENDPOINT) {
            body = json {
                "type" to "setPlaces"
                "id" to id
                "enc_id" to preferences.token
            }.toString()
        }

        val type = object : TypeToken<Boolean>() {}.type
        val response : Boolean = Gson().fromJson(str,type)

        if (!response) throw HttpException(500, "Internal Server Error")
    }

    companion object : Singleton<LandmarksNetwork, Context>(::LandmarksNetwork) {
        const val URL_ENDPOINT = "${BuildConfig.API_ENDPOINT}api.request.php"
    }

}

class HttpException(val code: Int, message: String) : IOException(message)