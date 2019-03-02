package com.projectcenterfvt.historicalpenza.signIn

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.projectcenterfvt.historicalpenza.data.Preferences
import com.projectcenterfvt.historicalpenza.R
import com.projectcenterfvt.historicalpenza.data.network.AuthNetwork
import com.projectcenterfvt.historicalpenza.greeting.GreetingActivity
import com.projectcenterfvt.historicalpenza.map.MapActivity
import com.projectcenterfvt.historicalpenza.services.FetchLandmarksService
import com.projectcenterfvt.historicalpenza.utils.toast
import com.projectcenterfvt.historicalpenza.utils.showSnackbar
import com.projectcenterfvt.historicalpenza.utils.viewModelFactory
import kotlinx.android.synthetic.main.activity_sign_in.*
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

        viewModel.loggedIn.observe(this, Observer {
            val serviceIntent = Intent(this, FetchLandmarksService::class.java)
            startService(serviceIntent)

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

    }

}

