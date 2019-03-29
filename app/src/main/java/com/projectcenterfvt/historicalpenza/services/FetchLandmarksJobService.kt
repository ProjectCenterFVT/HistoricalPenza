package com.projectcenterfvt.historicalpenza.services

import android.app.job.JobParameters
import android.app.job.JobService
import com.projectcenterfvt.historicalpenza.data.LandmarksRepository
import com.projectcenterfvt.historicalpenza.data.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class FetchLandmarksJobService : JobService() {

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private val repository: LandmarksRepository by lazy {
        LandmarksRepository.getInstance(this)
    }

    private val preferences: Preferences by lazy {
        Preferences.getInstance(this)
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        uiScope.launch {
            try {
                repository.refreshLandmarks()
                preferences.lastUpdate = System.currentTimeMillis()
                Timber.d("Landmarks successfully updated")
                jobFinished(params, false)
            } catch (e: Exception) {
                Timber.d(e, "Error occurred")
                jobFinished(params, true)
            }
        }

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        job.cancel()
        return true
    }

    companion object {

        const val FETCH_LANDMARKS_JOB_ID = -100
        const val UPDATE_PERIOD = 20 * 60 * 60 * 1000 // 20 часов

    }

}