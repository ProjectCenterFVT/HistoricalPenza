package com.projectcenterfvt.historicalpenza;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.SQLException;

public class ActivityMap extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastKnownLocation;
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static int DEFAULT_ZOOM = 9;
    private CameraPosition mCameraPosition;
    private DB_Position dbPosition;
    private FusedLocationProviderClient flpc;
    private final LatLng mDefaultLocation = new LatLng(53.204020, 45.012645);
    private Button btn_pos;
    private Context context = this;
    private Marker myMarker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        btn_pos = (Button) findViewById(R.id.btn_position);

        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();

        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.name_sight) {

        } else if (id == R.id.name_helpProject) {

        } else if (id == R.id.name_moreSight) {

        } else if (id == R.id.name_settings) {

        } else if (id == R.id.name_help) {

        } else if (id == R.id.name_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(12.0f);
        mMap.setMaxZoomPreference(17.0f);
        getLocationPermission();
        setCameraPosition(mLastKnownLocation);
        getDeviceLocation();
        fillArray(mMap);
        mMap.setOnMarkerClickListener(this);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void updateLocationUI(boolean flag) {
        Log.d("pos", "upadeLoc");
        try {
            if (mLocationPermissionGranted && flag) {
                btn_pos.setVisibility(View.VISIBLE);
                Log.d("position", "visible");
            } else {
                btn_pos.setVisibility(View.INVISIBLE);
                Log.d("position", "invisible");
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.d("pos", e.getMessage());
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("pos", "Смена позиции");
            getDeviceLocation();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.d("pos", "status - " + s + " i = " + i);
        }

        @Override
        public void onProviderEnabled(String s) {
            updateLocationUI(true);
        }

        @Override
        public void onProviderDisabled(String s) {
            updateLocationUI(false);
        }
    };

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("pos", "пользователь дал согласие");
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            Log.d("pos", "пользователь не дал согласие");
        }
    }

    private void getDeviceLocation() {
        if (mLocationPermissionGranted) {
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
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }
        if (mLastKnownLocation!=null) {
            if (myMarker != null) {
                Log.d("marker", "Моя позиция есть, изменяю её");
                myMarker.setPosition(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
            } else {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), getResources().
                        getIdentifier("my_marker","drawable", getPackageName()));
                bitmap = Bitmap.createScaledBitmap(bitmap, 44,70,false);
                Log.d("marker", "Моей позиции нет, делаю позицию");
                myMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap)).title("Я").position(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude())));
            }
            Log.d("myPosition", "Моя позиция - " + mLastKnownLocation.toString());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
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
        DEFAULT_ZOOM = (int) mMap.getCameraPosition().zoom;
    }

    public void minus(View view) {
        mMap.animateCamera(CameraUpdateFactory.zoomBy(-1.0f));
        DEFAULT_ZOOM = (int) mMap.getCameraPosition().zoom;
    }

    private synchronized void openDB() {
        dbPosition = new DB_Position(this);
        dbPosition.import_db();
        if (!dbPosition.isCreate())
            dbPosition.writeDB();
        try {
            dbPosition.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbPosition.DB_geo.close();
    }

    private void fillArray(GoogleMap map) {
        openDB();
        Cursor cursor = dbPosition.DB_geo.query(DB_Position.DB_TABLE, new String[]{DB_Position.COLUMN_ID, DB_Position.COLUMN_NAME, DB_Position.COLUMN_LOC, DB_Position.COLUMN_ISVISITED}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            final int id = cursor.getColumnIndex(dbPosition.COLUMN_ID);
            final int id_name = cursor.getColumnIndex(dbPosition.COLUMN_NAME);
            final int id_loc = cursor.getColumnIndex(dbPosition.COLUMN_LOC);
            final int id_isVisited = cursor.getColumnIndex(dbPosition.COLUMN_ISVISITED);

            do {
                String name = cursor.getString(id_name);
                String[] loc = cursor.getString(id_loc).split(" ");
                int bol = cursor.getInt(id_isVisited);
                boolean isVisited = (bol == 1);
                LatLng position = new LatLng(Double.parseDouble(loc[0]), Double.parseDouble(loc[1]));
                MarkerOptions options = new MarkerOptions();
                options.position(position).title(name).flat(true);
                if (isVisited) {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), getResources().
                            getIdentifier("unlock","drawable", getPackageName()));
                    bitmap = Bitmap.createScaledBitmap(bitmap, 54,70,false);
                    options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                }
                else {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), getResources().
                            getIdentifier("lock","drawable", getPackageName()));
                    bitmap = Bitmap.createScaledBitmap(bitmap, 44,70,false);
                    options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                }
                Marker marker = map.addMarker(options);
                marker.setTag(isVisited);
                Log.d("marker", "нарисовал маркер с координатами " + position);

            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        //не настроил нихуя свое местоположение
        Log.d("marker", "Нажал на маркер " + marker.getId() + " " + marker.getTitle() + " " + marker.getPosition().toString());
        boolean flag = (boolean) marker.getTag();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog, null);
        view.setBackgroundResource(R.drawable.dialog_bgn);
        TextView info = (TextView) view.findViewById(R.id.dialog_text_info);
        TextView distance = (TextView) view.findViewById(R.id.dialog_text_distance);
        Button first = (Button) view.findViewById(R.id.first_btn);
        Button second = (Button) view.findViewById(R.id.second_btn);
        builder.setView(view);
        if (flag) {
            info.setText(marker.getTitle());
            if (mLastKnownLocation != null) {
                int dist = calucateDistance(mLastKnownLocation, marker.getPosition());
                if (dist > 1000.00) {
                    dist = dist / 1000;
                    distance.setText("Расстояние = " + dist + " км");
                } else {
                    distance.setText("Расстояние = " + dist + " м");
                }
            }
            first.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(context, info_activity.class));
                }
            });
        } else {
            info.setText(marker.getTitle()+"\n"+"Вы тут еще не были");
            first.setBackgroundResource(R.drawable.first_btn_clon);
            if (mLastKnownLocation != null) {
                int dist = calucateDistance(mLastKnownLocation, marker.getPosition());
                if (dist > 1000.00) {
                    dist = dist / 1000;
                    distance.setText("Расстояние = " + dist + " км");
                } else {
                    distance.setText("Расстояние = " + dist + " м");
                }
            }
        }
        final AlertDialog alert = builder.create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.hide();
            }
        });
        alert.show();
        return false;
    }

    private int calucateDistance(Location l1, LatLng l2) {
        Log.d("marker ", "Мое местоположение = " + l1.toString());
        final int R = 6372795;
        double x1 = l1.getLatitude() * Math.PI / 180;
        double x2 = l1.getLongitude() * Math.PI / 180;
        double x3 = l2.latitude * Math.PI / 180;
        double x4 = l2.longitude * Math.PI / 180;
        double res = Math.acos(Math.sin(x1) * Math.sin(x3) + Math.cos(x1) * Math.cos(x3) * Math.cos(x2 - x4)) * R;
        Log.d("marker", "res = " + res);
        return (int) res;
    }

    public void lookAtMe(View view) {
        Log.d("pos", "нажал на кнопку");
        getDeviceLocation();
        setCameraPosition(mLastKnownLocation);
    }

    private void setCameraPosition(Location location){
        if (mLocationPermissionGranted) {
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
        }
        // Set the map's camera position to the current location of the device.
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (location != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(),
                            location.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d("TAG", "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
        }
        try {
            Log.d("myPosition", "Моя позиция - " + mLastKnownLocation.toString());
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }
}

