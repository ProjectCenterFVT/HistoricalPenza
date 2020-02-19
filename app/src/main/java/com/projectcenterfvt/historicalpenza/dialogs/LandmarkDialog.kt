package com.projectcenterfvt.historicalpenza.dialogs

import android.app.Dialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.model.LatLng
import com.projectcenterfvt.historicalpenza.App
import com.projectcenterfvt.historicalpenza.R
import com.projectcenterfvt.historicalpenza.data.Landmark
import com.projectcenterfvt.historicalpenza.data.LandmarksRepository
import com.projectcenterfvt.historicalpenza.info.InfoActivity
import com.projectcenterfvt.historicalpenza.managers.BillingManager
import com.projectcenterfvt.historicalpenza.utils.distanceTo
import com.projectcenterfvt.historicalpenza.utils.viewModelFactory
import kotlinx.android.synthetic.main.dialog_landmark.*

class LandmarkDialog : BaseDialog() {

    private val viewModel: LandmarkViewModel by lazy {
        val repository = LandmarksRepository.getInstance(context!!)
        ViewModelProviders
                .of(this, viewModelFactory {
                    LandmarkViewModel(landmarkId, repository)
                })
                .get(LandmarkViewModel::class.java)
    }

    var landmarkId = 0L
    private var lastKnownLocation: LatLng? = null

    private val billingManager: BillingManager by lazy {
        (activity!!.application as App).billingManager
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.dialog_landmark, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.landmark.observe(this, Observer { landmark ->
            landmark ?: return@Observer

            landmarkTitle.text = landmark.title
            lastKnownLocation?.let {
                val dist = it.distanceTo(landmark.position)
                distance.text = getString(R.string.landmark_dialog_distance_text, dist)
            }

            if (landmark.isOpened || landmark.type == Landmark.Type.EXTRA) {
                status.text = getString(R.string.is_opened_text)
                button.text = getString(R.string.is_opened_button_text)
                button.setOnClickListener {
                    activity?.startActivity(InfoActivity.getIntent(context, landmark))
                    dismiss()
                }
            } else {
                status.text = getString(R.string.is_closed_text)
                button.text = getString(R.string.is_closed_button_text)
                button.setOnClickListener {
                    billingManager.initiatePurchaseFlow(activity!!)
                }
            }
        })

        cancelButton.setOnClickListener { dismiss() }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        landmarkId = arguments?.getLong(LANDMARK_ID_EXTRA)!!
        lastKnownLocation = arguments?.getParcelable(LAST_KNOWN_LOCATION_EXTRA)

        return super.onCreateDialog(savedInstanceState)
    }

    companion object {
        const val LANDMARK_ID_EXTRA = "landmark_id_extra"
        const val LAST_KNOWN_LOCATION_EXTRA = "last_known_location_extra"

        fun newInstance(landmarkId: Long, lastKnownLocation: LatLng?): LandmarkDialog {
            val landmarkDialog = LandmarkDialog()
            val bundle = Bundle().apply {
                putLong(LANDMARK_ID_EXTRA, landmarkId)
                putParcelable(LAST_KNOWN_LOCATION_EXTRA, lastKnownLocation)
            }
            landmarkDialog.arguments = bundle
            return landmarkDialog
        }

    }

}