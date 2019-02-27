package com.projectcenterfvt.historicalpenza.Dialogs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.projectcenterfvt.historicalpenza.R
import kotlinx.android.synthetic.main.dialog_help_project.*

class HelpProjectDialog : BaseDialog() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.dialog_help_project, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sendEmailButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("creativityprojectcenter@gmail.com"))
            startActivity(intent)

            dismiss()
        }

        cancelButton.setOnClickListener { dismiss() }
    }
}
