package com.projectcenterfvt.historicalpenza.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.Marker;
import com.projectcenterfvt.historicalpenza.BuildConfig;
import com.projectcenterfvt.historicalpenza.DataBases.DB_Position;
import com.projectcenterfvt.historicalpenza.DataBases.Sight;
import com.projectcenterfvt.historicalpenza.Dialogs.AboutDialog;
import com.projectcenterfvt.historicalpenza.Dialogs.CardDialog;
import com.projectcenterfvt.historicalpenza.Dialogs.HomestadeDialog;
import com.projectcenterfvt.historicalpenza.Dialogs.PageDialog;
import com.projectcenterfvt.historicalpenza.Managers.CameraManager;
import com.projectcenterfvt.historicalpenza.Managers.ListManager;
import com.projectcenterfvt.historicalpenza.Managers.MarkerManager;
import com.projectcenterfvt.historicalpenza.Managers.SearchManager;
import com.projectcenterfvt.historicalpenza.R;
import com.projectcenterfvt.historicalpenza.Server.BaseAsyncTask;
import com.projectcenterfvt.historicalpenza.Server.ClientServer;
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
 * @see ListManager
 * @see com.projectcenterfvt.historicalpenza.Managers.LocationManager
 * @see MarkerManager
 * @see SearchManager
 * @since 1.0.0
 */

public class MapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, GoogleMap.OnMarkerClickListener, CardDialog.onEventListener {

    public static final String APP_PREFERENCES = "account";
    public static final String APP_PREFERENCES_TOKEN = "token";
    /** Сохранение предыдушей позиции камеры после разворота приложения*/
    private static final String KEY_CAMERA_POSITION = "camera_position";
    /**Сохранение предыдушей позиции после разворота приложения */
    private static final String KEY_LOCATION = "location";
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
    /** БД*/
    private DB_Position database;
    /** Менеджер маркеров*/
    private MarkerManager markerManager;
    /** Менеджер поиска*/
    private SearchManager searchManager;
    /** Менеджер камеры*/
    private CameraManager cameraManager;
    /** Менеджер достопримечательностей*/
    private ListManager listManager;
    /** Менеджер геопозиции*/
    private com.projectcenterfvt.historicalpenza.Managers.LocationManager locationManager;
    /**
     * !!!! Не трогать и не прикосаться. Это тестирующий вариант обработки местоположения. ТРЕБУЕТСЯ В ДОРАБОТКЕ И ТЕСТИРОВАНИИ
     */
    private boolean check;
    private int CAMERA_KEY = 1;
    private String TAG_GEO = "Geoinformation";
    private Intent serviceIntent;
    private SharedPreferences mAccount;
    private ServiceConnection sConn;

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
            locationManager.updateLocationUI(true, btn_pos);
            markerManager.showMyMarker();
            Log.d(TAG_GEO, "status : " + s);
        }

        @Override
        public void onProviderDisabled(String s) {
            if (markerManager != null) {
                markerManager.inviseMyMarker();
            }
            locationManager.updateLocationUI(false, btn_pos);
            Log.d(TAG_GEO, "status : " + s);
        }
    };

    //comment
    @Override
    public void setPosition(LatLng loc) {
        cameraManager.setCameraPosition(loc);
    }

    /**
     * Отрисовка элементов и объявление нужных объектов
     * @param savedInstanceState Сохраненное состояние
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceIntent = new Intent(MapActivity.this, LocationService.class);
        setContentView(R.layout.activity_map);
        database = new DB_Position(this);
        listManager = new ListManager();
        btn_pos = findViewById(R.id.btn_position);
        final LocationManager lM = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locationManager = new com.projectcenterfvt.historicalpenza.Managers.LocationManager(this, MapActivity.this);
        if (!locationManager.checkPermissions()) {
            locationManager.startLocationPermissionRequest();
        } else {
            Log.d("Location", "Настройки полученны");
        }
        locationManager.setListManager(listManager);

        lM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 4, locationListener);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        NavigationView navigationView1 = findViewById(R.id.navigation_drawer_bottom);
        navigationView1.setNavigationItemSelectedListener(this);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        searchManager = new SearchManager(this, mDrawerLayout, database);
        searchManager.setSearchView((FloatingSearchView) findViewById(R.id.floating_search_view));
        searchManager.setupSearch();
        checkForUpdates();

        check = savedInstanceState != null;

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
    }

    @Override
    protected void onStart() {
        super.onStart();
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

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

        if (id == R.id.name_sight) {
            ClientServer call = new ClientServer();
            call.setOnResponseListener(new BaseAsyncTask.OnResponseListener<Sight[]>() {
                @Override
                public void onSuccess(Sight[] result) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    CardDialog cardDialog = new CardDialog();

                    for (int i = 0; i < result.length; i++) {
                        Sight sight = listManager.getList().get(i);
                        int id = sight.getId() - 1;
                        sight.setTitle(result[id].getTitle());
                        listManager.getList().set(i, sight);
                    }

                    cardDialog.setList(listManager.getList());
                    cardDialog.show(fragmentManager, "dialog");
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(MapActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            });
            call.getAllInfo();
        } else if (id == R.id.name_helpProject) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View view = inflater.inflate(R.layout.help_project_menu, null);
            view.setBackgroundResource(R.drawable.dialog_bgn);
            builder.setView(view);
            final AlertDialog alert = builder.create();
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            view.findViewById(R.id.btnBackThird).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.hide();
                }
            });
            alert.show();
        } else if (id == R.id.name_settings) {
            Log.d("click ", "нажал на кнопку сетингс");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View view = inflater.inflate(R.layout.settings_menu, null);
            view.setBackgroundResource(R.drawable.dialog_bgn);
            builder.setView(view);
            final AlertDialog alert = builder.create();
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            view.findViewById(R.id.btnBackForth).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.hide();
                }
            });
            alert.show();
        } else if (id == R.id.name_help) {
            Log.d("click ", "нажал на кнопку хелп");
            FragmentManager fragmentManager = getSupportFragmentManager();
            PageDialog dialog = new PageDialog();
            dialog.show(fragmentManager, "dialog");

        } else if (id == R.id.name_about) {
            Log.d("click ", "нажал на кнопку абоут");
            FragmentManager fragmentManager = getSupportFragmentManager();
            AboutDialog dialog = new AboutDialog();
            dialog.show(fragmentManager, "dialog");
        } else if (id == R.id.name_homestade) {
            Log.d("click ", "нажал на кнопку усадеб");
            FragmentManager fragmentManager = getSupportFragmentManager();
            HomestadeDialog dialog = new HomestadeDialog();
            dialog.show(fragmentManager, "dialog");
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Отрисовка карты, объявление менеджеров
     * @param googleMap Карта
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMinZoomPreference(12.0f);
        mMap.setMaxZoomPreference(17.0f);
        mMap.setOnMarkerClickListener(this);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        markerManager = new MarkerManager(mMap, this);
        locationManager.setMarkerManager(markerManager);
        mLastKnownLocation = locationManager.getDeviceLocation();
        markerManager.addStartMarker();
        markerManager.addMyMarker(mLastKnownLocation);
        cameraManager = new CameraManager(this, mMap);
        cameraManager.setCameraPosition(mLastKnownLocation);
        searchManager.setCameraManager(cameraManager);
        if (!check)
            cameraManager.setCameraPosition(mLastKnownLocation);
        listManager.setList(database.fillArray(mMap, mLastKnownLocation, markerManager));
        listManager.setDistance(mLastKnownLocation);
        //serviceIntent.putParcelableArrayListExtra("list", listManager.getList());
        mAccount = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        String token = mAccount.getString(APP_PREFERENCES_TOKEN, " ");
        serviceIntent.putExtra("token", token);
        locationService.setContext(this);
        locationService.setMarkerManager(markerManager);
        locationService.setListManager(listManager);
        startService(serviceIntent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("check", true);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        check = savedInstanceState.getBoolean("check");
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
        mMap.animateCamera(CameraUpdateFactory.zoomBy(1.0f));
    }

    public void minus(View view) {
        mMap.animateCamera(CameraUpdateFactory.zoomBy(-1.0f));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_KEY) {
            check = true;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Обработка нажатие на маркер
     * @param marker Маркер
     * @return
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        Log.d("marker", "Нажал на маркер " + marker.getId() + " " + marker.getTitle() + " " + marker.getPosition().toString());
        if (marker.getTag() != null) {
            final Sight sight = (Sight) marker.getTag();
            final int id = sight.getId();

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final LayoutInflater inflater = this.getLayoutInflater();
            final View view = inflater.inflate(R.layout.dialog, null);
            view.setBackgroundResource(R.drawable.dialog_bgn);

            final TextView info = view.findViewById(R.id.dialog_text_info);
            final TextView distance = view.findViewById(R.id.dialog_text_distance);
            final TextView were = view.findViewById(R.id.dialog_text_were);

            final Button first = view.findViewById(R.id.first_btn);
            final Button second = view.findViewById(R.id.second_btn);

            builder.setView(view);
            final AlertDialog alert = builder.create();

            ClientServer call = new ClientServer();
            call.setOnResponseListener(new BaseAsyncTask.OnResponseListener<Sight[]>() {
                @Override
                public void onSuccess(final Sight[] result) {
                    info.setText(result[0].getTitle());
                    if (sight.getFlag()) {
                        were.setText("Вы тут были");
                        first.setText("Узнать больше");

                        if (mLastKnownLocation != null) {
                            int dist = listManager.calculateDistance(mLastKnownLocation, marker.getPosition());
                            distance.setText(dist + " м");
                        }

                        first.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(context, InfoActivity.class);
                                intent.putExtra("title", result[0].getTitle());
                                intent.putExtra("description", result[0].getDescription());
                                intent.putExtra("uml", result[0].getImg());
                                if (sight.getType() == 1) {
                                    intent.putExtra("button", true);
                                }
                                startActivityForResult(intent,CAMERA_KEY);
                                alert.hide();
                            }
                        });

                    } else {
                        were.setText("Вы тут еще не были");
                        first.setText("Хочу открыть");
                        first.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(MapActivity.this, "Доступно в следующий версиях", Toast.LENGTH_SHORT).show();
                            }
                        });
                        if (mLastKnownLocation != null) {
                            int dist = listManager.calculateDistance(mLastKnownLocation, marker.getPosition());
                            distance.setText(dist + " м");
                        }
                    }
                    alert.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    second.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.hide();
                        }
                    });
                    alert.show();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(MapActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            call.getInfo(id);
        }
        return false;
    }

    public void lookAtMe(View view) {
        Log.d("pos", "нажал на кнопку");
        mLastKnownLocation = locationManager.getDeviceLocation();
        cameraManager.setCameraPosition(mLastKnownLocation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForCrashes();
    }

    @Override
    protected void onPause() {
        super.onPause();
        listManager.clearList();
        unregisterManagers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterManagers();
    }

    private void unregisterManagers() {
        UpdateManager.unregister();
    }

    private void checkForCrashes() {
        CrashManager.register(this);
    }

    public void close_target(View view) {
        try {
            if (mLastKnownLocation != null && cameraManager != null && listManager != null) {
                synchronized (listManager) {
                    cameraManager.setCameraPosition(listManager.getList().get(0).getLocation());
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
        if (requestCode == 34) {
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

