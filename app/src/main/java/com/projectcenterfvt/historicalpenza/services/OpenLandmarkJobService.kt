package com.projectcenterfvt.historicalpenza.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.projectcenterfvt.historicalpenza.R
import com.projectcenterfvt.historicalpenza.data.LandmarksRepository
import com.projectcenterfvt.historicalpenza.data.Preferences
import com.projectcenterfvt.historicalpenza.info.InfoActivity
import com.projectcenterfvt.historicalpenza.map.MapActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber


class OpenLandmarkJobService : JobService() {

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private val repository: LandmarksRepository by lazy {
        LandmarksRepository.getInstance(this)
    }

    private val preferences: Preferences by lazy {
        Preferences.getInstance(this)
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        val id = params?.jobId?.toLong() ?: return false

        uiScope.launch {
            try {
                repository.openLandmark(id)
                Timber.d("Landmark with id=$id successfully opened")

//                if (preferences.shouldNotify) {
//                    notify(id)
//                }

                jobFinished(params, false)
            } catch (e: Exception) {
                Timber.d(e, "Error occurred")
                jobFinished(params, true)
            }
        }

        return true
    }

    private fun notify(id: Long) {
        val landmark = repository.getLandmarkById(id) ?: return

        val contentIntent = InfoActivity.getIntent(this, landmark)
        val stackBuilder = TaskStackBuilder.create(this).apply {
            addParentStack(MapActivity::class.java)
            addNextIntent(contentIntent)
        }
        val contentPendingIntent = stackBuilder
                .getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                    "historicalPenza",
                    "historicalPenza",
                    NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, "historicalPenza")
                .setSmallIcon(R.drawable.ic_marker_opened_landmark)
                .setContentTitle("Историческая Пенза")
                .setContentText(landmark.title)
                .setContentIntent(contentPendingIntent)

        NotificationManagerCompat.from(this)
                .notify(id.toInt(), notificationBuilder.build())
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        job.cancel()
        return true
    }

}