package com.projectcenterfvt.historicalpenza.landmarks_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.projectcenterfvt.historicalpenza.data.LandmarksRepository

class LandmarksListViewModel(repository: LandmarksRepository,
                             lastKnownLocation: LatLng?)
    : ViewModel() {

    private val currentFiltering = MutableLiveData<Filtering>()

    private val mapper = LandmarkMapper(lastKnownLocation)
    private val _adapterItems = Transformations.map(repository.landmarks) { landmarks ->
        landmarks.map { landmark ->
            mapper.mapFromDomain(landmark)
        }
    }

    val adapterItems: LiveData<MutableList<LandmarkAdapterItem>>
            = Transformations.switchMap(currentFiltering) { filtering ->
        when (filtering) {
            Filtering.DISTANCE -> {
                Transformations.map(_adapterItems) { landmarks ->
                    val mutableList = landmarks.toMutableList()
                    mutableList.sortBy { it.distance }
                    mutableList
                }
            }
            else -> {
                Transformations.map(_adapterItems) { landmarks ->
                    val mutableList = landmarks.toMutableList()
                    mutableList.sortBy { it.title }
                    mutableList
                }
            }
        }
    }

    init {
        currentFiltering.value =
                if (lastKnownLocation == null) Filtering.ALPHABETIC else Filtering.DISTANCE
    }

    fun setFiltering(filtering: Filtering) {
        if (currentFiltering.value == filtering) return
        currentFiltering.value = filtering
    }

}

enum class Filtering { DISTANCE, ALPHABETIC}