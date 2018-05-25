package com.projectcenterfvt.historicalpenza.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class InternetReceive extends BroadcastReceiver {

    onInternetStatusChange listener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isOnline(context)) {
            Log.d("Broadcast", "Интернет есть");
            listener.onSuccess();
        } else {
            listener.onFailure();
            Log.d("Broadcast", "Интернета нет");
        }

    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setOnInternetStatusChange(onInternetStatusChange listener) {
        this.listener = listener;
    }

    public interface onInternetStatusChange {
        void onSuccess();

        void onFailure();
    }

}
