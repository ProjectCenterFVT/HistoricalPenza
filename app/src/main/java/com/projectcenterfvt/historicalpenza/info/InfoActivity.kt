package com.projectcenterfvt.historicalpenza.info

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.projectcenterfvt.historicalpenza.R
import com.projectcenterfvt.historicalpenza.data.Landmark
import com.projectcenterfvt.historicalpenza.dialogs.ExtraProjectDialog
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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                scroll.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                    if (scrollY > oldScrollY) {
                        fab.hide()
                    } else {
                        fab.show()
                    }
                }
            }
        } else fab.hide()
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
