package com.projectcenterfvt.historicalpenza.splash

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.projectcenterfvt.historicalpenza.sign_in.SignInActivity
import com.projectcenterfvt.historicalpenza.data.Preferences
import com.projectcenterfvt.historicalpenza.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.projectcenterfvt.historicalpenza.map.MapActivity
import com.projectcenterfvt.historicalpenza.services.FetchLandmarksJobService
import timber.log.Timber


class SplashActivity : AppCompatActivity() {

    private val preferences by lazy {
        Preferences.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_Launcher)
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (checkPlayServices()) {
            startApp()
        }
    }

    private fun startApp() {
        if (preferences.token.isEmpty()) {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        } else {
            val timeLeft = System.currentTimeMillis() - preferences.lastUpdate
            if (timeLeft > FetchLandmarksJobService.UPDATE_PERIOD) {
                startFetchingLandmarksJob()
            }

            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        finish()
    }

    private fun checkPlayServices(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show()
            } else {
                Timber.i("This device is not supported.")
                finish()
            }
            return false
        }
        return true
    }

    private fun startFetchingLandmarksJob() {
        val serviceName = ComponentName(this, FetchLandmarksJobService::class.java)
        val jobInfo = JobInfo.Builder(FetchLandmarksJobService.FETCH_LANDMARKS_JOB_ID, serviceName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build()

        val scheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val result = scheduler.schedule(jobInfo)
        if (result == JobScheduler.RESULT_SUCCESS) {
            Timber.d("Job scheduled successfully!")
        }
    }

    companion object {

        private const val PLAY_SERVICES_RESOLUTION_REQUEST = 2404

    }
}
