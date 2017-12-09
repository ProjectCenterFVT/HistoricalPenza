package com.projectcenterfvt.historicalpenza;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Roman on 09.12.2017.
 */

public class Marker_info {

    static LatLng position;
    static String name;
    static String history;
    static boolean isVisited;

    Marker_info(long x1, long x2){
        position = new LatLng(x1,x2);
    }



}
