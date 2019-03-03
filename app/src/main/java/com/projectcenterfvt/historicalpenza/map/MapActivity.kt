package com.projectcenterfvt.historicalpenza.map

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.algo.GridBasedAlgorithm
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.projectcenterfvt.historicalpenza.dialogs.*
import com.projectcenterfvt.historicalpenza.R
import com.projectcenterfvt.historicalpenza.data.Landmark
import com.projectcenterfvt.historicalpenza.data.LandmarksRepository
import com.projectcenterfvt.historicalpenza.landmarks_list.LandmarksListActivity
import com.projectcenterfvt.historicalpenza.utils.*
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.activity_map_content.*
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion


class MapActivity : AppCompatActivity(),
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener,
        NavigationView.OnNavigationItemSelectedListener {

    private val viewModel: MapViewModel by lazy {
        val repository = LandmarksRepository.getInstance(this)
        ViewModelProviders
                .of(this, viewModelFactory {
                    MapViewModel(application, repository)
                })
                .get(MapViewModel::class.java)
    }

    private lateinit var map: GoogleMap

    private lateinit var clusterManager: ClusterManager<LandmarkMarker>

    private var currentLocationMarker: Marker? = null

    private var remainCameraPosition = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        remainCameraPosition = savedInstanceState != null

        OnMapAndViewReadyListener(mapFragment as SupportMapFragment, this)

        setupSearchView()

        navigationViewTop.setNavigationItemSelectedListener(this)
        navigationViewBottom.setNavigationItemSelectedListener(this)
    }

    private fun setupSearchView() {
        searchView.attachNavigationDrawerToMenuButton(drawerLayout)

        viewModel.suggestions.observe(this, Observer { suggestions ->
            suggestions ?: return@Observer

            searchView.swapSuggestions(suggestions)
        })

        searchView.setOnQueryChangeListener { oldQuery, newQuery ->
            if (oldQuery != "" && newQuery == "") searchView.clearSuggestions()
            else viewModel.setSearchQuery(newQuery)
        }

        searchView.setOnSearchListener(object : FloatingSearchView.OnSearchListener {

            override fun onSuggestionClicked(searchSuggestion: SearchSuggestion?) {
                searchView.clearSearchFocus()

                val suggestion = searchSuggestion as Suggestion
                val landmark = viewModel.getLandmark(suggestion.id) ?: return
                map.animateCamera(CameraUpdateFactory
                        .newLatLngZoom(landmark.position, DEFAULT_ZOOM),
                        ANIMATE_CAMERA_DURATION, null)
            }

            override fun onSearchAction(currentQuery: String?) {}

        })

        searchView.setOnFocusChangeListener(object : FloatingSearchView.OnFocusChangeListener {

            override fun onFocus() {
                val suggestions = viewModel.suggestions.value ?: return
                searchView.swapSuggestions(suggestions)
            }

            override fun onFocusCleared() {}

        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.landmarks_menu_item -> {
                val location = viewModel.currentLocation.value?.getLatLng()
                val intent = LandmarksListActivity.getIntent(this, location)
                startActivityForResult(intent, SIGHT_KEY)
            }
            R.id.help_project_menu_item -> showDialog(HelpProjectDialog())
            R.id.settings_menu_item -> showDialog(SettingsDialog())
            R.id.help_menu_item -> showDialog(GuideDialog())
            R.id.about_menu_item -> showDialog(AboutDialog())
            R.id.extra_project_menu_item -> showDialog(ExtraProjectDialog())
            R.id.log_out_menu_item -> showDialog(LogOutDialog())
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap ?: return

        setUpButtons()

        setUpClusterManager()
        viewModel.landmarks.observe(this, Observer { landmarks ->
            if (landmarks != null && landmarks.isNotEmpty()) {
                if (!remainCameraPosition) {
                    remainCameraPosition = true
                    val boundsBuilder = LatLngBounds.Builder()
                    landmarks.map { landmark -> boundsBuilder.include(landmark.position) }
                    val bounds = boundsBuilder.build()
                    map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                }

                val mapper = MarkerMapper()
                val markers = landmarks.map { landmark ->
                    mapper.mapFromDomain(landmark)
                }
                addMarkersToMap(markers)
            }
        })

        viewModel.currentLocation.observe(this, Observer { location ->
            location ?: return@Observer

            if (currentLocationMarker != null) {
                currentLocationMarker!!.animateMarkerTo(location.getLatLng())
            } else {
                currentLocationMarker = map.addMarker(
                        MarkerOptions().apply {
                            position(location.getLatLng())
                            icon(bitmapDescriptorFromVector(R.drawable.ic_marker_my_position))
                        })
            }
        })
    }

    private fun setUpButtons() {
        zoomOutButton.setOnClickListener {
            map.animateCamera(CameraUpdateFactory.zoomBy(-0.5f))
        }

        zoomInButton.setOnClickListener {
            map.animateCamera(CameraUpdateFactory.zoomBy(0.5f))
        }

        myLocationButton.setOnClickListener {
            val location = viewModel.currentLocation.value ?: return@setOnClickListener
            map.animateCamera(CameraUpdateFactory
                    .newLatLngZoom(location.getLatLng(), DEFAULT_ZOOM))
        }

        nearPlaceButton.setOnClickListener {
            val landmark = viewModel.getClosestLandmark() ?: return@setOnClickListener
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(landmark.position, DEFAULT_ZOOM))
        }
    }

    private fun setUpClusterManager() {
        clusterManager = ClusterManager(this, map)

        clusterManager.renderer =
                object : DefaultClusterRenderer<LandmarkMarker>(this, map, clusterManager) {
                    override fun onBeforeClusterItemRendered(item: LandmarkMarker?,
                                                             markerOptions: MarkerOptions?) {
                        val drawableRes = when (item!!.type) {
                            Landmark.Type.USUAL -> {
                                if (item.isOpened) R.drawable.ic_marker_opened_landmark
                                else R.drawable.ic_marker_closed_landmark
                            }
                            Landmark.Type.EXTRA -> R.drawable.ic_marker_extra_project
                        }
                        markerOptions!!.icon(bitmapDescriptorFromVector(drawableRes))
                    }
        }

        clusterManager.algorithm = GridBasedAlgorithm<LandmarkMarker>()
        map.setOnCameraIdleListener(clusterManager)

        clusterManager.setOnClusterClickListener { cluster ->
            val builder = LatLngBounds.builder()
            for (item in cluster.items) {
                builder.include(item.position)
            }
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100))
            true
        }
        clusterManager.setOnClusterItemClickListener { landmarkMarker ->
            showLandmark(landmarkMarker.id)
            true
        }

        map.setOnMarkerClickListener(clusterManager)
    }

    private fun showLandmark(id: Long) {
        val landmark = viewModel.getLandmark(id) ?: return

        val currentLocation = viewModel.currentLocation.value?.getLatLng()
        showDialog(LandmarkDialog.newInstance(landmark, currentLocation))
    }

    private fun addMarkersToMap(markers: List<LandmarkMarker>) {
        clusterManager.clearItems()
        clusterManager.addItems(markers)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            SIGHT_KEY -> {
                if (resultCode == Activity.RESULT_OK) {
                    val id = data?.getLongExtra(
                            LandmarksListActivity.LANDMARK_EXTRA_ID, -1) ?: return

                    val landmark = viewModel.getLandmark(id) ?: return
                    map.animateCamera(CameraUpdateFactory
                            .newLatLngZoom(landmark.position, DEFAULT_ZOOM),
                            ANIMATE_CAMERA_DURATION, null)
                }
            }
        }
    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        if ((requestCode == 34) or (requestCode == 35)) {
//            if (grantResults.isEmpty()) {
//                // If user interaction was interrupted, the permission request is cancelled and you
//                // receive empty arrays.
//            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted.
//                //getLastLocation();
//            } else {
//                val intent = Intent()
//                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//                val uri = Uri.fromParts("package",
//                        BuildConfig.APPLICATION_ID, null)
//                intent.data = uri
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                startActivity(intent)
//            }
//        }
//    }

    companion object {

        private const val SIGHT_KEY = 400
        private const val DEFAULT_ZOOM = 15.0F
        private const val ANIMATE_CAMERA_DURATION = 1000

    }

}

