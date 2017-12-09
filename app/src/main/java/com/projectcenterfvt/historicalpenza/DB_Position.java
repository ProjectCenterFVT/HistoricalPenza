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

    public static String DB_NAME_ID = "position.db";
    private static String DB_PATH = null;
    private static final int DB_VERSION = 1;

    static final String DB_TABLE = "pos";
    static final String COLUMN_ID = "_id";
    static final String COLUMN_NAME = "name";
    static final String COLUMN_LOC = "location";
    static final String COLUMN_ISVISITED = "isVisited";

    public SQLiteDatabase DB_geo;
    private Context myContext;
    private File file;


    public DB_Position(Context context) {
        super(context, DB_NAME_ID, null, DB_VERSION);
        this.myContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void import_db (){
        Log.d("geo_db"," Захожу в метод импорта ");
        DB_PATH="//data/data/"+myContext.getPackageName()+"/"+"databases/";
        Log.d("geo_db"," путь : "+DB_PATH+DB_NAME_ID);
        file= new File (DB_PATH+DB_NAME_ID);
    }
    public void open() throws SQLException {
        Log.d("geo_db","открываю базу данных");
        String path = DB_PATH + DB_NAME_ID;
        DB_geo = SQLiteDatabase.openDatabase(path, null,
                SQLiteDatabase.OPEN_READWRITE);
        Log.d("geo_db","открыл базу данных");
    }
    @Override
    public synchronized void close() {
        if (DB_geo != null) {
            DB_geo.close();
        }
        super.close();
    }
    public void writeDB(){
        try {
            Log.d("geo_db", "записываю файл");
            this.getReadableDatabase();
            InputStream Input = myContext.getAssets().open(DB_NAME_ID);
            OutputStream Output = new FileOutputStream(file);
            byte[] buffer = new byte[Input.available()];
            Input.read(buffer, 0, buffer.length);
            Output.write(buffer, 0, buffer.length);
            Output.flush();
            Output.close();
            Input.close();
            Log.d("geo_db", "закончил запись");
        }catch(IOException e){
            Log.d("geo_db", e.toString());
        }
    }

    public void deleteDB(){

    }

    public boolean isCreate(){
        Log.d("geo_db","размер файла = "+file.length());
        if (file.length()!=0)
            return true;
        else return false;
    }
}
