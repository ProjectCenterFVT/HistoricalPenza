package com.projectcenterfvt.historicalpenza.DataBases;

import android.content.ContentValues;
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
 * База данных
 * !!!! Нужно сделать обновление дб, а не перетирание
 * @author Roman, Dmitry
 * @version 1.0.0
 * @since 1.0.0
 */

public class DB_Position extends SQLiteOpenHelper {

    /**
     * Имя таблицы
     */
    public static final String DB_TABLE = "coordinates";
    /** ID*/
    public static final String COLUMN_ID = "_id";
    /** Широта*/
    public static final String COLUMN_X1 = "x1";
    /** Долгота*/
    public static final String COLUMN_X2 = "x2";
    /** Открыт или неоткрыт*/
    public static final String COLUMN_flag = "flag";

    public static final String COLUMN_type = "type";
    /** Версия(Пока не используется, нужно получать от сервера и хранить в бд)*/
    private static final int DB_VERSION = 1;
    /** Имя файла*/
    public static String DB_NAME = "coordinatesDb";
    private Context myContext;
    private SQLiteDatabase db;

    public DB_Position(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;
    }

    /**
     * Создание БД
     * @param db экземпляр класса <b>SQLiteDatabase </b>для работы с бд
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE coordinates (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " x1 REAL," +
                " x2 REAL," +
                " flag INTEGER," +
                " type INTEGER);");
    }

    /**
     * Обновление БД, пока не используется!
     * @param db экземпляр класса <b>SQLiteDatabase </b>для работы с бд
     * @param i старая версия
     * @param i1 новая версия
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists " + DB_TABLE);

        onCreate(db);
    }

    /**
     * Возможность чтения БД
     */
    public void connectToRead() {
        db = this.getReadableDatabase();
    }

    /**
     * Возможность записи БД
     */
    public void connectToWrite() {
        db = this.getWritableDatabase();
    }

    /**
     * Закрыть чтение и запись БД
     */
    public void close() {
        super.close();
    }

    public SQLiteDatabase getDB() {
        return db;
    }

    /**
     * Получение строки из БД по id
     * @param id id
     * @return объект Sight
     */
    public Sight getCell(int id) {
        Sight sight = new Sight();
        connectToRead();
        Cursor cursor = db.query(DB_Position.DB_TABLE, new String[]{DB_Position.COLUMN_ID, DB_Position.COLUMN_X1, DB_Position.COLUMN_X2, DB_Position.COLUMN_flag}, null, null, null, null, null);
        cursor.move(id);
        final int id_x1 = cursor.getColumnIndex(COLUMN_X1);
        final int id_x2 = cursor.getColumnIndex(COLUMN_X2);
        final int id_flag = cursor.getColumnIndex(COLUMN_type);
        double latitude = cursor.getDouble(id_x1);
        double longtitude = cursor.getDouble(id_x2);
        sight.setLatitude(latitude);
        sight.setLongitude(longtitude);
        cursor.close();
        close();
        return sight;
    }

    /**
     * Отрисовка карты в <b>MapActivity</b> и заполнение листа Sight
     * @param map Карта
     * @param mLastKnownLocation Позиция пользователя
     * @return Лист достоприм
     * @see com.projectcenterfvt.historicalpenza.Activity.MapActivity
     */
    public ArrayList<Sight> fillArray(GoogleMap map, Location mLastKnownLocation, MarkerManager markerManager) {
        ArrayList<Sight> list = new ArrayList<>();
        this.connectToRead();
        Cursor cursor = this.db.query(DB_TABLE, new String[]{COLUMN_ID, COLUMN_X1, COLUMN_X2, COLUMN_flag, COLUMN_type}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            final int id_id = cursor.getColumnIndex(COLUMN_ID);
            final int id_x1 = cursor.getColumnIndex(COLUMN_X1);
            final int id_x2 = cursor.getColumnIndex(COLUMN_X2);
            final int id_flag = cursor.getColumnIndex(COLUMN_flag);
            final int id_type = cursor.getColumnIndex(COLUMN_type);
            do {
                Log.d("db ", "проверка");
                int bol = cursor.getInt(id_flag);
                int id = cursor.getInt(id_id);
                int type = cursor.getInt(id_type);
                boolean isVisited = (bol == 1);
                double x1 = cursor.getDouble(id_x1);
                double x2 = cursor.getDouble(id_x2);
                LatLng position = new LatLng(x1, x2);
                Sight sight = new Sight(id, x1, x2, isVisited, type);
                if (mLastKnownLocation != null) {
                    sight.setDistance(calculateDistance(mLastKnownLocation, position));
                } else {
                    sight.setDistance(0);
                }
                list.add(sight);
                markerManager.addSightMarker(isVisited, type, position, list.get(list.size() - 1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        this.close();
        return list;
    }

    /**
     * Расчет дистанции(Можно нагрузить на сервер, а можно оставить на клиенте)
     * @param l1 Позиция пользователя
     * @param l2 Позиция объекта
     * @return Расстояние
     */
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

    public void updateColumn(Sight sight) {
        ContentValues values = new ContentValues();
        connectToWrite();
        values.put(COLUMN_flag, true);
        String selection = "_id = ?";
        String args[] = {"" + sight.getId()};
        db.update(DB_TABLE, values, selection, args);
        this.close();
    }
}
