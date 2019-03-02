package com.projectcenterfvt.historicalpenza.Dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.model.LatLng
import com.projectcenterfvt.historicalpenza.Activity.InfoActivity
import com.projectcenterfvt.historicalpenza.R
import com.projectcenterfvt.historicalpenza.data.Landmark
import com.projectcenterfvt.historicalpenza.utils.toast
import kotlinx.android.synthetic.main.dialog_landmark.*

class LandmarkDialog : BaseDialog() {

    lateinit var landmark: Landmark
    lateinit var lastKnownLocation: LatLng

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_landmark, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        landmarkTitle.text = landmark.title
        val dist = calculateDistance(lastKnownLocation, landmark.position)
        distance.text = "$dist m"

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
                dismiss()
                context?.toast("Потом сделаю")
            }
        }

        cancelButton.setOnClickListener { dismiss() }
    }

    private fun calculateDistance(position1: LatLng, position2: LatLng): Int {
        val R = 6372795
        val x1 = position1.latitude * Math.PI / 180
        val y1 = position1.longitude * Math.PI / 180
        val x2 = position2.latitude * Math.PI / 180
        val y2 = position2.longitude * Math.PI / 180
        val res = Math.acos(Math.sin(x1) * Math.sin(x2) + Math.cos(x1) * Math.cos(x2) * Math.cos(y1 - y2)) * R
        return res.toInt()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        landmark = arguments?.getParcelable(LANDMARK_EXTRA)
                ?: throw IllegalArgumentException()
        lastKnownLocation = arguments?.getParcelable(LAST_KNOWN_LOCATION_EXTRA)
                ?: throw IllegalArgumentException()

        return super.onCreateDialog(savedInstanceState)
    }

    companion object {
        const val LANDMARK_EXTRA = "landmark_extra"
        const val LAST_KNOWN_LOCATION_EXTRA = "last_known_location_extra"

        fun newInstance(landmark: Landmark, lastKnownLocation: LatLng) : LandmarkDialog {
            val landmarkDialog = LandmarkDialog()
            val bundle = Bundle().apply {
                putParcelable(LANDMARK_EXTRA, landmark)
                putParcelable(LAST_KNOWN_LOCATION_EXTRA, lastKnownLocation)
            }
            landmarkDialog.arguments = bundle
            return landmarkDialog
        }

    }

}