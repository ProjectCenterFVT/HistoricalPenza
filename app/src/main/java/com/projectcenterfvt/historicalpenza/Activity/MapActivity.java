package com.projectcenterfvt.historicalpenza.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.projectcenterfvt.historicalpenza.BuildConfig;
import com.projectcenterfvt.historicalpenza.DataBases.DSightHandler;
import com.projectcenterfvt.historicalpenza.DataBases.DataBaseHandler;
import com.projectcenterfvt.historicalpenza.Dialogs.AboutDialog;
import com.projectcenterfvt.historicalpenza.Dialogs.ExtraProjectDialog;
import com.projectcenterfvt.historicalpenza.Dialogs.HelpProjectDialog;
import com.projectcenterfvt.historicalpenza.Dialogs.LogOutDialog;
import com.projectcenterfvt.historicalpenza.Dialogs.GuideDialog;
import com.projectcenterfvt.historicalpenza.Dialogs.SettingsDialog;
import com.projectcenterfvt.historicalpenza.Managers.BillingManager;
import com.projectcenterfvt.historicalpenza.Managers.CameraManager;
import com.projectcenterfvt.historicalpenza.Managers.ClusterHundler;
import com.projectcenterfvt.historicalpenza.Managers.MarkerManager;
import com.projectcenterfvt.historicalpenza.Managers.MyBillingUpdateListener;
import com.projectcenterfvt.historicalpenza.Managers.PreferencesManager;
import com.projectcenterfvt.historicalpenza.Managers.SearchManager;
import com.projectcenterfvt.historicalpenza.R;
import com.projectcenterfvt.historicalpenza.Service.LocationService;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

/**
 * Вся основная работа происходит в этом классе. Однако основные задачи распределены по классам менеджерам.
 * Класс отрисовывает карту и все её элементы, включая меню
 *
 * @author Roman, Dmitry
 * @version 1.0.0
 * @see CameraManager
 * @see com.projectcenterfvt.historicalpenza.Managers.LocationManager
 * @see MarkerManager
 * @see SearchManager
 * @since 1.0.0
 */

public class MapActivity extends AppCompatActivity
        implements  NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

    private static final int SIGHT_KEY = 2;
    LocationService locationService;
    /** Карта*/
    private GoogleMap mMap;
    /** API*/
    private GoogleApiClient mGoogleApiClient;
    /** Позиция пользователя*/
    private Location mLastKnownLocation;
    /** Кнопка - мое местоположение*/
    private Button btn_pos;
    /** Контекст*/
    private Context context = this;
    /** Меню*/
    private DrawerLayout mDrawerLayout;
    /** Менеджер маркеров*/
    private MarkerManager markerManager;
    /** Менеджер поиска*/
    private SearchManager searchManager;
    /** Менеджер камеры*/
    private CameraManager cameraManager;
    /** Менеджер геопозиции*/
    private com.projectcenterfvt.historicalpenza.Managers.LocationManager locationManager;
    /**
     * Менеджер настроек
     */
    private PreferencesManager preferencesManager;

    private DSightHandler dSightHandler;

    private Activity activity;
   //Billing

    /**
     * !!!! Не трогать и не прикосаться. Это тестирующий вариант обработки местоположения. ТРЕБУЕТСЯ В ДОРАБОТКЕ И ТЕСТИРОВАНИИ
     */
    private boolean check = false;
    private int CAMERA_KEY = 1;
    private String TAG_GEO = "Geoinformation";
    private Intent serviceIntent;
    private ServiceConnection sConn;
    private ClusterHundler clusterHundler;

    private boolean isMapDraw = false;
    private boolean isMarkerClick = false;
    BillingManager  billingManager;
    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.d(TAG_GEO, "status - " + s + " i = " + i);
        }

        @Override
        public void onProviderEnabled(String s) {
            if (Build.VERSION.SDK_INT < 23) {
                locationManager.updateLocationUI(true, btn_pos);
                markerManager.showMyMarker();
                Log.d(TAG_GEO, "status : " + s);
            }
        }

        @Override
        public void onProviderDisabled(String s) {
            if (Build.VERSION.SDK_INT < 23) {
                if (markerManager != null) {
                    markerManager.inviseMyMarker();
                }
                locationManager.updateLocationUI(false, btn_pos);
                Log.d(TAG_GEO, "status : " + s);
            }
        }
    };

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        isMapDraw = savedInstanceState.getBoolean("mapState");
    }

    /**
     * Отрисовка элементов и объявление нужных объектов
     * @param savedInstanceState Сохраненное состояние
     */
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        serviceIntent = new Intent(MapActivity.this, LocationService.class);
        setContentView(R.layout.activity_map);
        preferencesManager = new PreferencesManager(getApplicationContext());
        btn_pos = findViewById(R.id.myLocationButton);
        final LocationManager lM = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationManager = new com.projectcenterfvt.historicalpenza.Managers.LocationManager(this, MapActivity.this);
        locationManager.updateLocationUI(true, btn_pos);
        dSightHandler = new DSightHandler(getApplicationContext());
        locationManager.setdSightHandler(dSightHandler);

        lM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 4, locationListener);
        lM.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 4, locationListener);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();

        NavigationView navigationView = findViewById(R.id.navigationViewTop);
        navigationView.setNavigationItemSelectedListener(this);

        NavigationView navigationView1 = findViewById(R.id.navigationViewBottom);
        navigationView1.setNavigationItemSelectedListener(this);

        mDrawerLayout = findViewById(R.id.drawerLayout);
        searchManager = new SearchManager(this, mDrawerLayout);
        searchManager.setSearchView((FloatingSearchView) findViewById(R.id.floatingSearchView));
        checkForUpdates();

        if (savedInstanceState != null){
            isMarkerClick = savedInstanceState.getBoolean("mapState");
        }

          billingManager = new BillingManager(MapActivity.this, new MyBillingUpdateListener());
    }

    @Override
    protected void onStart() {
        super.onStart();
        sConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                LocationService.LocalBinder binder = (LocationService.LocalBinder) iBinder;
                locationService = binder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        try {
            bindService(serviceIntent, sConn, Context.BIND_AUTO_CREATE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this);
    }

//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (markerManager != null) {
            markerManager.removeMyMarker();
            clusterHundler.clearMap();
        }
    }

    /**
     * Слушатель нажатия на элементы в меню
     * @param item Элемент в меню
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.name_sight:
                mLastKnownLocation = locationManager.getDeviceLocation();
//                ArrayList<Sight> sights = new DataBaseHandler(this).getAllSight();
//                Collections.sort(sights, new Comparator<Sight>() {
//                    @Override
//                    public int compare(Sight s0, Sight s1) {
//                        s0.setDistance(DSightHandler.calculateDistance(mLastKnownLocation, s0.getLocation()));
//                        s1.setDistance(DSightHandler.calculateDistance(mLastKnownLocation, s1.getLocation()));
//                        return s0.getDistance() - s1.getDistance();
//                    }
//                });
                Intent intent = new Intent(this, LandmarksListActivity.class);
//                intent.putParcelableArrayListExtra("sights",sights);
                startActivityForResult(intent, SIGHT_KEY);
                break;
            case R.id.name_helpProject:
                HelpProjectDialog dialog = new HelpProjectDialog();

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                dialog.show(ft, "dialog");
                break;
            case R.id.name_settings:
                SettingsDialog dialog1 = new SettingsDialog();

                FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                Fragment prev1 = getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev1 != null) {
                    ft1.remove(prev1);
                }
                ft1.addToBackStack(null);

                dialog1.show(ft1, "dialog");
                break;
            case R.id.name_help:
                Log.d("click ", "нажал на кнопку хелп");
                FragmentManager fragmentManager_hp = getSupportFragmentManager();
                GuideDialog dialog_hp = new GuideDialog();
                dialog_hp.show(fragmentManager_hp, "dialog");
                break;
            case R.id.name_about:
                Log.d("click ", "нажал на кнопку абоут");
                FragmentManager fragmentManager_about = getSupportFragmentManager();
                AboutDialog dialog_about = new AboutDialog();
                dialog_about.show(fragmentManager_about, "dialog");
                break;
            case R.id.name_homestade:
                Log.d("click ", "нажал на кнопку усадеб");
                FragmentManager fragmentManager_homestade = getSupportFragmentManager();
                ExtraProjectDialog dialog_homestade = new ExtraProjectDialog();
                dialog_homestade.show(fragmentManager_homestade, "dialog");
                break;
            case R.id.name_logout:
                Log.d("click", "Нажал на выйти из аккаунта");
                FragmentManager fragmentManager_logout = getSupportFragmentManager();
                LogOutDialog logoutDialog = new LogOutDialog();
                logoutDialog.show(fragmentManager_logout, "dialog");

                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        isMapDraw = true;
        outState.putBoolean("mapState", isMapDraw);

        super.onSaveInstanceState(outState);
    }

    /**
     * Отрисовка карты, объявление менеджеров
     * @param googleMap Карта
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        clusterHundler = new ClusterHundler(mMap, this, this ,billingManager);
        clusterHundler.setLocationManager(locationManager);
        clusterHundler.setupClusterManager();
        if (!isMapDraw) {
            clusterHundler.addSights(new DataBaseHandler(context).getAllSight());
        } else {
            clusterHundler.addSights(new DataBaseHandler(context).getAllSight());
            clusterHundler.restoreMap();
        }
        markerManager = new MarkerManager(mMap, this);
        locationManager.setMarkerManager(markerManager);
        mLastKnownLocation = locationManager.getDeviceLocation();
        dSightHandler.sortList(mLastKnownLocation);
        markerManager.addStartMarker();
        markerManager.addMyMarker(mLastKnownLocation);
        cameraManager = new CameraManager(this, mMap);
        searchManager.setCameraManager(cameraManager);
        Log.d("check", "check = " + check);
        if (!check)
            cameraManager.setCameraPosition(mLastKnownLocation);
        searchManager.setupSearch();
        String token = preferencesManager.getToken();
        serviceIntent.putExtra("token", token);
        locationService.setContext(this);
        locationService.setdSightHandler(dSightHandler);
        locationService.setClusterHundler(clusterHundler);
        startService(serviceIntent);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void plus(View view) {
        synchronized (clusterHundler) {
            final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.activity_down_up_close_enter);
            view.startAnimation(animAlpha);
            mMap.animateCamera(CameraUpdateFactory.zoomBy(0.5f));
        }
    }

    public void minus(View view) {
        synchronized (clusterHundler) {
            final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.activity_down_up_close_enter);
            view.startAnimation(animAlpha);
            mMap.animateCamera(CameraUpdateFactory.zoomBy(-0.5f));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Camera", "Получил результат " + requestCode);
        if (requestCode == CAMERA_KEY) {
            check = true;
        }
        if (requestCode == SIGHT_KEY && data != null){
            check = true;
            double lat = data.getDoubleExtra("latitude", 0);
            double lon = data.getDoubleExtra("longitude", 0);
            cameraManager.setCameraToCloseSight(new LatLng(lat,lon));
        }
    }

    public void lookAtMe(View view) {
        Log.d("pos", "нажал на кнопку");
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.activity_down_up_close_enter);
        view.startAnimation(animAlpha);
        mLastKnownLocation = locationManager.getDeviceLocation();
        cameraManager.setCameraPosition(mLastKnownLocation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForCrashes();
        if (clusterHundler != null){
            clusterHundler.restoreMap();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try{
            unregisterManagers();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterManagers();
        unbindService(sConn);
    }

    private void unregisterManagers() {
        UpdateManager.unregister();
    }

    private void checkForCrashes() {
        CrashManager.register(this);
    }

    public void close_target(View view) {
        try {
            if (mLastKnownLocation != null && cameraManager != null && dSightHandler != null) {
                synchronized (dSightHandler) {
                    cameraManager.setCameraPosition(dSightHandler.getCloseLocation());
                }
            }
        } catch (Exception ex) {
            Log.d("ex", ex.getMessage());
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 34 | requestCode == 35) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                //getLastLocation();
            } else {
                Intent intent = new Intent();
                intent.setAction(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package",
                        BuildConfig.APPLICATION_ID, null);
                intent.setData(uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

}

