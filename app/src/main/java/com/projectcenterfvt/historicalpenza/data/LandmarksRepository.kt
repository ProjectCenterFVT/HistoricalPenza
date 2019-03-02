package com.projectcenterfvt.historicalpenza.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
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
        landmarks.value?.map { landmark ->
            if (landmark.id == id) return landmark
        }
        return null
    }

    @Throws(Exception::class)
    suspend fun refreshLandmarks() {
        withContext(Dispatchers.IO) {
            val result = network.fetchLandmarks()
            db.insertLandmarks(result)
        }
    }

    companion object : Singleton<LandmarksRepository, Context>(::LandmarksRepository)
}