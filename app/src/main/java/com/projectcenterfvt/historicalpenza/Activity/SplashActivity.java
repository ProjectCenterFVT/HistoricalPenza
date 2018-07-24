package com.projectcenterfvt.historicalpenza.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;
import com.projectcenterfvt.historicalpenza.BuildConfig;
import com.projectcenterfvt.historicalpenza.DataBases.DataBaseHandler;
import com.projectcenterfvt.historicalpenza.DataBases.Sight;
import com.projectcenterfvt.historicalpenza.Managers.ImageCacheManager;
import com.projectcenterfvt.historicalpenza.Managers.PreferencesManager;
import com.projectcenterfvt.historicalpenza.R;
import com.projectcenterfvt.historicalpenza.Server.BaseAsyncTask;
import com.projectcenterfvt.historicalpenza.Server.ClientServer;
import com.projectcenterfvt.historicalpenza.Server.LoginServer;
import com.projectcenterfvt.historicalpenza.Service.InternetReceive;

import java.io.File;


/**
 * Активити
 * Класс служит для отрисовки лого и подгрузки базы данных <b>DataBaseHandler</b>
 *
 * @author Roman, Dmitry
 * @version 1.0.0
 * @see DataBaseHandler
 * @since 1.0.0
 */

public class SplashActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {


    static final String TAG = "server";
    private static final int REQ_CODE = 9002;
    private static final int RESPONCE_LOCATION_PERMISSION_KEY = 21;
    private static final int RESPONCE_EXTERNAL_STORAGE_PERMISSION_KEY = 22;
    private static final String PERMISIONS_TAG = "permisions";
    private static DataBaseHandler db;
    /**
     * Экземпляр класс базы данных
     */
    private GoogleSignInOptions signInOptions;
    private SignInButton sign_in_button;
    private GoogleSignInClient mGoogleSignInClient;
    private PreferencesManager preferencesManager;
    private Activity activity;
    private InternetReceive internetReceive;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preferencesManager = new PreferencesManager(getApplicationContext());
        sign_in_button = findViewById(R.id.sign_in_button);
        sign_in_button.setEnabled(false);
        sign_in_button.setOnClickListener(this);
        internetReceive = new InternetReceive();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetReceive, intentFilter);
        activity = this;
        MultiplePermissionsListener snackbarMultiplePermissionsListener =
                SnackbarOnAnyDeniedMultiplePermissionsListener.Builder
                        .with(this.findViewById(R.id.splash), "Необохдим доступ к вашему местоположению")
                        .withOpenSettingsButton("Settings")
                        .withCallback(new Snackbar.Callback() {
                            @Override
                            public void onShown(Snackbar snackbar) {
                                // Event handler for when the given Snackbar has been dismissed
                            }
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                // Event handler for when the given Snackbar is visible
                            }
                        })
                        .build();
        Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(snackbarMultiplePermissionsListener)
                .check();
        internetReceive.setOnInternetStatusChange(new InternetReceive.onInternetStatusChange() {
            @Override
            public void onSuccess() {
                Log.d("Broadcast", "Получил callback от сервера: интернет есть");
                validateServerClientID();
                signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.client_server_id))
                        .build();

                mGoogleSignInClient = GoogleSignIn.getClient(activity, signInOptions);

                db = new DataBaseHandler(activity);
                nextPage();
            }

            @Override
            public void onFailure() {
                Log.d("Broadcast", "Получил callback от сервера: интернета нет");
                Toast.makeText(getApplicationContext(), "Необходимо подключение к интернету!", Toast.LENGTH_LONG).show();
            }
        });

    }

//    private void checkLocationPermission(){
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//
//            } else {
//                Log.d(PERMISIONS_TAG, "Разрешения на местоположения не получены");
//                ActivityCompat.requestPermissions(this, new String[]
//                        {
//                                Manifest.permission.ACCESS_FINE_LOCATION,
//                                Manifest.permission.ACCESS_COARSE_LOCATION
//                        }, RESPONCE_LOCATION_PERMISSION_KEY);
//            }
//        } else {
//            Log.d(PERMISIONS_TAG, "Разрешения на местоположения получены");
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(internetReceive);
    }

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

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode){
//            case RESPONCE_LOCATION_PERMISSION_KEY:
//                if (grantResults.length <= 0) {
//
//                } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Log.d(PERMISIONS_TAG, "Местоположение доступно");
//                } else {
//                    Intent intent = new Intent();
//                    intent.setAction(
//                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                    Uri uri = Uri.fromParts("package",
//                            BuildConfig.APPLICATION_ID, null);
//                    intent.setData(uri);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                }
//                break;
//            case RESPONCE_EXTERNAL_STORAGE_PERMISSION_KEY:
//                if (grantResults.length <= 0) {
//
//                } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Log.d(PERMISIONS_TAG, "Создаю папку");
//                    ImageCacheManager.createFolder();
//                }
//                break;
//        }
//    }

    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
        try {

            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            sendToBackEnd(account.getIdToken());

        } catch (ApiException e) {
            Log.w(TAG, "handleSignInResult:error", e);

        }
    }
/**
 * метод отправляет данные на сервер*/
    private void sendToBackEnd(String id) {
    LoginServer ls = new LoginServer(getApplicationContext());
        ls.setOnResponseListener(new BaseAsyncTask.OnResponseListener<String>() {
            @Override
            public void onSuccess(String result) {

                Log.d("mToken", result);
                preferencesManager.setToken(result);
                preferencesManager.setFirstTime(false);

                idToServer();
                Intent intent = new Intent(getApplicationContext(), GreetingActivity.class);
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();

            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    ls.getLogin(id);

    }

    /**
     * Метод проверяющий первый ли раз запущ. приложение
     * Если не 1-ый. то 3 сек. ждем и переход на MapActivity
     * Иначе проресовываем кнопку google sign  и удаляем текст "Историческая Пенза"
     */

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }
    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }
    public void nextPage() {

        if (!preferencesManager.getFirstTime()) {
            idToServer();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), MapActivity.class));
                        finish();
                    }
                }, 3000);
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    signOut();
                    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein_alpha);
                    sign_in_button.setEnabled(true);
                    sign_in_button.setVisibility(View.VISIBLE);
                    sign_in_button.startAnimation(anim);
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

    private void idToServer() {
        ClientServer call = new ClientServer(getApplicationContext());
        call.setOnResponseListener(new BaseAsyncTask.OnResponseListener<Sight[]>() {
            /**
             * Метод вызывается при успешном ответе от сервера
             */
            @Override
            public void onSuccess(Sight[] result) {
                db.deleteAll();
                for (Sight aResult : result) {
                    db.addSight(aResult);
                }
                db.close();

            }

            /**
             * Метод вызывается при неудачном подключении
             */
            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Нет интернета!");
                //Toast.makeText(getApplicationContext(), "Ошибка соединения с сервером, пожалуйста, повторите попытку позже!", Toast.LENGTH_LONG).show();
//                Intent intent;
//
//                if (preferencesManager.getFirstTime()) {
//
//                    preferencesManager.setFirstTime(false);
//                    intent = new Intent(SplashActivity.this, GreetingActivity.class);
//                    SplashActivity.this.startActivity(intent);
//                    SplashActivity.this.finish();
//
//                } else {
//
//                    intent = new Intent(SplashActivity.this, MapActivity.class);
//                    SplashActivity.this.startActivity(intent);
//                    SplashActivity.this.finish();
//
//                }
            }

        });
        call.getCoordinates();
    }


}





