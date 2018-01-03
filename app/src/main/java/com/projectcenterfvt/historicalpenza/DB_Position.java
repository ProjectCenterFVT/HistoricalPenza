package com.projectcenterfvt.historicalpenza;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Roman on 08.12.2017.
 */

public class DB_Position extends SQLiteOpenHelper {

    public static String DB_NAME = "coordinatesDb";
    private static final int DB_VERSION = 1;

    static final String DB_TABLE = "coordinates";
    static final String COLUMN_ID = "_id";
    static final String COLUMN_X1 = "x1";
    static final String COLUMN_X2 = "x2";
    static final String COLUMN_flag = "flag";

    private Context myContext;
    private SQLiteDatabase db;

    public DB_Position(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.myContext=context;
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

    public void connectToRead (){
       db = this.getReadableDatabase();
    }

    public void connectToWrite(){
        db = this.getWritableDatabase();
    }

    public void close(){
        super.close();
    }

    public SQLiteDatabase getDB(){
        return db;
    }

    public Sight getCell(int id){
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

}
