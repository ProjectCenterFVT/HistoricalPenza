package com.projectcenterfvt.historicalpenza;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Dmitry on 13.12.2017.
 */

class MyGuideFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final int PAGE_COUNT = 3;

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
