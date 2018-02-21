package com.projectcenterfvt.historicalpenza.Managers;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.projectcenterfvt.historicalpenza.R;

/**
 * Created by roman on 20.02.2018.
 */

public class LocationManager {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Context context;
    private Location mLastKnownLocation;
    private Boolean mLocationPermissionGranted;
    private Activity activity;
    private GoogleApiClient mGoogleApiClient;

    public LocationManager(Context context, Activity activity, GoogleApiClient mGoogleApiClient) {
        this.context = context;
        this.activity = activity;
        this.mGoogleApiClient = mGoogleApiClient;
    }

    public Location getDeviceLocation() {
        Log.d("pos", "" + mLastKnownLocation);
        if (mLocationPermissionGranted) {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }
        return mLastKnownLocation;
    }

    public void updateLocationUI(boolean flag, Button btn_pos) {
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

    public void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(context.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("pos", "пользователь дал согласие");
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            Log.d("pos", "пользователь не дал согласие");
        }
    }

    public Boolean getmLocationPermissionGranted() {
        return mLocationPermissionGranted;
    }
}
