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
        DB_Position dbPosition = new DB_Position(this);
        dbPosition.import_db();
        if (!dbPosition.isCreate())
            dbPosition.writeDB();
        try {
            dbPosition.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
