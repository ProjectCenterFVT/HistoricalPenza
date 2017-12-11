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

        fadein = AnimationUtils.loadAnimation(this, R.anim.fadein_alpha);
        fadeout = AnimationUtils.loadAnimation(this, R.anim.fadeout_alpha);

        final TextView tvQuote = (TextView) findViewById(R.id.tvQuote);
        tvQuote.startAnimation(fadein);
        tvQuote.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tvQuote.startAnimation(fadeout);
                tvQuote.setVisibility(View.GONE);

                TextView tvDescription = (TextView) findViewById(R.id.tvDescription);
                tvDescription.startAnimation(fadein);
                tvDescription.setVisibility(View.VISIBLE);

                Button btnContinue = (Button) findViewById(R.id.btnContinue);
                btnContinue.startAnimation(fadein);
                btnContinue.setVisibility(View.VISIBLE);
            }
        }, QUOTE_DISPLAY_LENGTH);
    }

    public void continueClick(View view) {
        Intent intent = new Intent(this, ActivityMap.class);
        startActivity(intent);
    }
}
