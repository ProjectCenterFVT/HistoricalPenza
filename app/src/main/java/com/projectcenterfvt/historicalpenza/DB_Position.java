package com.projectcenterfvt.historicalpenza;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

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

}
