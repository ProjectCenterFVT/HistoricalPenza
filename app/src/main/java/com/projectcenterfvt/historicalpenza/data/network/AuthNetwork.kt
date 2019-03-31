package com.projectcenterfvt.historicalpenza.data.network

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.projectcenterfvt.historicalpenza.BuildConfig
import com.projectcenterfvt.historicalpenza.data.Preferences
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
import kotlinx.serialization.json.json
import timber.log.Timber

class AuthNetwork private constructor(context: Context) {

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

    @Throws(Exception::class)
    suspend fun logIn(tokenId: String) {
        val str: String = client.post(URL_ENDPOINT) {
            body = json {
                "type" to "login"
                "token" to tokenId
            }.toString()
        }

        try {
            val type = object : TypeToken<LogInResponse>() {}.type
            val response : LogInResponse = Gson().fromJson(str,type)

            preferences.token = response.result[0].enc_id
        } catch (e: JsonSyntaxException) {
            Timber.e(e)
            throw Exception("Error occurred", e)
        }
    }

    companion object : Singleton<AuthNetwork, Context>(::AuthNetwork) {
        const val URL_ENDPOINT = "${BuildConfig.API_ENDPOINT}api.request.php"
    }

}