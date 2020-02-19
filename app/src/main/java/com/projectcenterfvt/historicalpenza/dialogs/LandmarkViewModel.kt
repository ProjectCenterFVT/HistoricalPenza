package com.projectcenterfvt.historicalpenza.dialogs

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.projectcenterfvt.historicalpenza.data.LandmarksRepository

class LandmarkViewModel(openedLandmarkId: Long, repository: LandmarksRepository)
    : ViewModel() {

    val landmark = Transformations.map(repository.landmarks) { landmarks ->
        landmarks.find { landmark ->
            landmark.id == openedLandmarkId
        }
    }!!

}