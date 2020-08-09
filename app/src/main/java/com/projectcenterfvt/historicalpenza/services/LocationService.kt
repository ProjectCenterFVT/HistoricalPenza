package com.projectcenterfvt.historicalpenza.services

import android.app.Service
import android.app.job.JobInfo
import android.app.job.JobScheduler
import androidx.lifecycle.Observer
import androidx.lifecycle.ProcessLifecycleOwner
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.projectcenterfvt.historicalpenza.data.CurrentLocationListener
import com.projectcenterfvt.historicalpenza.data.Landmark
import com.projectcenterfvt.historicalpenza.data.LandmarksRepository
import com.projectcenterfvt.historicalpenza.utils.distanceTo
import com.projectcenterfvt.historicalpenza.utils.getLatLng
import timber.log.Timber

class LocationService : Service() {

    private val lifecycleOwner = ProcessLifecycleOwner.get()

    private val repository: LandmarksRepository by lazy {
        LandmarksRepository.getInstance(this)
    }

    private val currentLocation: CurrentLocationListener by lazy {
        CurrentLocationListener.getInstance(this)
    }

    override fun onCreate() {
        super.onCreate()

        currentLocation.observe(lifecycleOwner, Observer { location ->
            location ?: return@Observer

            repository.landmarks.value?.forEach { landmark ->
                if ((!landmark.isOpened || landmark.type != Landmark.Type.EXTRA)
                        && location.getLatLng().distanceTo(landmark.position) < landmark.range) {

                    startOpeningLandmark(landmark.id)
                }
            }
        })
    }

    private fun startOpeningLandmark(id: Long) {
        val serviceName = ComponentName(this, OpenLandmarkJobService::class.java)
        val jobInfo = JobInfo.Builder(id.toInt(), serviceName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build()

        val scheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val result = scheduler.schedule(jobInfo)
        if (result == JobScheduler.RESULT_SUCCESS) {
            Timber.d("Job with id=$id scheduled successfully!")
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}
