package com.projectcenterfvt.historicalpenza.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.projectcenterfvt.historicalpenza.Fragments.PageFragment;

/**
 * Created by Dmitry on 13.12.2017.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final int PAGE_COUNT = 3;

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return PageFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

}
