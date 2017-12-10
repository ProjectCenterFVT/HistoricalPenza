package com.projectcenterfvt.historicalpenza;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 2000;

    static final String KEY_IS_FIRST_TIME = "first_time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;

                if(isFirstTime()) {
                    getPreferences(Context.MODE_PRIVATE).edit().putBoolean(KEY_IS_FIRST_TIME, false).apply();
                    intent = new Intent(SplashActivity.this, GreetingActivity.class);
                } else
                    intent = new Intent(SplashActivity.this, ActivityMap.class);

                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    public boolean isFirstTime() {
        return getPreferences(Context.MODE_PRIVATE).getBoolean(KEY_IS_FIRST_TIME, true);
    }
}
