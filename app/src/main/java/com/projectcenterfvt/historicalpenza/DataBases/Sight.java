package com.projectcenterfvt.historicalpenza.DataBases;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 *Класс - объект достопримечательности
 * @author Roman
 * @version 1.0.0
 * @since 1.0.0
 */

public class Sight implements Parcelable {
    final static String LOG_TAG = "SightLog";
    public static final Creator<Sight> CREATOR = new Creator<Sight>() {
        @Override
        public Sight createFromParcel(Parcel in) {
            Log.d(LOG_TAG, "createFromParcel");
            return new Sight(in);
        }

        @Override
        public Sight[] newArray(int size) {
            return new Sight[size];
        }
    };
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

    public Sight(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    private Sight(Parcel in) {
        Log.d(LOG_TAG, "Конструктир читает данные");
        id = in.readInt();
        title = in.readString();
        description = in.readString();
        img = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        flag = in.readByte() != 0;
        distance = in.readInt();
        type = in.readInt();
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
        String[] coord = coordRaw.split(",");
        this.latitude = Double.parseDouble(coord[0].trim());
        this.longitude = Double.parseDouble(coord[1].trim());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Log.d(LOG_TAG, "writeToParcel");
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(img);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeByte((byte) (flag ? 1 : 0));
        parcel.writeInt(distance);
        parcel.writeInt(type);
    }
}
