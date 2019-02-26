package com.projectcenterfvt.historicalpenza.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import com.projectcenterfvt.historicalpenza.DataBases.DataBaseHandler;
import com.projectcenterfvt.historicalpenza.DataBases.Sight;
import com.projectcenterfvt.historicalpenza.Managers.PreferencesManager;
import com.projectcenterfvt.historicalpenza.R;
import com.projectcenterfvt.historicalpenza.Server.BaseAsyncTask;
import com.projectcenterfvt.historicalpenza.Server.ClientServer;
import com.projectcenterfvt.historicalpenza.Server.LoginServer;
import com.projectcenterfvt.historicalpenza.Service.InternetReceive;


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
    private  static final int REQ_CODE_PERM = 111;

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
        internetReceive.setOnInternetStatusChange(new InternetReceive.onInternetStatusChange() {
            @Override
            public void onSuccess() {
                Log.d("Broadcast", "Получил callback от сервера: интернет есть");
                validateServerClientID();
                signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.client_server_id))
                        .build();

                mGoogleSignInClient = GoogleSignIn.getClient(activity, signInOptions);

                nextPage();
            }

            @Override
            public void onFailure() {
                Toast.makeText(getApplicationContext(), "Необходимо подключение к интернету!", Toast.LENGTH_LONG).show();
            }
        });

    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allowed = true;

        switch (requestCode){
            case REQ_CODE_PERM:

                for (int res : grantResults){
                    // if user granted all permissions.
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }

                break;
            default:
                // if user not granted permissions.
                allowed = false;
                break;
        }

        if (allowed){
            nextPage();
        }
        else {
            // we will give warning to user that they haven't granted permissions.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)){

                    Toast.makeText(this, "Без этого невозможно функционирование приложения", Toast.LENGTH_LONG).show();
                    //Toast.makeText(this,"Приложение будет закрыто",Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
//                    SplashActivity.this.startActivity(intent);
//                    SplashActivity.this.finish();
//                    Handler handler = new Handler();
//                  handler.postDelayed(new Runnable() {
//                       public void run() {
//                           Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
//                           SplashActivity.this.startActivity(intent);
//                           SplashActivity.this.finish();
//                       }
//                    }, 5000);

                } else {
                    showNoStoragePermissionSnackbar();
                }
            }
        }

    }

    public void showNoStoragePermissionSnackbar() {

     final Snackbar snackbar2 =   Snackbar.make(SplashActivity.this.findViewById(R.id.activity_splash_p), "Разрешение на отслеживание местоположения не дано" , Snackbar.LENGTH_INDEFINITE);
        snackbar2.setAction("Настройки", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        openApplicationSettings();

                        Toast.makeText(getApplicationContext(),
                                "Откройте разрешения и дайте разрешение для отслеживания местоположения",
                                Toast.LENGTH_SHORT)
                                .show();
                        snackbar2.dismiss();
                    }
                })
                .show();
    }
    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(appSettingsIntent, REQ_CODE_PERM);
    }
    public void requestPermissionWithRationale() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            final String message = "Нам нужно ваше местополодения для функционироания приложения";
            final Snackbar snackBar = Snackbar.make(findViewById(R.id.activity_splash_p), message, Snackbar.LENGTH_INDEFINITE);
            snackBar//.make//(SplashActivity.this.findViewById(R.id.activity_splash_p), message, Snackbar.LENGTH_LONG +300000)
                    .setAction("Разрешить", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestPerms();

                            snackBar.dismiss();
                        }
                    })
                    .show();
        } else {

            requestPerms();
        }
    }


    private void requestPerms(){
        String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(permissions,REQ_CODE_PERM);
        }
    }
    private boolean hasPermissions(){

        //string array of permissions,
        String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION  };

        for (String perms : permissions){
            int res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }

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
        if (!hasPermissions()){
            requestPermissionWithRationale();
            Log.d("perm", "не могу найти перммшионс");

        }
        else {
            db = new DataBaseHandler(activity);
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
        if (requestCode == REQ_CODE_PERM) {
            if (hasPermissions()){
                nextPage();
            return;
            }
            else {
                Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            }
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





