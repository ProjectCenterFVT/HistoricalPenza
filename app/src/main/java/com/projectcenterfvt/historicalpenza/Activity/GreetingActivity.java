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

/**
 * Активити
 * Класс служит для отрисовки экрана при первом запуске
 *
 * @author Dmitry
 * @version 1.0.0
 * @since 1.0.0
 */
public class GreetingActivity extends AppCompatActivity {
    /** Экзмепляр класса ViewPager*/
    ViewPager pager;
    /** Экземпляр класса PagerAdapter */
    PagerAdapter pagerAdapter;

    /**
     * Метод вызывается при создании или перезапуска активности <br>
     * Отрисовка экрана при первом запуске
     * @param savedInstanceState
     */
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

    /**
     * Обработчик нажатия на кнопку "Продолжить"
     * @param view Кнопка
     */
    public void continueClick(View view) {
        int currentItem = pager.getCurrentItem();
        if (currentItem != 2) {
            pager.setCurrentItem(2);
        } else {
            startActivity(new Intent(this, MapActivity.class));
        }
    }

}

