package com.projectcenterfvt.historicalpenza.DataBases;

import com.google.android.gms.maps.model.LatLng;

/**
 *Класс - объект достопримечательности
 * @author Roman
 * @version 1.0.0
 * @since 1.0.0
 */

public class Sight {
    /**
     * id из бд
     */
    private int id;
    /** Название дост*/
    private String title;
    /** Описание дост*/
    private String description;
    /** Ссылка на изображение*/
    private String img;
    /** Широта*/
    private double latitude;
    /** Долгота*/
    private double longitude;
    /** Открыт или нет*/
    private boolean flag;
    /** Дистанция от объекта до пользователя*/
    private int distance;
    private int type;

    public Sight(int id) {
        this.id = id;
    }

    public Sight(int id, double latitude, double longitude, boolean flag, int type) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.flag = flag;
        this.type = type;
    }

    public Sight() {

    }

    public Sight(int id, String title, String description, String img) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.img = img;
    }

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

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void setFlag() {
        this.flag = flag;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public LatLng getLocation() {
        return new LatLng(latitude, longitude);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setCoordinates(String coordRaw) {
        this.latitude = Double.parseDouble(coordRaw.substring(0, coordRaw.indexOf(",")));
        this.longitude = Double.parseDouble(coordRaw.substring(coordRaw.indexOf(" ") + 1, coordRaw.length() - 1));
    }
}
