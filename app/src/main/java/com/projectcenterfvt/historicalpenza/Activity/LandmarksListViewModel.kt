package com.projectcenterfvt.historicalpenza.Activity

import android.arch.lifecycle.ViewModel
import com.projectcenterfvt.historicalpenza.data.LandmarksRepository

class LandmarksListViewModel(repository: LandmarksRepository) : ViewModel() {

    val landmarks = repository.landmarks

}