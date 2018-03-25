package com.projectcenterfvt.historicalpenza.Managers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.projectcenterfvt.historicalpenza.R;

/**
 * Работа с местоположением пользователя
 * @author Roman
 * @version 1.0.0
 * @since 1.0.0
 * @see com.projectcenterfvt.historicalpenza.Activity.MapActivity
 */

public class LocationManager {

    private Context context;
    private Location mLastKnownLocation;
    private Activity activity;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private String TAG = "Location";
    private String TAG_SERVICE = "TagService";
    private MarkerManager markerManager;

    private ListManager listManager;

    public LocationManager(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        createLocationRequest();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d(TAG, "нет позиции");
                }
                for (Location location : locationResult.getLocations()) {
                    mLastKnownLocation = location;
                    if (markerManager != null) {
                        markerManager.addMyMarker(location);
                    }
                    if (listManager != null) {
                        listManager.setDistance(mLastKnownLocation);
                    }
                    Log.d(TAG, "Смена позиции");
                }
            }
        };
        startLocationUpdates();
        getLocation();
    }

    public void startLocationUpdate() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d(TAG_SERVICE, "нет позиции");
                }
                for (Location location : locationResult.getLocations()) {
                    mLastKnownLocation = location;
                    Log.d(TAG_SERVICE, "Служба обновляет данные : " + location.toString());
                }
            }
        };
        startLocationUpdates();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);
    }

    /**
     * Получение местоположения пользователя
     *
     * @return Местоположение пользователя
     */
    public Location getDeviceLocation() {
        getLocation();
        return mLastKnownLocation;
    }

    public void setMarkerManager(MarkerManager markerManager) {
        this.markerManager = markerManager;
    }

    public void setListManager(ListManager listManager) {
        this.listManager = listManager;
    }

    @SuppressLint("MissingPermission")
    public void getLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(activity, new OnCompleteListener<Location>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastKnownLocation = task.getResult();
                            Log.d(TAG, "Моя позиция = " + mLastKnownLocation.toString());
                        } else {
                            Log.d(TAG, "Ошибка");
                        }
                    }
                });
    }

    /**
     * Обновление UI
     * @param flag Доступность GPS or NETWORK
     * @param btn_pos Кнопка местоположения
     */
    public void updateLocationUI(boolean flag, Button btn_pos) {
        Log.d("pos", "upadeLoc");
        try {
            if (flag) {
                btn_pos.setEnabled(true);
                btn_pos.setBackgroundResource(R.drawable.get_location);
                Log.d("position", "visible");
            } else {
                btn_pos.setEnabled(false);
                btn_pos.setBackgroundResource(R.drawable.my_pos_un);
                Log.d("position", "invisible");
                mLastKnownLocation = null;
            }
        } catch (SecurityException e) {
            Log.d("pos", e.getMessage());
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Проверка разрешения пользователя
     */

    public boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    public void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                34);
    }

}
