package com.projectcenterfvt.historicalpenza.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.projectcenterfvt.historicalpenza.Fragments.GuidePageFragment;

/**
 * Created by Dmitry on 13.12.2017.
 */

public class MyGuideFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final int PAGE_COUNT = 4;

    public MyGuideFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return GuidePageFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

}
