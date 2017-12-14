package com.projectcenterfvt.historicalpenza;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;

public class GreetingActivity extends AppCompatActivity {

    static final int PAGE_COUNT = 3;

    ViewPager pager;
    PagerAdapter pagerAdapter;

    Animation fadein, fadeout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeting);


        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(pager, true);

//        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrolled(int position, float positionOffset,
//                                       int positionOffsetPixels) {
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//            }
//        });

    }

    public void continueClick(View view) {
        startActivity(new Intent(this, ActivityMap.class));
    }

}

