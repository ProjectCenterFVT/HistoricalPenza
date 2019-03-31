package com.projectcenterfvt.historicalpenza.dialogs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

import com.projectcenterfvt.historicalpenza.sign_in.SignInActivity
import com.projectcenterfvt.historicalpenza.data.Preferences
import com.projectcenterfvt.historicalpenza.R
import com.projectcenterfvt.historicalpenza.data.LandmarksRepository
import kotlinx.android.synthetic.main.dialog_log_out.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class LogOutDialog : BaseDialog() {

    private val preferences: Preferences by lazy {
        Preferences.getInstance(context!!)
    }

    private val repository: LandmarksRepository by lazy {
        LandmarksRepository.getInstance(context!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_log_out, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        positiveButton.setOnClickListener {
            val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.client_server_id))
                    .build()
            val googleSignInClient = GoogleSignIn.getClient(context!!, signInOptions)

            googleSignInClient.signOut().addOnCompleteListener {
                with(preferences) {
                    token = ""
                    shouldNotify = true
                }

                GlobalScope.launch {
                    repository.resetLandmarks()
                }

                val intent = Intent(activity, SignInActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                startActivity(intent)

                dismiss()
                activity?.finish()
            }
        }
        negativeButton.setOnClickListener {
            dismiss()
        }
    }

}
