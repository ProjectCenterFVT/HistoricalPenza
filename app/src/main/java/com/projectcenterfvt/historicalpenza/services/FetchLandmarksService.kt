package com.projectcenterfvt.historicalpenza.services

import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import com.projectcenterfvt.historicalpenza.data.Connection
import com.projectcenterfvt.historicalpenza.data.ConnectionListener
import com.projectcenterfvt.historicalpenza.data.LandmarksRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class FetchLandmarksService : LifecycleService() {

    private val repository: LandmarksRepository by lazy {
        LandmarksRepository.getInstance(this)
    }

    private val connectionListener: ConnectionListener by lazy {
        ConnectionListener.getInstance(this)
    }

    override fun onCreate() {
        super.onCreate()

        connectionListener.observe(this, Observer { connection ->
            if (connection != null && connection != Connection.NOT_CONNECTED) {

                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        repository.refreshLandmarks()
                        Timber.d("Landmarks successfully updated")
                    } catch (e: Exception) {
                        Timber.d(e, "Error occurred")
                    }
                    stopSelf()
                }

            }
        })
    }

}