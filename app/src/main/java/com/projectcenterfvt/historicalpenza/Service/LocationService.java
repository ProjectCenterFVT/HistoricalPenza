package com.projectcenterfvt.historicalpenza.Service;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.projectcenterfvt.historicalpenza.Activity.InfoActivity;
import com.projectcenterfvt.historicalpenza.DataBases.DB_Position;
import com.projectcenterfvt.historicalpenza.DataBases.Sight;
import com.projectcenterfvt.historicalpenza.Managers.ListManager;
import com.projectcenterfvt.historicalpenza.Managers.MarkerManager;
import com.projectcenterfvt.historicalpenza.R;
import com.projectcenterfvt.historicalpenza.Server.BaseAsyncTask;
import com.projectcenterfvt.historicalpenza.Server.ClientServer;
import com.projectcenterfvt.historicalpenza.Server.SetPlacesServer;

public class LocationService extends Service implements LocationListener {

    public static final String APP_PREFERENCES = "account";
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
        try {
            final Sight sight = listManager.getList().get(0);
            Log.d(TAG, "точка значение - " + sight.getFlag());
            if (!sight.getFlag() && listManager.isWithinPoint(location, sight)) {
                Log.d(TAG, "Точка входит");

                SetPlacesServer setPoint = new SetPlacesServer();
                setPoint.setOnResponseListener(new BaseAsyncTask.OnResponseListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        DB_Position db_position = new DB_Position(context);
                        db_position.updateColumn(sight);
                        sight.setFlag(true);
                        markerManager.refreshMarker(sight);
                        listManager.refreshSight(sight);

                        setUpNotification(sight);
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
                setPoint.setPlaces(sight.getId(), mToken);
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

    private void setUpNotification(Sight sight) {
        final Intent resultIntent = new Intent(this, InfoActivity.class);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        final TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        SharedPreferences preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        final boolean checked = preferences.getBoolean("Pref", true);
        ClientServer call = new ClientServer();
        call.setOnResponseListener(new BaseAsyncTask.OnResponseListener<Sight[]>() {
            @Override
            public void onSuccess(final Sight[] result) {
                if (checked) {
                    Sight sight = result[0];

                    resultIntent.putExtra("title", sight.getTitle());
                    resultIntent.putExtra("description", sight.getDescription());
                    resultIntent.putExtra("uml", sight.getImg());
                    if (sight.getType() == 1) {
                        resultIntent.putExtra("button", true);
                    }

                    // Adds the back stack
                    stackBuilder.addParentStack(InfoActivity.class);
                    // Adds the Intent to the top of the stack
                    stackBuilder.addNextIntent(resultIntent);
                    // Gets a PendingIntent containing the entire back stack
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                    builder.setContentIntent(resultPendingIntent)
                            .setContentTitle("Историческая Пенза")
                            .setContentText(sight.getTitle())
                            .setChannelId("channelId")
                            .setSmallIcon(R.drawable.logo_main);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    assert mNotificationManager != null;

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        NotificationChannel notificationChannel =
                                new NotificationChannel(
                                        "channelId",
                                        "channelName",
                                        NotificationManager.IMPORTANCE_DEFAULT);
                        mNotificationManager.createNotificationChannel(notificationChannel);
                    }

                    mNotificationManager.notify(sight.getId(), builder.build());
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
        call.getInfo(sight.getId());
    }

    public class LocalBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

}
