package com.projectcenterfvt.historicalpenza.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.projectcenterfvt.historicalpenza.R
import com.projectcenterfvt.historicalpenza.data.Preferences
import kotlinx.android.synthetic.main.dialog_settings.*

class SettingsDialog : BaseDialog() {

    private val preferences: Preferences by lazy {
        Preferences.getInstance(context!!)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.dialog_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notifySwitch.isChecked = preferences.shouldNotify
        notifySwitch.setOnCheckedChangeListener { _, checked ->
            preferences.shouldNotify = checked
        }

        cancelButton.setOnClickListener { dismiss() }
    }

}