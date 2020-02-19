package com.projectcenterfvt.historicalpenza.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.projectcenterfvt.historicalpenza.data.CurrentLocationListener
import com.projectcenterfvt.historicalpenza.data.Landmark
import com.projectcenterfvt.historicalpenza.data.LandmarksRepository
import com.projectcenterfvt.historicalpenza.utils.distanceTo
import com.projectcenterfvt.historicalpenza.utils.getLatLng
import java.util.regex.Pattern

class MapViewModel(application: Application, private val repository: LandmarksRepository)
    : AndroidViewModel(application) {

    val landmarks = repository.landmarks

    val currentLocation = CurrentLocationListener.getInstance(application)

    private val _searchQuery = MutableLiveData<String>()

    val suggestions = Transformations.map(_searchQuery) { query ->
        val pattern = Pattern.compile(query.toLowerCase())

        val mapper = SuggestionMapper()
        val result = mutableListOf<Suggestion>()
        landmarks.value?.forEach { landmark ->
            if (result.size >= MAX_SUGGESTIONS) return@forEach
            val matcher = pattern.matcher(landmark.title.toLowerCase())
            if (matcher.find()) {
                result.add(mapper.mapFromDomain(landmark))
            }
        }

        result
    }!!

    fun setSearchQuery(text: String) {
        _searchQuery.value = text
    }

    fun getLandmark(id: Long): Landmark? {
        return repository.getLandmarkById(id)
    }

    fun getClosestLandmark(): Landmark? {
        val current = currentLocation.value ?: return null
        val list = landmarks.value ?: return null

        val location = current.getLatLng()
        val distances = hashMapOf<Long, Long>()
        list.map { landmark ->
            distances[landmark.id] = location.distanceTo(landmark.position)
        }
        var closestDistance = Long.MAX_VALUE
        var closestLandmarkId = 0L
        for ((id, distance) in distances) {
            if (distance < closestDistance) {
                closestDistance = distance
                closestLandmarkId = id
            }
        }

        var result: Landmark? = null
        list.map { landmark ->
            if (closestLandmarkId == landmark.id) result = landmark
        }

        return result
    }

    companion object {

        private const val MAX_SUGGESTIONS = 6

    }

}