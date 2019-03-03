package com.projectcenterfvt.historicalpenza.dialogs

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.projectcenterfvt.historicalpenza.R
import kotlinx.android.synthetic.main.dialog_guide.*


class GuideDialog : BaseDialog() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.dialog_guide, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pager.adapter = object : FragmentPagerAdapter(childFragmentManager) {

            override fun getItem(position: Int): Fragment {
                return GuidePageFragment.newInstance(position)
            }

            override fun getCount(): Int {
                return GuidePageFragment.COUNT
            }

        }
        dots.setupWithViewPager(pager, true)

        cancelButton.setOnClickListener { dismiss() }
    }
}
