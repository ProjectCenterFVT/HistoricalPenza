package com.projectcenterfvt.historicalpenza.Managers;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
 * Работа с камерой на карте
 * @author Roman
 * @version 1.0.0
 * @since 1.0.0
 * @see com.projectcenterfvt.historicalpenza.Activity.MapActivity
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

    /**
     * Установка позиции камеры на карте
     *
     * @param location Позиция пользователя
     */
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

    /**
     * Установка позиции камеры на карте
     * @param location Позиция пользователя
     */
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
