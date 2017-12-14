package com.projectcenterfvt.historicalpenza;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class info_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity);

        TextView tvDescription = (TextView) findViewById(R.id.tvDescription);
        tvDescription.setMovementMethod(new ScrollingMovementMethod());

    }

}
