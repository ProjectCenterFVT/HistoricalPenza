package com.projectcenterfvt.historicalpenza.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.projectcenterfvt.historicalpenza.BuildConfig
import com.projectcenterfvt.historicalpenza.Dialogs.ExtraProjectDialog
import com.projectcenterfvt.historicalpenza.R
import com.projectcenterfvt.historicalpenza.data.Landmark
import com.projectcenterfvt.historicalpenza.utils.showDialog
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_info.*

class InfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        val landmark = intent.getParcelableExtra<Landmark>(LANDMARK_EXTRA)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = landmark.title
        }

        body.text = landmark.description

        Picasso.with(this)
                .load(landmark.photoUrl.toString())
                .into(photo)

        if (landmark.type == Landmark.Type.EXTRA) {
            fab.show()
            fab.setOnClickListener {
                showDialog(ExtraProjectDialog())
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

    companion object {

        private const val LANDMARK_EXTRA = "landmark_extra"

        fun getIntent(context: Context?, landmark: Landmark) : Intent {
            return Intent(context, InfoActivity::class.java).apply {
                putExtra(LANDMARK_EXTRA, landmark)
            }
        }

    }
}
