package com.projectcenterfvt.historicalpenza.dialogs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.projectcenterfvt.historicalpenza.sign_in.SignInActivity
import com.projectcenterfvt.historicalpenza.data.Preferences
import com.projectcenterfvt.historicalpenza.R
import kotlinx.android.synthetic.main.dialog_log_out.*


class LogOutDialog : BaseDialog() {

    private val preferences: Preferences by lazy {
        Preferences.getInstance(context!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_log_out, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        positiveButton.setOnClickListener {
            with(preferences) {
                token = ""
                shouldNotify = true
            }

            val intent = Intent(activity, SignInActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(intent)

            dismiss()
            activity?.finish()
        }
        negativeButton.setOnClickListener {
            dismiss()
        }
    }

}
