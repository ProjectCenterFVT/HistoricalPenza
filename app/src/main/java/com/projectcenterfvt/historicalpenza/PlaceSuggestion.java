package com.projectcenterfvt.historicalpenza;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Dmitry on 22.12.2017.
 */

public class PlaceSuggestion implements SearchSuggestion {

    public static final Creator<PlaceSuggestion> CREATOR = new Creator<PlaceSuggestion>() {
        @Override
        public PlaceSuggestion createFromParcel(Parcel in) {
            return new PlaceSuggestion(in);
        }

        @Override
        public PlaceSuggestion[] newArray(int size) {
            return new PlaceSuggestion[size];
        }
    };
    private boolean isHistory = false;
    private String placeName;
    private int id;
    private Marker marker;


    private LatLng location;

    public PlaceSuggestion(int id, String suggestion, Marker marker, double latitude, double longitude) {
        this.placeName = suggestion.toLowerCase();
        this.id = id;
        this.marker = marker;
        location = new LatLng(latitude, longitude);

    }

    public PlaceSuggestion(Parcel source) {
        this.placeName = source.readString();
        this.isHistory = source.readInt() != 0;
    }

    public Marker getMarker() {
        return marker;
    }

    public int getId() {
        return id;
    }

    public LatLng getLocation() {
        return location;
    }

    @Override
    public String getBody() {
        return placeName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(placeName);
        dest.writeInt(isHistory ? 1 : 0);
    }

    public boolean getIsHistory() {
        return this.isHistory;
    }

    public void setIsHistory(boolean isHistory) {
        this.isHistory = isHistory;
    }

}
