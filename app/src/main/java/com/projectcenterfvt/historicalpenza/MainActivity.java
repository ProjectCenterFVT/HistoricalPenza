package com.projectcenterfvt.historicalpenza;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {
//санина
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void test(View view) {
        Intent intent = new Intent(this, ActivityMap.class);
        startActivity(intent);
    }
}
