package com.projectcenterfvt.historicalpenza.Activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import com.projectcenterfvt.historicalpenza.Fragments.GreetingPageFragment
import com.projectcenterfvt.historicalpenza.R
import kotlinx.android.synthetic.main.activity_greeting.*

class GreetingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_greeting)

        pager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {

            override fun getItem(position: Int): Fragment {
                return GreetingPageFragment.newInstance(position)
            }

            override fun getCount(): Int {
                return GreetingPageFragment.COUNT
            }

        }
        dots.setupWithViewPager(pager, true)

        continueButton.setOnClickListener {
            when (pager.currentItem) {
                GreetingPageFragment.COUNT - 1 -> {
                    val intent = Intent(this, MapActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else -> {
                    pager.currentItem = GreetingPageFragment.COUNT - 1
                }
            }
        }
    }

}

