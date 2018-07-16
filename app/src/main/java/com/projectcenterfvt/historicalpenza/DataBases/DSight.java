package com.projectcenterfvt.historicalpenza.DataBases;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by roman on 11.07.2018.
 */

public class DSight {

    private int id;
    private LatLng location;
    private int distance;

    public DSight(int id, LatLng location) {
        this.id = id;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
