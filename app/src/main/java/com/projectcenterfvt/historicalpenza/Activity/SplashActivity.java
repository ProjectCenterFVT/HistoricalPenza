package com.projectcenterfvt.historicalpenza.Activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.projectcenterfvt.historicalpenza.DataBases.DB_Position;
import com.projectcenterfvt.historicalpenza.DataBases.Sight;
import com.projectcenterfvt.historicalpenza.R;
import com.projectcenterfvt.historicalpenza.Server.ClientServer;

/**
 * Активити
 * Класс служит для отрисовки лого и подгрузки базы данных <b>DB_Position</b>
 *
 * @author Roman, Dmitry
 * @version 1.0.0
 * @see DB_Position
 * @since 1.0.0
 */

public class SplashActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener{

    /** ID, по которому выводятся логи в Logcat*/
    static final String TAG = "server";
    /** Ключ, по которому проверяется первый запуск приложения*/
    static final String KEY_IS_FIRST_TIME = "first_time";
    /** Экземпляр класс базы данных*/
    private static DB_Position db;
    private SignInButton sign_in_button;
    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 9001;

    /**
     * Метод вызывается при создании или перезапуска активности <br>
     * (Временное решение) Удаляется старая бд и от сервера получаем новую <br>
     * Если {@link #KEY_IS_FIRST_TIME} = true, то переходим на <b>GreetingActivity</b> <br>
     * Иначе переходим на <b>MapActivity</b> <br>
     * @see GreetingActivity
     * @see MapActivity
     * @param savedInstanceState сохраненное состояние <br>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final TextView textViewHistoric;
        final TextView textViewPenza;
        textViewHistoric = findViewById(R.id.textViewHistorical);
        textViewPenza = findViewById(R.id.textView8);
        sign_in_button = (SignInButton) findViewById(R.id.sign_in_button);
        sign_in_button.setOnClickListener(this);
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions)
                .build();
        db = new DB_Position(this);


        ClientServer call = new ClientServer(this);
        call.setOnResponseListener(new ClientServer.OnResponseListener<Sight>() {
            /**
             * Метод вызывается при успешном ответе от сервера
             */
            @Override
            public void onSuccess(Sight[] result) {
                db.connectToWrite();
                db.getDB().delete(DB_Position.DB_TABLE, null, null);

                for (Sight aResult : result) {
                    ContentValues contentValues = new ContentValues();

                    contentValues.put(DB_Position.COLUMN_ID, aResult.getId());
                    Log.d(TAG, "вствавил  id = " + aResult.getId());

                    contentValues.put(DB_Position.COLUMN_X1, aResult.getLatitude());
                    Log.d(TAG, "вствавил  x1 = " + aResult.getLatitude());

                    contentValues.put(DB_Position.COLUMN_X2, aResult.getLongitude());
                    Log.d(TAG, "вствавил  x2 = " + aResult.getLongitude());

                    contentValues.put(DB_Position.COLUMN_flag, aResult.getFlag());
                    Log.d(TAG, "вствавил  flag = " + aResult.getFlag());

                    contentValues.put(DB_Position.COLUMN_type, aResult.getType());
                    Log.d(TAG, "вствавил  type = " + aResult.getType());

                    db.getDB().insert(DB_Position.DB_TABLE, null, contentValues);
                }

                db.close();

            }
            /**
             * Метод вызывается при неудачном подключении
             */
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(SplashActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                Intent intent;
                if (isFirstTime()) {
                    getPreferences(Context.MODE_PRIVATE).edit().putBoolean(KEY_IS_FIRST_TIME, false).apply();

                    intent = new Intent(SplashActivity.this, GreetingActivity.class);
                } else
                    intent = new Intent(SplashActivity.this, MapActivity.class);

                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            }
        });
        call.getCoordinates();
    }

    /**
     * Метод отвечает за проверку первого запуска приложения
     * @return булевое значение
     */
    public boolean isFirstTime() {
        //return getPreferences(Context.MODE_PRIVATE).getBoolean(KEY_IS_FIRST_TIME, true);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sign_in_button:
            signIn(); break;
        }
    }
private void signIn(){
Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
startActivityForResult(intent,REQ_CODE);
}
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
    private void handleResult(GoogleSignInResult result){
        if (result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            String name = account.getDisplayName();
            String email = account.getEmail();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE) {
            GoogleSignInResult  result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

        }
    }
}
