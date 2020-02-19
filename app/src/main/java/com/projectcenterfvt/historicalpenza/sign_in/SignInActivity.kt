package com.projectcenterfvt.historicalpenza.sign_in

import android.Manifest
import android.app.job.JobInfo
import android.app.job.JobScheduler
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.projectcenterfvt.historicalpenza.R
import com.projectcenterfvt.historicalpenza.data.Preferences
import com.projectcenterfvt.historicalpenza.data.network.AuthNetwork
import com.projectcenterfvt.historicalpenza.greeting.GreetingActivity
import com.projectcenterfvt.historicalpenza.map.MapActivity
import com.projectcenterfvt.historicalpenza.services.FetchLandmarksJobService
import com.projectcenterfvt.historicalpenza.utils.showSnackbar
import com.projectcenterfvt.historicalpenza.utils.toast
import com.projectcenterfvt.historicalpenza.utils.viewModelFactory
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton
import timber.log.Timber


class SignInActivity : AppCompatActivity() {

    private val viewModel: SignInViewModel by lazy {
        val auth = AuthNetwork.getInstance(this)
        ViewModelProviders
                .of(this, viewModelFactory { SignInViewModel(applicationContext, auth) })
                .get(SignInViewModel::class.java)
    }

    private val preferences: Preferences by lazy {
        Preferences.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_server_id))
                .build()

        val googleSignInClient = GoogleSignIn.getClient(applicationContext, signInOptions)

        viewModel.toast.observe(this, Observer { text ->
            text?.let { toast(text) }
        })

        viewModel.snackbar.observe(this, Observer { text ->
            text?.let { content.showSnackbar(text) {} }
        })

        checkPermissions()

        viewModel.loggedIn.observe(this, Observer {
            startFetchingLandmarksJob()

            val intent = if (preferences.showGreeting) {
                preferences.showGreeting = false
                Intent(this, GreetingActivity::class.java)
            } else {
                Intent(this, MapActivity::class.java)
            }
            startActivity(intent)
            finish()
        })

        signInButton.setOnClickListener {
            if (viewModel.canLogIn()) {
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
        }
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

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {

                alert(getString(R.string.allow_permission_dialog_text)) {
                    yesButton { requestPermission() }
                    noButton {  }
                }.show()

            } else {
                requestPermission()
            }
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) { }
                return
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RC_SIGN_IN -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            }
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            account?.idToken?.let { idToken ->
                Timber.d(idToken)
                viewModel.tryLogIn(idToken)
            }

        } catch (e: ApiException) {
            Timber.d(e)
        }

    }

    companion object {

        private const val RC_SIGN_IN = 100
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 200

    }

}

