package com.projectcenterfvt.historicalpenza.DataBases;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by roman on 11.07.2018.
 */

public class DSightHandler {

    private ArrayList<DSight> list;
    private Context context;
    private String DB_TAG = "DS_Handler";

    public DSightHandler(Context context) {
        list = new ArrayList<>();
        this.context = context;
        fillArray();
    }

    public static int calculateDistance(Location l1, LatLng l2) {
        final int R = 6372795;
        double x1 = l1.getLatitude() * Math.PI / 180;
        double x2 = l1.getLongitude() * Math.PI / 180;
        double x3 = l2.latitude * Math.PI / 180;
        double x4 = l2.longitude * Math.PI / 180;
        double res = Math.acos(Math.sin(x1) * Math.sin(x3) + Math.cos(x1) * Math.cos(x3) * Math.cos(x2 - x4)) * R;
        return (int) res;
    }

    public static int calculateDistance(Location l1, double lat, double lon) {
        final int R = 6372795;
        double x1 = l1.getLatitude() * Math.PI / 180;
        double x2 = l1.getLongitude() * Math.PI / 180;
        double x3 = lat * Math.PI / 180;
        double x4 = lon * Math.PI / 180;
        double res = Math.acos(Math.sin(x1) * Math.sin(x3) + Math.cos(x1) * Math.cos(x3) * Math.cos(x2 - x4)) * R;
        return (int) res;
    }

    public void fillArray() {
        DataBaseHandler dataBaseHandler = new DataBaseHandler(context);
        ArrayList<Sight> sights = dataBaseHandler.getAllSight();
        for (Sight sight : sights) {
            DSight dSight = new DSight(sight.getId(), sight.getLocation());
            list.add(dSight);
        }
    }

    public void sortList(Location location) {
        if (location!=null) {
            for (int i = 0; i < list.size(); i++) {
                DSight dSight = list.get(i);
                dSight.setDistance(calculateDistance(location, dSight.getLocation()));
            }
            Collections.sort(list, new Comparator<DSight>() {
                @Override
                public int compare(DSight d0, DSight d1) {
                    return d0.getDistance() - d1.getDistance();
                }
            });
        }
    }

    public LatLng getCloseLocation() {
        return list.get(0).getLocation();
    }

    public DSight getCloseSight() {
        return list.get(0);
    }

    public boolean isWithinPoint(Location mLocation, Sight point) {
        int distance = calculateDistance(mLocation, point.getLocation());
        return distance <= point.getRange();
    }

}
