package com.projectcenterfvt.historicalpenza.landmarks_list

import android.annotation.SuppressLint
import android.app.Activity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import com.google.android.gms.maps.model.LatLng
import com.projectcenterfvt.historicalpenza.R
import com.projectcenterfvt.historicalpenza.data.LandmarksRepository
import com.projectcenterfvt.historicalpenza.utils.viewModelFactory
import kotlinx.android.synthetic.main.activity_landmarks_list.*
import kotlinx.android.synthetic.main.activity_landmarks_list.toolbar
import kotlinx.android.synthetic.main.sorting_bottom_sheet.view.*
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.DisplayMetrics
import android.app.Dialog
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi


class LandmarksListActivity : AppCompatActivity() {

    private val viewModel: LandmarksListViewModel by lazy {
        val repository = LandmarksRepository.getInstance(this)
        val lastKnownLocation: LatLng? = intent.getParcelableExtra(LAST_KNOWN_LOCATION_EXTRA)
        ViewModelProviders
                .of(this, viewModelFactory {
                    LandmarksListViewModel(repository, lastKnownLocation)
                })
                .get(LandmarksListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landmarks_list)

        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
        ViewCompat.setOnApplyWindowInsetsListener(appbar) { v, insets ->
            v.updatePadding(top = insets.systemWindowInsetTop)
            insets
        }

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.landmarks_list_activity_title)
        }

        list.layoutManager = LinearLayoutManager(this)
        list.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        viewModel.adapterItems.observe(this, Observer { adapterItems ->
            adapterItems?.let {
                if (list.adapter == null) {
                    list.adapter = LandmarksAdapter(adapterItems) { item ->
                        val intent = Intent().apply {
                            putExtra(LANDMARK_EXTRA_ID, item.id)
                        }
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                } else {
                    (list.adapter as LandmarksAdapter).setData(adapterItems)
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_landmarks_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.sort -> {
                showBottomSheet()
            }
        }
        return false
    }

    @SuppressLint("InflateParams")
    private fun showBottomSheet() {
        val dialog = SortingBottomDialog()
        dialog.setOnAlphabeticSortListener {
            viewModel.setFiltering(Filtering.ALPHABETIC)
        }
        dialog.setOnDistanceSortListener {
            viewModel.setFiltering(Filtering.DISTANCE)
        }
        dialog.show(supportFragmentManager, "sorting_dialog_fragment")
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {

        const val LANDMARK_EXTRA_ID = "landmark_id_extra"
        private const val LAST_KNOWN_LOCATION_EXTRA = "last_known_location_extra"

        fun getIntent(context: Context?, lastKnownLocation: LatLng?) : Intent {
            return Intent(context, LandmarksListActivity::class.java).apply {
                putExtra(LAST_KNOWN_LOCATION_EXTRA, lastKnownLocation)
            }
        }

    }

}
