package com.projectcenterfvt.historicalpenza.Activity

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.projectcenterfvt.historicalpenza.BuildConfig
import com.projectcenterfvt.historicalpenza.Dialogs.HomestadeDialog
import com.projectcenterfvt.historicalpenza.R
import com.projectcenterfvt.historicalpenza.data.LandmarksRepository
import com.projectcenterfvt.historicalpenza.data.network.LandmarksNetwork
import com.projectcenterfvt.historicalpenza.utils.toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_info.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class InfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        val landmarkName = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val uml = intent.getStringExtra("uml")
        val check = intent.getBooleanExtra("button", false)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = landmarkName
        }

        body.text = description

        Picasso.with(this)
                .load("${BuildConfig.API_ENDPOINT}/img/$uml")
                .into(photo)

        if (check) {
            fab.show()
            fab.setOnClickListener {
                val fragmentManager = supportFragmentManager
                val dialog = HomestadeDialog()
                dialog.show(fragmentManager, "dialog")
            }
        } else fab.hide()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
