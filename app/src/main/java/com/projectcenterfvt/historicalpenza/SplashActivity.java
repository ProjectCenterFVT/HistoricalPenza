package com.projectcenterfvt.historicalpenza;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

    static final String TAG = "server";
    private static DB_Position db;
    static final String KEY_IS_FIRST_TIME = "first_time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        db = new DB_Position(this);

        ClientServer call = new ClientServer(this);
        call.setOnResponseListener(new ClientServer.OnResponseListener<Sight>() {
            @Override
            public void onSuccess(Sight[] result) {
                db.connectToWrite();
                db.getDB().delete(db.DB_TABLE, null, null);

                for (Sight aResult : result) {
                    ContentValues contentValues = new ContentValues();

                    contentValues.put(db.COLUMN_ID, aResult.getId());
                    Log.d(TAG, "вствавил  id = " + aResult.getId());

                    contentValues.put(db.COLUMN_X1, aResult.getLatitude());
                    Log.d(TAG, "вствавил  x1 = " + aResult.getLatitude());

                    contentValues.put(db.COLUMN_X2, aResult.getLongitude());
                    Log.d(TAG, "вствавил  x2 = " + aResult.getLongitude());

                    contentValues.put(db.COLUMN_flag, aResult.getFlag());
                    Log.d(TAG, "вствавил  flag = " + aResult.getFlag());

                    db.getDB().insert(db.DB_TABLE, null, contentValues);
                }

                db.close();

                Intent intent;

                if(isFirstTime()) {
                    getPreferences(Context.MODE_PRIVATE).edit().putBoolean(KEY_IS_FIRST_TIME, false).apply();
                    intent = new Intent(SplashActivity.this, GreetingActivity.class);
                } else
                    intent = new Intent(SplashActivity.this, MapActivity.class);

                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(SplashActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                Intent intent;
                if(isFirstTime()) {
                    getPreferences(Context.MODE_PRIVATE).edit().putBoolean(KEY_IS_FIRST_TIME, false).apply();
                    intent = new Intent(SplashActivity.this, GreetingActivity.class);
                } else
                    intent = new Intent(SplashActivity.this, MapActivity.class);

                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            }
        });
        call.getCoordinates();
    }

    public boolean isFirstTime() {
        return getPreferences(Context.MODE_PRIVATE).getBoolean(KEY_IS_FIRST_TIME, true);
    }
}
