package com.projectcenterfvt.historicalpenza;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
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

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
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

import com.arlib.floatingsearchview.FloatingSearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ActivityMap extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, GoogleMap.OnMarkerClickListener, GoogleMap.InfoWindowAdapter, Card_dialog.onEventListener {

    @Override
    public void setPosition(LatLng loc) {
        setCameraPosition(loc);
    }

    class Point {
        int id;
        LatLng location;
        int distance;
        int flag;
        String name;

        Point(int id, LatLng loc, int distance, int flag) {
            this.id = id;
            this.location = loc;
            this.distance = distance;
            this.flag = flag;
        }
    }

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
    private DrawerLayout mDrawerLayout;

    private FloatingSearchView searchView;
    private String lastQuery = "";
    public static final long FIND_SUGGESTION_SIMULATED_DELAY = 250;

    private ArrayList<Point> list = new ArrayList<>();
    private ArrayList<Point> searchList = new ArrayList<>();

    public final static String LOG_SEARCH = "searchView";


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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 4, locationListener);

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        NavigationView navigationView1 = (NavigationView) findViewById(R.id.navigation_drawer_bottom);
        navigationView1.setNavigationItemSelectedListener(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        searchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        searchView.attachNavigationDrawerToMenuButton(mDrawerLayout);

        setupSearch();

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


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.name_sight) {
            ClientServer call = new ClientServer(this);
            call.setOnResponseListener(new ClientServer.OnResponseListener<Sight>() {
                @Override
                public void onSuccess(Sight[] result) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    Card_dialog card_dialog = new Card_dialog();

                    for (int i = 0; i < result.length; i++) {
                        ActivityMap.Point point = list.get(i);
                        int id = point.id - 1;
                        point.name = result[id].title;
                        list.set(i, point);
                    }

                    card_dialog.setList(list);
                    card_dialog.show(fragmentManager, "dialog");
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(ActivityMap.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
            About_Dialog dialog = new About_Dialog();
            dialog.show(fragmentManager, "dialog");
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
        mMap.setOnMarkerClickListener(this);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setInfoWindowAdapter(this);
        getLocationPermission();
        getDeviceLocation();
        setCameraPosition(mLastKnownLocation);
        fillArray(mMap);
        setDistance();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void updateLocationUI(boolean flag) {
        Log.d("pos", "upadeLoc");
        try {
            if (mLocationPermissionGranted && flag) {
                btn_pos.setBackgroundResource(R.drawable.get_location);
                Log.d("position", "visible");
            } else {
                btn_pos.setBackgroundResource(R.drawable.my_pos_un);
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
            if (myMarker!=null && mLastKnownLocation!=null) {
                myMarker.setPosition(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
            }
            getDeviceLocation();
            setDistance();
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
        if (mLastKnownLocation != null) {
            if (myMarker != null) {
                Log.d("myPosition", "Моя позиция есть, изменяю её");
                myMarker.setPosition(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
            } else {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), getResources().
                        getIdentifier("my_marker", "drawable", getPackageName()));
                bitmap = Bitmap.createScaledBitmap(bitmap, 57, 100, false);
                Log.d("myPosition", "Моей позиции нет, делаю позицию");
                myMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap)).position(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude())));
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
    }

    public void minus(View view) {
        mMap.animateCamera(CameraUpdateFactory.zoomBy(-1.0f));
    }

    private void fillArray(GoogleMap map) {
        DB_Position db = new DB_Position(context);
        SQLiteDatabase databases = db.getReadableDatabase();
        Cursor cursor = databases.query(DB_Position.DB_TABLE, new String[]{DB_Position.COLUMN_ID, DB_Position.COLUMN_X1, DB_Position.COLUMN_X2, DB_Position.COLUMN_flag}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            final int id_id = cursor.getColumnIndex(dbPosition.COLUMN_ID);
            final int id_x1 = cursor.getColumnIndex(dbPosition.COLUMN_X1);
            final int id_x2 = cursor.getColumnIndex(dbPosition.COLUMN_X2);
            final int id_flag = cursor.getColumnIndex(dbPosition.COLUMN_flag);

           do {
               Log.d("db ", "проверка");
                int bol = cursor.getInt(id_flag);
                int id = cursor.getInt(id_id);
                boolean isVisited = (bol == 1);
                double x1 = cursor.getDouble(id_x1);
                double x2 = cursor.getDouble(id_x2);
                LatLng position = new LatLng(x1, x2);
                MarkerOptions options = new MarkerOptions();
                options.position(position);
                if (mLastKnownLocation != null) {
                    list.add(new Point(id, position, calculateDistance(mLastKnownLocation, position), bol));
                    searchList.add(new Point(id, position, calculateDistance(mLastKnownLocation, position), bol));

                } else {
                    list.add(new Point(id, position,0,bol));
                    searchList.add(new Point(id, position, calculateDistance(mLastKnownLocation, position), bol));

                }
                if (isVisited) {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), getResources().
                            getIdentifier("unlock", "drawable", getPackageName()));
                    bitmap = Bitmap.createScaledBitmap(bitmap, 74, 100, false);
                   options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                } else {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), getResources().
                            getIdentifier("lock", "drawable", getPackageName()));
                    bitmap = Bitmap.createScaledBitmap(bitmap, 62, 100, false);
                    options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                }
                Marker marker = map.addMarker(options);
                marker.setTag(list.get(list.size()-1));
                Log.d("marker", "нарисовал маркер с координатами " + position);

            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        Log.d("marker", "Нажал на маркер " + marker.getId() + " " + marker.getTitle() + " " + marker.getPosition().toString());
        if (marker.getTag() != null) {
            final Point point = (Point) marker.getTag();
            final int id = point.id;

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final LayoutInflater inflater = this.getLayoutInflater();
            final View view = inflater.inflate(R.layout.dialog, null);
            view.setBackgroundResource(R.drawable.dialog_bgn);

            final TextView info = (TextView) view.findViewById(R.id.dialog_text_info);
            final TextView distance = (TextView) view.findViewById(R.id.dialog_text_distance);
            final TextView were = (TextView) view.findViewById(R.id.dialog_text_were);

            final Button first = (Button) view.findViewById(R.id.first_btn);
            final Button second = (Button) view.findViewById(R.id.second_btn);

            builder.setView(view);
            final AlertDialog alert = builder.create();

            ClientServer call = new ClientServer(this);
            call.setOnResponseListener(new ClientServer.OnResponseListener<Sight>() {
                @Override
                public void onSuccess(final Sight[] result) {
                    info.setText(result[0].title);
                    if (point.flag==1) {
                        were.setText("Вы тут были");
                        first.setText("Узнать больше");

                        if (mLastKnownLocation != null) {
                            int dist = calculateDistance(mLastKnownLocation, marker.getPosition());
                            distance.setText(dist + " м");
                        }

                        first.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(context, info_activity.class);
                                intent.putExtra("title", result[0].title);
                                intent.putExtra("description", result[0].description);
                                intent.putExtra("uml", result[0].img);
                                startActivity(intent);
                                alert.hide();
                            }
                        });

                    } else {
                        were.setText("Вы тут еще не были");
                        first.setText("Хочу открыть");
                        first.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(ActivityMap.this, "Доступно в следующий версиях", Toast.LENGTH_SHORT).show();
                            }
                        });
                        if (mLastKnownLocation != null) {
                            int dist = calculateDistance(mLastKnownLocation, marker.getPosition());
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
                    Toast.makeText(ActivityMap.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            call.getInfo(id);
        }
        return false;
    }

    private int calculateDistance(Location l1, LatLng l2) {
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

    private void setCameraPosition(Location location) {
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
                            location.getLongitude()), mMap.getCameraPosition().zoom));
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

    private void setCameraPosition(LatLng location) {
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
        synchronized (location) {
            if (mCameraPosition != null) {
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
            } else if (location.latitude != 0) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.latitude,
                                location.longitude), 16.0f));
            } else {
                Log.d("TAG", "Current location is null. Using defaults.");
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        list.clear();
    }

    @Override
    public View getInfoWindow(Marker marker) {
//        if (marker.getTag()!=null) {
//            int badge;
//            boolean flag = (boolean) marker.getTag();
//            if (flag) {
//                badge = R.drawable.info_unlock;
//            } else {
//                badge = R.drawable.info_lock;
//            }
//        }
            return null;
        }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private void sortList() {
        Log.d("list", "start sort");
        Collections.sort(list, new Comparator<Point>() {
            @Override
            public int compare(Point point, Point t1) {
                return point.distance - t1.distance;
            }
        });
        for (int i = 0; i < list.size(); i++) {
            Log.d("list", "dist = " + list.get(i).distance);
        }
    }

    private void setDistance(){
        if (mLastKnownLocation != null) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).distance = calculateDistance(mLastKnownLocation, list.get(i).location);
            }
            sortList();
        }
    }

    public void close_target(View view) {
        if (mLastKnownLocation!=null){
            setCameraPosition(list.get(0).location);
        }
    }

    private void setupSearch() {
        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {

            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                if (!oldQuery.equals("") && newQuery.equals("")) {
                    searchView.clearSuggestions();
                } else {

                    //this shows the top left circular progress
                    //you can call it where ever you want, but
                    //it makes sense to do it when loading something in
                    //the background.
                    searchView.showProgress();

                    //simulates a query call to a data source
                    //with a new query.
                    DataHelper.findSuggestions(this, newQuery, 5,
                            FIND_SUGGESTION_SIMULATED_DELAY, new DataHelper.OnFindSuggestionsListener() {

                                @Override
                                public void onResults(List<PlaceSuggestion> results) {

                                    //this will swap the data and
                                    //render the collapse/expand animations as necessary
                                    searchView.swapSuggestions(results);

                                    //let the users know that the background
                                    //process has completed
                                    searchView.hideProgress();
                                }
                            });
                }

                Log.d(LOG_SEARCH, "onSearchTextChanged()");
            }
        });

        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {


                PlaceSuggestion placeSuggestion = (PlaceSuggestion) searchSuggestion;
                int id = placeSuggestion.getId();
//                DataHelper.findSuggestions(this, PlaceSuggestion.getBody(),
//                        new DataHelper.OnFindColorsListener() {
//
//                            @Override
//                            public void onResults(List<PlaceSuggestion> results) {
//                                //show search results
//                            }
//
//                        });
                Log.d(LOG_SEARCH, "onSuggestionClicked()");
                setCameraPosition(searchList.get(id).location);

                lastQuery = searchSuggestion.getBody();
                searchView.setSearchBarTitle(lastQuery);
                searchView.clearSuggestions();
            }

            @Override
            public void onSearchAction(String query) {
                lastQuery = query;

//                DataHelper.findColors(getActivity(), query,
//                        new DataHelper.OnFindColorsListener() {
//
//                            @Override
//                            public void onResults(List<ColorWrapper> results) {
//                                //show search results
//                            }
//
//                        });
                Log.d(LOG_SEARCH, "onSearchAction()");
                Toast.makeText(ActivityMap.this, "onSearchAction()", Toast.LENGTH_SHORT).show();
            }
        });

        searchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
                ArrayList <String> list = new ArrayList();
                ArrayList<PlaceSuggestion> placeSuggestionArrayList = new ArrayList<>();
                ClientServer call = new ClientServer(getApplicationContext());
                call.execute("{\"getAllInfo\":\"1\"}");
                try {
                    list = call.get();
                    for (int i=0;i<list.size();i++) {
                        placeSuggestionArrayList.add(new PlaceSuggestion((i),list.get(i)));
                    }
                    DataHelper.setsPlaceSuggestions(placeSuggestionArrayList);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                //show suggestions when search bar gains focus (typically history suggestions)
                searchView.swapSuggestions(DataHelper.getHistory(this, 3));

                Log.d(LOG_SEARCH, "onFocus()");
            }

            @Override
            public void onFocusCleared() {

                //set the title of the bar so that when focus is returned a new query begins
                searchView.setSearchBarTitle("Поиск по городу");

                //you can also set setSearchText(...) to make keep the query there when not focused and when focus returns
                //mSearchView.setSearchText(searchSuggestion.getBody());

                Log.d(LOG_SEARCH, "onFocusCleared()");
            }
        });
    }

}

