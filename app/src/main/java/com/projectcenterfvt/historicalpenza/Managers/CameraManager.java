package com.projectcenterfvt.historicalpenza.Managers;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by roman on 21.02.2018.
 */

public class CameraManager {

    private static int DEFAULT_ZOOM = 9;
    private final LatLng mDefaultLocation = new LatLng(53.204020, 45.012645);
    private Context myContext;
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    public CameraManager(Context myContext, GoogleMap mMap) {
        this.myContext = myContext;
        this.mMap = mMap;
    }

    public void setCameraPosition(Location location) {
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
    }

    public void setCameraPosition(LatLng location) {
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

}
