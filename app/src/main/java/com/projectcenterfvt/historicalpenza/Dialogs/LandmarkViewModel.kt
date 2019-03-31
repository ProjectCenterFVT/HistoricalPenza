package com.projectcenterfvt.historicalpenza.dialogs

import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.projectcenterfvt.historicalpenza.data.LandmarksRepository

class LandmarkViewModel(openedLandmarkId: Long, repository: LandmarksRepository)
    : ViewModel() {

    val landmark = Transformations.map(repository.landmarks) { landmarks ->
        landmarks.find { landmark ->
            landmark.id == openedLandmarkId
        }
    }!!

}