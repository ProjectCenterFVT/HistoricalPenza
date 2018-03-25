package com.projectcenterfvt.historicalpenza.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.projectcenterfvt.historicalpenza.Managers.LocationManager;

public class PositionService extends Service {

    private String TAG = "PositionService";
    private LocationManager locationManager;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if ((flags & START_FLAG_RETRY) == 0) {

        } else {
            Log.d(TAG, "Служба запустилась");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {

        super.onCreate();
        Log.d(TAG, "Служба создалась");
        String svcName = Context.LOCATION_SERVICE;
        LocationManager locationManager = (LocationManager) getSystemService(svcName);

    }

    @Override
    public void onDestroy() {
        Log.d("TAG", "Служба остановилась");
        super.onDestroy();
    }
}
