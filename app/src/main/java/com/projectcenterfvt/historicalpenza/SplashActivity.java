package com.projectcenterfvt.historicalpenza;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

    static final String TAG = "server";

    private final int SPLASH_DISPLAY_LENGTH = 2000;

    static final String KEY_IS_FIRST_TIME = "first_time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final DB_Position db = new DB_Position(this);

        ClientServer call = new ClientServer(this);
        call.setOnResponseListener(new ClientServer.OnResponseListener<Sight>() {
            @Override
            public void onSuccess(Sight[] result) {
                SQLiteDatabase databases = db.getWritableDatabase();
                databases.delete(db.DB_TABLE, null, null);

                for (Sight aResult : result) {
                    ContentValues contentValues = new ContentValues();

                    contentValues.put(db.COLUMN_ID, aResult.id);
                    Log.d(TAG, "вствавил  id = " + aResult.id);

                    contentValues.put(db.COLUMN_X1, aResult.x1);
                    Log.d(TAG, "вствавил  x1 = " + aResult.x1);

                    contentValues.put(db.COLUMN_X2, aResult.x2);
                    Log.d(TAG, "вствавил  x2 = " + aResult.x2);

                    contentValues.put(db.COLUMN_flag, aResult.flag);
                    Log.d(TAG, "вствавил  flag = " + aResult.flag);

                    databases.insert(db.DB_TABLE, null, contentValues);
                }

                db.close();

                Intent intent;

                if(isFirstTime()) {
                    getPreferences(Context.MODE_PRIVATE).edit().putBoolean(KEY_IS_FIRST_TIME, false).apply();
                    intent = new Intent(SplashActivity.this, GreetingActivity.class);
                } else
                    intent = new Intent(SplashActivity.this, ActivityMap.class);

                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(SplashActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
        call.getCoordinates();
    }

    public boolean isFirstTime() {
        return getPreferences(Context.MODE_PRIVATE).getBoolean(KEY_IS_FIRST_TIME, true);
    }
}
