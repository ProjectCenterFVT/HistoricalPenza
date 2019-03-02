package com.projectcenterfvt.historicalpenza.landmarks_list

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
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

    fun setFiltering() {
        currentFiltering.value =
                if (currentFiltering.value == Filtering.DISTANCE) Filtering.ALPHABETIC
                else Filtering.DISTANCE
    }

}

enum class Filtering { DISTANCE, ALPHABETIC}