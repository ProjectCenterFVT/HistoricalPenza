package com.projectcenterfvt.historicalpenza.dialogs

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.projectcenterfvt.historicalpenza.R
import kotlinx.android.synthetic.main.fragment_page.*


class GuidePageFragment : Fragment() {

    private var page: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        page = arguments?.getInt(PAGE_NUMBER_EXTRA) ?: 0
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        content.setImageResource(pages[page])
    }

    companion object {

        private val pages = listOf(
                R.drawable.help_location,
                R.drawable.help_unlock,
                R.drawable.help_lock,
                R.drawable.help_homestade
        )

        val COUNT = pages.size

        private const val PAGE_NUMBER_EXTRA = "page_number_extra"

        fun newInstance(page: Int): GuidePageFragment {
            val pageFragment = GuidePageFragment()

            val bundle = Bundle().apply {
                putInt(PAGE_NUMBER_EXTRA, page)
            }
            pageFragment.arguments = bundle

            return pageFragment
        }
    }
}