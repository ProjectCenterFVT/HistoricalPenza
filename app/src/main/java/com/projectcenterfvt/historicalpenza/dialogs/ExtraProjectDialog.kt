package com.projectcenterfvt.historicalpenza.Dialogs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.projectcenterfvt.historicalpenza.R
import kotlinx.android.synthetic.main.dialog_extra_project.*


class ExtraProjectDialog : BaseDialog() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.dialog_extra_project, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cancelButton.setOnClickListener { dismiss() }

        linkButton.setOnClickListener {
            val address = Uri.parse(ADDRESS)
            val intent = Intent(Intent.ACTION_VIEW, address)
            startActivity(intent)
            dismiss()
        }
    }

    companion object {
        const val ADDRESS = "https://vk.com/po_sledam_usadeb58"
    }
}