package com.projectcenterfvt.historicalpenza.landmarks_list

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.projectcenterfvt.historicalpenza.R
import kotlinx.android.synthetic.main.sorting_bottom_sheet.view.*


class SortingBottomDialog : BottomSheetDialogFragment() {

    lateinit var alphabeticListener : () -> Unit
    lateinit var distanceListener : () -> Unit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.sorting_bottom_sheet, container)
        view.alphabeticSort.setOnClickListener {
            alphabeticListener.invoke()
            dialog?.dismiss()
        }
        view.distanceSort.setOnClickListener {
            distanceListener.invoke()
            dialog?.dismiss()
        }
        return view
    }

    fun setOnAlphabeticSortListener(listener : () -> Unit) {
        alphabeticListener = listener
    }

    fun setOnDistanceSortListener(listener : () -> Unit) {
        distanceListener = listener
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dialog?.window?.apply {
                findViewById<View>(com.google.android.material.R.id.container)
                        .fitsSystemWindows = false
                val decorView = decorView
                decorView.systemUiVisibility = decorView.systemUiVisibility or
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
        }
    }
}