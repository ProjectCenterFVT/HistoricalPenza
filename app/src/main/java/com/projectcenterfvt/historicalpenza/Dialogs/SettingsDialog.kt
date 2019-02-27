package com.projectcenterfvt.historicalpenza.Dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.projectcenterfvt.historicalpenza.Managers.PreferencesManager
import com.projectcenterfvt.historicalpenza.R
import kotlinx.android.synthetic.main.dialog_settings.*

class SettingsDialog : BaseDialog() {

    lateinit var preferences: PreferencesManager

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        preferences = PreferencesManager(context)

        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.dialog_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notifySwitch.setOnCheckedChangeListener { _, checked ->
            preferences.notificationStatus = checked
        }

        cancelButton.setOnClickListener { dismiss() }
    }

}