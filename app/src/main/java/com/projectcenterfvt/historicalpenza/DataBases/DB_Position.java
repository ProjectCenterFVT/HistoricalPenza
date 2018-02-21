package com.projectcenterfvt.historicalpenza.DataBases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.projectcenterfvt.historicalpenza.Managers.MarkerManager;

import java.util.ArrayList;

/**
 * Created by Roman on 08.12.2017.
 */

public class DB_Position extends SQLiteOpenHelper {

    public static final String DB_TABLE = "coordinates";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_X1 = "x1";
    public static final String COLUMN_X2 = "x2";
    public static final String COLUMN_flag = "flag";
    private static final int DB_VERSION = 1;
    public static String DB_NAME = "coordinatesDb";
    private Context myContext;
    private SQLiteDatabase db;

    public DB_Position(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE coordinates (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " x1 REAL," +
                " x2 REAL," +
                " flag INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists " + DB_TABLE);

        onCreate(db);
    }

    public void connectToRead() {
        db = this.getReadableDatabase();
    }

    public void connectToWrite() {
        db = this.getWritableDatabase();
    }

    public void close() {
        super.close();
    }

    public SQLiteDatabase getDB() {
        return db;
    }

    public Sight getCell(int id) {
        Sight sight = new Sight();
        connectToRead();
        Cursor cursor = db.query(DB_Position.DB_TABLE, new String[]{DB_Position.COLUMN_ID, DB_Position.COLUMN_X1, DB_Position.COLUMN_X2, DB_Position.COLUMN_flag}, null, null, null, null, null);
        cursor.move(id);
        final int id_x1 = cursor.getColumnIndex(COLUMN_X1);
        final int id_x2 = cursor.getColumnIndex(COLUMN_X2);
        double latitude = cursor.getDouble(id_x1);
        double longtitude = cursor.getDouble(id_x2);
        sight.setLatitude(latitude);
        sight.setLongitude(longtitude);
        cursor.close();
        close();
        return sight;
    }

    public ArrayList<Sight> fillArray(GoogleMap map, Location mLastKnownLocation) {
        ArrayList<Sight> list = new ArrayList<>();
        MarkerManager markerManager = new MarkerManager(map, myContext);
        this.connectToRead();
        Cursor cursor = this.db.query(DB_TABLE, new String[]{COLUMN_ID, COLUMN_X1, COLUMN_X2, COLUMN_flag}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            final int id_id = cursor.getColumnIndex(COLUMN_ID);
            final int id_x1 = cursor.getColumnIndex(COLUMN_X1);
            final int id_x2 = cursor.getColumnIndex(COLUMN_X2);
            final int id_flag = cursor.getColumnIndex(COLUMN_flag);

            do {
                Log.d("db ", "проверка");
                int bol = cursor.getInt(id_flag);
                int id = cursor.getInt(id_id);
                boolean isVisited = (bol == 1);
                double x1 = cursor.getDouble(id_x1);
                double x2 = cursor.getDouble(id_x2);
                LatLng position = new LatLng(x1, x2);
                Sight sight = new Sight(id, x1, x2, isVisited);
                if (mLastKnownLocation != null) {
                    sight.setDistance(calculateDistance(mLastKnownLocation, position));
                } else {
                    sight.setDistance(0);
                }
                list.add(sight);
                markerManager.addSightMarker(isVisited, position, list.get(list.size() - 1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        this.close();
        return list;
    }

    private int calculateDistance(Location l1, LatLng l2) {
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
}
