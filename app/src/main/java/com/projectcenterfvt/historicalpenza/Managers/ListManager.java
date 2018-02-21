package com.projectcenterfvt.historicalpenza.Managers;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.projectcenterfvt.historicalpenza.DataBases.Sight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by roman on 21.02.2018.
 */

public class ListManager {

    private ArrayList<Sight> list = new ArrayList<>();

    public int calculateDistance(Location l1, LatLng l2) {
        Log.d("marker ", "Мое местоположение = " + l1.toString());
        final int R = 6372795;
        double x1 = l1.getLatitude() * Math.PI / 180;
        double x2 = l1.getLongitude() * Math.PI / 180;
        double x3 = l2.latitude * Math.PI / 180;
        double x4 = l2.longitude * Math.PI / 180;
        double res = Math.acos(Math.sin(x1) * Math.sin(x3) + Math.cos(x1) * Math.cos(x3) * Math.cos(x2 - x4)) * R;
        Log.d("marker", "res = " + res);
        return (int) res;
    }

    public void sortList() {
        Log.d("list", "start sort");
        Collections.sort(list, new Comparator<Sight>() {
            @Override
            public int compare(Sight point, Sight t1) {
                return point.getDistance() - t1.getDistance();
            }
        });
        for (int i = 0; i < list.size(); i++) {
            Log.d("list", "dist = " + list.get(i).getDistance());
        }
        Log.d("list", "end sort");
    }

    public void setDistance(Location mLastKnownLocation) {
        if (mLastKnownLocation != null) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setDistance(calculateDistance(mLastKnownLocation, list.get(i).getLocation()));
            }
            sortList();
        }
    }

    public ArrayList<Sight> getList() {
        return list;
    }

    public void setList(ArrayList<Sight> list) {
        this.list = list;
        sortList();
    }

    public void clearList() {
        list.clear();
    }
}
