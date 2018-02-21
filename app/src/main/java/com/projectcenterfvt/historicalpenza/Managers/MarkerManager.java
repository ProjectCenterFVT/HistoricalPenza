package com.projectcenterfvt.historicalpenza.Managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.projectcenterfvt.historicalpenza.DataBases.Sight;

/**
 * Created by roman on 21.02.2018.
 */

public class MarkerManager {

    private GoogleMap mMap;
    private Context myContext;
    private Marker myMarker = null;

    public MarkerManager(GoogleMap mMap, Context myContext) {
        this.mMap = mMap;
        this.myContext = myContext;
    }

    public void addSightMarker(Boolean flag, LatLng position, Sight sight) {
        MarkerOptions options = new MarkerOptions();
        options.position(position);
        if (flag) {
            Bitmap bitmap = BitmapFactory.decodeResource(myContext.getResources(), myContext.getResources().
                    getIdentifier("unlock", "drawable", myContext.getPackageName()));
            bitmap = Bitmap.createScaledBitmap(bitmap, 74, 100, false);
            options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(myContext.getResources(), myContext.getResources().
                    getIdentifier("lock", "drawable", myContext.getPackageName()));
            bitmap = Bitmap.createScaledBitmap(bitmap, 62, 100, false);
            options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
        }
        Marker marker = mMap.addMarker(options);
        marker.setTag(sight);
        Log.d("marker", "нарисовал маркер с координатами " + position);
    }

    public void addMyMarker(Location location) {
        if (location != null) {
            if (myMarker != null) {
                Log.d("pos", "Моя позиция есть, изменяю её");
                myMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            } else {
                Bitmap bitmap = BitmapFactory.decodeResource(myContext.getResources(), myContext.getResources().
                        getIdentifier("my_marker", "drawable", myContext.getPackageName()));
                bitmap = Bitmap.createScaledBitmap(bitmap, 57, 100, false);
                Log.d("pos", "Моей позиции нет, делаю позицию");
                myMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap)).position(new LatLng(location.getLatitude(), location.getLongitude())));
            }
            Log.d("pos", "Моя позиция - " + location.toString());
        }
    }

    public Marker getMyMarker() {
        return myMarker;
    }
}
