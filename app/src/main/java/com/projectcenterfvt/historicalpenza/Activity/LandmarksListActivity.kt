package com.projectcenterfvt.historicalpenza.Activity

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import com.projectcenterfvt.historicalpenza.Adapters.LandmarksAdapter
import com.projectcenterfvt.historicalpenza.R
import com.projectcenterfvt.historicalpenza.data.LandmarksRepository
import com.projectcenterfvt.historicalpenza.utils.viewModelFactory
import kotlinx.android.synthetic.main.activity_landmarks_list.*


class LandmarksListActivity : AppCompatActivity() {

    private val viewModel: LandmarksListViewModel by lazy {
        val repository = LandmarksRepository.getInstance(this)
        ViewModelProviders
                .of(this, viewModelFactory { LandmarksListViewModel(repository) })
                .get(LandmarksListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landmarks_list)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.landmarks_list_activity_title)
        }

        list.layoutManager = LinearLayoutManager(this)
        list.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        viewModel.landmarks.observe(this, Observer { landmarks ->
            landmarks?.let {
                if (list.adapter == null) {
                    list.adapter = LandmarksAdapter(landmarks) { landmark ->
                        val intent = Intent()
                        intent.putExtra("latitude", landmark.position.latitude)
                        intent.putExtra("longitude", landmark.position.longitude)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                } else {
                    (list.adapter as LandmarksAdapter).setData(landmarks)
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_landmarks_list, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}
