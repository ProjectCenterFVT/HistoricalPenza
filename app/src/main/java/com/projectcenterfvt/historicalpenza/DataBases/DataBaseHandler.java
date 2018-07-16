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

public class DataBaseHandler extends SQLiteOpenHelper implements IDatabaseHandler {

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

    public static final String COLUMN_title = "title";

    public static final String COLUMN_description = "description";

    public static final String COLUMN_img = "img";

    public static final String COLUMN_range = "range";
    /** Версия(Пока не используется, нужно получать от сервера и хранить в бд)*/
    private static final int DB_VERSION = 4;
    /** Имя файла*/
    public static String DB_NAME = "coordinatesDb";
    private final String DB_TAG = "db";
    public int count = 0;
    private SQLiteDatabase db;

    public DataBaseHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Создание БД
     * @param db экземпляр класса <b>SQLiteDatabase </b>для работы с бд
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(DB_TAG, "База данных создана!");
        db.execSQL("CREATE TABLE coordinates (_id INTEGER PRIMARY KEY," +
                " x1 REAL," +
                " x2 REAL," +
                " flag INTEGER," +
                " type INTEGER," +
                " title TEXT," +
                " description TEXT," +
                " img TEXT," +
                " range REAL);");
    }

    /**
     * Обновление БД, пока не используется!
     * @param db экземпляр класса <b>SQLiteDatabase </b>для работы с бд
     * @param oldVersion старая версия
     * @param newVersion новая версия
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(DB_TAG, "База данных обновлена с версии " + oldVersion + " до " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS coordinates;");

        onCreate(db);
    }

    /**
     * Возможность чтения БД
     */
    private void connectToRead() {
        db = this.getReadableDatabase();
    }

    /**
     * Возможность записи БД
     */
    private void connectToWrite() {
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
     * Отрисовка карты в <b>MapActivity</b> и заполнение листа Sight
     * @param map Карта
     * @param mLastKnownLocation Позиция пользователя
     * @return Лист достоприм
     * @see com.projectcenterfvt.historicalpenza.Activity.MapActivity
     */
    @Deprecated
    public ArrayList<Sight> fillArray(GoogleMap map, Location mLastKnownLocation, MarkerManager markerManager) {
        final ArrayList<Sight> list = new ArrayList<>();
        this.connectToRead();
        Cursor cursor = this.db.query(DB_TABLE, new String[]{COLUMN_ID, COLUMN_X1, COLUMN_X2, COLUMN_flag, COLUMN_type, COLUMN_title, COLUMN_description, COLUMN_img, COLUMN_range}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            final int id_id = cursor.getColumnIndex(COLUMN_ID);
            final int id_x1 = cursor.getColumnIndex(COLUMN_X1);
            final int id_x2 = cursor.getColumnIndex(COLUMN_X2);
            final int id_flag = cursor.getColumnIndex(COLUMN_flag);
            final int id_type = cursor.getColumnIndex(COLUMN_type);
            final int id_title = cursor.getColumnIndex(COLUMN_title);
            final int id_description = cursor.getColumnIndex(COLUMN_description);
            final int id_img = cursor.getColumnIndex(COLUMN_img);
            final int id_range = cursor.getColumnIndex(COLUMN_range);
            do {
                int bol = cursor.getInt(id_flag);
                int id = cursor.getInt(id_id);
                int type = cursor.getInt(id_type);
                boolean isVisited = (bol == 1);
                double x1 = cursor.getDouble(id_x1);
                double x2 = cursor.getDouble(id_x2);
                double range = cursor.getDouble(id_range);
                String title = cursor.getString(id_title);
                String description = cursor.getString(id_description);
                String img = cursor.getString(id_img);
                LatLng position = new LatLng(x1, x2);
                Sight sight = new Sight(id, x1, x2, isVisited, type);
                sight.setTitle(title);
                sight.setDescription(description);
                sight.setImg(img);
                sight.setRange(range);
//                if (mLastKnownLocation != null) {
//                    sight.setDistance(calculateDistance(mLastKnownLocation, position));
//                } else {
//                    sight.setDistance(0);
//                }
                list.add(sight);
                // markerManager.addSightMarker(isVisited, type, position, list.get(list.size() - 1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        this.close();
        return list;
    }

    @Override
    public Sight getSight(int id) {
        connectToRead();

        Cursor cursor = db.query(DB_TABLE, new String[]{COLUMN_ID, COLUMN_X1, COLUMN_X2, COLUMN_flag, COLUMN_type,
                        COLUMN_title, COLUMN_description, COLUMN_img, COLUMN_range}, COLUMN_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        Sight sight = new Sight();
        sight.setId(id);
        sight.setLatitude(cursor.getDouble(1));
        sight.setLongitude(cursor.getDouble(2));
        sight.setFlag(cursor.getInt(3) == 1);
        sight.setType(cursor.getInt(4));
        sight.setTitle(cursor.getString(5));
        sight.setDescription(cursor.getString(6));
        sight.setImg(cursor.getString(7));
        sight.setRange(cursor.getDouble(8));

        cursor.close();
        close();

        return sight;
    }

    @Override
    public void addSight(Sight sight) {
        connectToWrite();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, sight.getId());
        contentValues.put(COLUMN_X1, sight.getLatitude());
        contentValues.put(COLUMN_X2, sight.getLongitude());
        contentValues.put(COLUMN_flag, sight.getFlag());
        contentValues.put(COLUMN_type, sight.getType());
        contentValues.put(COLUMN_title, sight.getTitle());
        contentValues.put(COLUMN_description, sight.getDescription());
        contentValues.put(COLUMN_img, sight.getImg());
        contentValues.put(COLUMN_range, sight.getRange());

        db.insert(DB_TABLE, null, contentValues);
        count++;

        close();
    }


    @Override
    public void changeStatus(int id) {
        ContentValues values = new ContentValues();
        connectToWrite();
        values.put(COLUMN_flag, true);
        String selection = "_id = ?";
        String args[] = {"" + id};
        db.update(DB_TABLE, values, selection, args);
        close();
    }

    @Override
    public void deleteAll() {
        connectToWrite();
        db.delete(DB_TABLE, null, null);
        close();
    }

    @Override
    public ArrayList<Sight> getAllSight() {
        connectToRead();

        Cursor cursor = this.db.query(DB_TABLE, new String[]{COLUMN_ID, COLUMN_X1, COLUMN_X2, COLUMN_flag, COLUMN_type, COLUMN_title,
                COLUMN_description, COLUMN_img, COLUMN_range}, null, null, null, null, null);


        ArrayList<Sight> sights = new ArrayList<>();
        cursor.moveToFirst();
        do {
            Sight sight = new Sight();
            sight.setId(cursor.getInt(0));
            sight.setLatitude(cursor.getDouble(1));
            sight.setLongitude(cursor.getDouble(2));
            sight.setFlag(cursor.getInt(3) == 1);
            sight.setType(cursor.getInt(4));
            sight.setTitle(cursor.getString(5));
            sight.setDescription(cursor.getString(6));
            sight.setImg(cursor.getString(7));
            sight.setRange(cursor.getDouble(8));
            sights.add(sight);
        } while (cursor.moveToNext());

        cursor.close();
        close();

        return sights;
    }
}
