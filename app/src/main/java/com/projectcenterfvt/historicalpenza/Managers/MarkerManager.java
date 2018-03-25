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
 * Работа с маркерами
 * @author Roman
 * @version 1.0.0
 * @since 1.0.0
 * @see com.projectcenterfvt.historicalpenza.Activity.MapActivity
 */

public class MarkerManager {

    private GoogleMap mMap;
    private Context myContext;
    private Marker myMarker;
    private String TAG = "Marker";

    public MarkerManager(GoogleMap mMap, Context myContext) {
        this.mMap = mMap;
        this.myContext = myContext;
    }

    /**
     * Добавление маркера на карту(Маркер достоприм)
     *
     * @param flag     Открыт/неоткрыт
     * @param position Позиция достопримечательности
     * @param sight    Достопримечательность
     */

    public void addSightMarker(Boolean flag, int type, LatLng position, Sight sight) {
        MarkerOptions options = new MarkerOptions();
        options.position(position);
        if (flag) {
            switch (type) {
                case 0:
                    Bitmap bitmap = BitmapFactory.decodeResource(myContext.getResources(), myContext.getResources().
                            getIdentifier("unlock", "drawable", myContext.getPackageName()));
                    bitmap = Bitmap.createScaledBitmap(bitmap, 74, 100, false);
                    options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                    break;
                case 1:
                    Bitmap bitmapHomestead = BitmapFactory.decodeResource(myContext.getResources(), myContext.getResources().
                            getIdentifier("homestead", "drawable", myContext.getPackageName()));
                    bitmap = Bitmap.createScaledBitmap(bitmapHomestead, 74, 100, false);
                    options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                    break;
            }
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(myContext.getResources(), myContext.getResources().
                    getIdentifier("lock", "drawable", myContext.getPackageName()));
            bitmap = Bitmap.createScaledBitmap(bitmap, 62, 100, false);
            options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
        }
        Marker marker = mMap.addMarker(options);
        sight.setType(type);
        marker.setTag(sight);
        Log.d(TAG, "нарисовал маркер с координатами " + position);
    }

    /**
     * Добавленеи маркера местоположения
     * @param location Позиция пользователя
     */
    public void addMyMarker(Location location) {
        if (location != null) {
            Log.d(TAG, "Моя позиция есть, изменяю её");
            myMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            showMyMarker();
        }
    }

    public void removeMyMarker() {
        if (myMarker != null) {
            myMarker.remove();
            Log.d(TAG, "Удалил маркер");
        }
    }

    public void inviseMyMarker() {
        if (myMarker != null) {
            myMarker.setVisible(false);
            Log.d(TAG, "Спрятал маркер");
        }
    }

    public void showMyMarker() {
        if (myMarker != null) {
            myMarker.setVisible(true);
            Log.d(TAG, "Показал маркер");
        }
    }

    public Marker getMyMarker() {
        return myMarker;
    }

    public void addStartMarker() {
        Bitmap bitmap = BitmapFactory.decodeResource(myContext.getResources(), myContext.getResources().
                getIdentifier("my_marker", "drawable", myContext.getPackageName()));
        bitmap = Bitmap.createScaledBitmap(bitmap, 57, 100, false);
        myMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(53.196854, 45.017561)).icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
        inviseMyMarker();
        Log.d(TAG, "Создал стартовый маркер");
    }
}
