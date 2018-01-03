package com.projectcenterfvt.historicalpenza;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Dmitry on 25.12.2017.
 */

public class Sight {
    private int id;
    private String title;
    private String description;
    private String img;
    private double latitude;
    private double longitude;
    private boolean flag;
    private int distance;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean getFlag() {
        return flag;
    }
    
    public void setFlag(){ this.flag = flag;}

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public LatLng getLocation(){
        return new LatLng(latitude,longitude);
    }


    Sight(int id) {
        this.id = id;
    }

    Sight(int id, String title, String description, String img) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.img = img;
    }
    Sight(int id, double latitude, double longitude, boolean flag){
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.flag = flag;
    }

    Sight(){

    }

    public void setCoordinates(String coordRaw) {
        this.latitude = Double.parseDouble(coordRaw.substring(0, coordRaw.indexOf(",")));
        this.longitude = Double.parseDouble(coordRaw.substring(coordRaw.indexOf(" ")+1, coordRaw.length()-1));
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
