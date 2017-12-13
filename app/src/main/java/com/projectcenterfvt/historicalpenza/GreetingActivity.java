package com.projectcenterfvt.historicalpenza;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class GreetingActivity extends AppCompatActivity {

    private final int QUOTE_DISPLAY_LENGTH = 5000;

    Animation fadein, fadeout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeting);


    }

    public void continueClick(View view) {
        Intent intent = new Intent(this, ActivityMap.class);
        startActivity(intent);
    }
}
