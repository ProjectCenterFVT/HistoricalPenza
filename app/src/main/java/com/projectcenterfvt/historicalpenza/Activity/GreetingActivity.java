package com.projectcenterfvt.historicalpenza.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.projectcenterfvt.historicalpenza.Adapters.MyFragmentPagerAdapter;
import com.projectcenterfvt.historicalpenza.R;

public class GreetingActivity extends AppCompatActivity {

    ViewPager pager;
    PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeting);


        pager = findViewById(R.id.pager);
        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(pager, true);

    }

    public void continueClick(View view) {
        startActivity(new Intent(this, MapActivity.class));
    }

}

