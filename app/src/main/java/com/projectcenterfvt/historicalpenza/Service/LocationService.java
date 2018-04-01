package com.projectcenterfvt.historicalpenza.Service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.projectcenterfvt.historicalpenza.DataBases.DB_Position;
import com.projectcenterfvt.historicalpenza.DataBases.Sight;
import com.projectcenterfvt.historicalpenza.Managers.ListManager;
import com.projectcenterfvt.historicalpenza.Managers.MarkerManager;
import com.projectcenterfvt.historicalpenza.Server.ClientServer;

public class LocationService extends Service implements LocationListener {

    private static final int NOTIFY_ID = 101;
    LocalBinder binder = new LocalBinder();
    private LocationManager locationManager;
    private ListManager listManager;
    private String TAG = "locationManager";
    private String mToken;
    private Context context;
    private MarkerManager markerManager;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setListManager(ListManager listManager) {
        this.listManager = listManager;
    }

    @SuppressLint("MissingPermission")

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 2000, 15, this);
        mToken = intent.getStringExtra("token");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        listManager.sortList();
        setUpNotification();
        try {
            Sight point = listManager.getList().get(0);
            Log.d(TAG, "точка значение - " + point.getFlag());
            if (!point.getFlag() && listManager.isWithinPoint(location, point)) {
                Log.d(TAG, "Точка входит");
                ClientServer setPoint = new ClientServer();
                setPoint.setPlaces(point.getId(), mToken);
                DB_Position db_position = new DB_Position(context);
                db_position.updateColumn(point);
                point.setFlag(true);
                markerManager.refreshMarker(point);
                listManager.refreshSight(point);
                //кинуть метод сюда, я вчера тестирова у меня не получалось
            } else {
                Log.d(TAG, "Точка не входит");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setContext(Context context) {
        this.context = context;
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

    public void setMarkerManager(MarkerManager markerManager) {
        this.markerManager = markerManager;
    }

    private void setUpNotification() {
        //Тут у нас должнен вызываться пуш
//        Intent notificationIntent = new Intent(this, MapActivity.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(this,
//                0, notificationIntent,
//                PendingIntent.FLAG_CANCEL_CURRENT);
//        Notification notification = new Notification();
//        notification.de
//        Log.d(TAG,"Уведомление!");
    }

    public class LocalBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

}
