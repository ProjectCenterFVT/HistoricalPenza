package com.projectcenterfvt.historicalpenza.data

import androidx.lifecycle.LiveData
import android.content.Context
import com.projectcenterfvt.historicalpenza.data.db.LandmarksDatabase
import com.projectcenterfvt.historicalpenza.data.network.LandmarksNetwork
import com.projectcenterfvt.historicalpenza.utils.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LandmarksRepository private constructor(context: Context) {

    private val network = LandmarksNetwork.getInstance(context)
    private val db = LandmarksDatabase.getInstance(context)

    val landmarks: LiveData<List<Landmark>> by lazy(LazyThreadSafetyMode.NONE) {
        db.getLandmarks()
    }

    fun getLandmarkById(id: Long): Landmark? {
        return landmarks.value?.find { landmark ->
            landmark.id == id
        }
    }

    @Throws(Exception::class)
    suspend fun refreshLandmarks() {
        withContext(Dispatchers.IO) {
            val result = network.fetchLandmarks()
            db.insertLandmarks(result)
        }
    }

    @Throws(Exception::class)
    suspend fun openLandmark(id: Long) {
        withContext(Dispatchers.IO) {
            network.openLandmark(id)
            db.openLandmark(id)
        }
    }

    suspend fun resetLandmarks() {
        withContext(Dispatchers.IO) {
            db.resetLandmarks()
        }
    }

    companion object : Singleton<LandmarksRepository, Context>(::LandmarksRepository)
}