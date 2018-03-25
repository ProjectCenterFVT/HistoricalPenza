package com.projectcenterfvt.historicalpenza.Service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.projectcenterfvt.historicalpenza.DataBases.Sight;
import com.projectcenterfvt.historicalpenza.Managers.ListManager;

import java.util.ArrayList;
import java.util.Arrays;

public class LocationService extends Service implements LocationListener {

    private LocationManager locationManager;
    private ArrayList<Sight> list;
    private ListManager listManager;
    private String TAG = "locationManager";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listManager = new ListManager();
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);
        list = intent.getParcelableArrayListExtra("list");
        if (list != null)
            Log.d(TAG, "Получил лист - " + Arrays.toString(list.toArray()));
        else
            Log.d(TAG, "list - null");
        listManager.setList(list);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        listManager.sortList();
        Sight point = listManager.getList().get(0);
        if (listManager.isWithinPoint(location, point)) {
            Log.d(TAG, "Точка входит");
        } else {
            Log.d(TAG, "Точка не входит");
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
