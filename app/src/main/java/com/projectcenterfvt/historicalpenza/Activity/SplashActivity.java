package com.projectcenterfvt.historicalpenza.Activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.projectcenterfvt.historicalpenza.DataBases.DB_Position;
import com.projectcenterfvt.historicalpenza.DataBases.Sight;
import com.projectcenterfvt.historicalpenza.R;
import com.projectcenterfvt.historicalpenza.Server.BaseAsyncTask;
import com.projectcenterfvt.historicalpenza.Server.ClientServer;
import com.projectcenterfvt.historicalpenza.Server.LoginServer;


/**
 * Активити
 * Класс служит для отрисовки лого и подгрузки базы данных <b>DB_Position</b>
 *
 * @author Roman, Dmitry
 * @version 1.0.0
 * @see DB_Position
 * @since 1.0.0
 */

public class SplashActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    public static final String APP_PREFERENCES = "account";
    public static final String APP_PREFERENCES_TOKEN = "token";
    /**
     * ID, по которому выводятся логи в Logcat
     */
    static final String TAG = "server";
    /**
     * Ключ, по которому проверяется первый запуск приложения
     */
    static final String KEY_IS_FIRST_TIME = "first_time";
    private static final int REQ_CODE = 9002;
    private static DB_Position db;
    private static String url = "http://hpenza.creativityprojectcenter.ru/api.request.php";
    /**
     * Экземпляр класс базы данных
     */
    private GoogleSignInOptions signInOptions;
    private SignInButton sign_in_button;
    private TextView textViewHistoric;
    private TextView textViewPenza;
    private GoogleSignInClient mGoogleSignInClient;
    private SharedPreferences mAccount;
    private Animation mAnimationFadeOut;
    /**
     * Метод вызывается при создании или перезапуска активности <br>
     * (Временное решение) Удаляется старая бд и от сервера получаем новую <br>
     * Если {@link #KEY_IS_FIRST_TIME} = true, то переходим на <b>GreetingActivity</b> <br>
     * Иначе переходим на <b>MapActivity</b> <br>
     *
     * @param savedInstanceState сохраненное состояние <br>
     * @see GreetingActivity
     * @see MapActivity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mAccount = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        setContentView(R.layout.activity_splash);
        textViewHistoric = findViewById(R.id.textViewHistorical);
        textViewPenza = findViewById(R.id.textView8);
        sign_in_button = findViewById(R.id.sign_in_button);
        sign_in_button.setEnabled(false);
        sign_in_button.setOnClickListener(this);
        validateServerClientID();
        mAnimationFadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);

        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_server_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, signInOptions);

        db = new DB_Position(this);
        nextPage();

    }
/**
 * вызывается при нажатии на кнопку войти*/
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQ_CODE);
    }

    private void validateServerClientID() {
        String serverClientId = getString(R.string.client_server_id);
        String suffix = ".apps.googleusercontent.com";
        if (!serverClientId.trim().endsWith(suffix)) {
            String message = "Invalid server client ID in strings.xml, must end with " + suffix;

            Log.w(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Метод отвечает за проверку первого запуска приложения
     *
     * @return булевое значение
     */
    public boolean isFirstTime() {
        return mAccount.getBoolean(KEY_IS_FIRST_TIME, true);

    }

    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
        try {

            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();
            sendToBackEnd(idToken);

        } catch (ApiException e) {
            Log.w(TAG, "handleSignInResult:error", e);

        }
    }
/**
 * метод отправляет данные на сервер*/
    private void sendToBackEnd(String idToken) {
        LoginServer ls = new LoginServer();
        ls.setOnResponseListener(new BaseAsyncTask.OnResponseListener<String>() {
            @Override
            public void onSuccess(String result) {

                Log.d("mToken", result);
                SharedPreferences.Editor editor = mAccount.edit();
                editor.putString(APP_PREFERENCES_TOKEN, result);
                editor.putBoolean(KEY_IS_FIRST_TIME, false);
                editor.apply();
                idToServer(result);
                Intent intent = new Intent(getApplicationContext(), GreetingActivity.class);
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();

            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(SplashActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
        ls.getLogin(idToken);

    }

    /**
     * Метод проверяющий первый ли раз запущ. приложение
     * Если не 1-ый. то 3 сек. ждем и переход на MapActivity
     * Иначе проресовываем кнопку google sign  и удаляем текст "Историческая Пенза"
     */

    public void nextPage() {

        if (!isFirstTime()) {
            idToServer(mAccount.getString(APP_PREFERENCES_TOKEN, ""));
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    startActivity(new Intent(getApplicationContext(), MapActivity.class));
                }
            }, 3000);
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    textViewHistoric.startAnimation(mAnimationFadeOut);
                    textViewPenza.startAnimation(mAnimationFadeOut);
                    textViewHistoric.setVisibility(View.INVISIBLE);
                    textViewPenza.setVisibility(View.INVISIBLE);
                    sign_in_button.setEnabled(true);
                    sign_in_button.setVisibility(View.VISIBLE);
                }
            }, 2000);
        }

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private void idToServer(final String mIdTokrn) {
        ClientServer call = new ClientServer();
        call.setOnResponseListener(new BaseAsyncTask.OnResponseListener<Sight[]>() {
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

                    mAccount.edit().putBoolean(KEY_IS_FIRST_TIME, false).apply();
                    intent = new Intent(SplashActivity.this, GreetingActivity.class);

                } else {

                    intent = new Intent(SplashActivity.this, MapActivity.class);

                    SplashActivity.this.startActivity(intent);
                    SplashActivity.this.finish();

                }
            }

        });
        call.getCoordinates(mIdTokrn);
    }

}





