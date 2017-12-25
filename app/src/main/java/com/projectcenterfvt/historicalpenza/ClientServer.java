package com.projectcenterfvt.historicalpenza;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Roman on 15.12.2017.
 */


public class ClientServer extends AsyncTask<String, Void, Sight[]>{

    private Exception mExeption;

    public interface OnResponseListener<T> {
        public void onSuccess(T[] result);
        public void onFailure(Exception e);
    }

    OnResponseListener<Sight> listener;

    private Context context;
    private String server = "http://d95344yu.beget.tech/api/api.request.php";
    private String command;
    byte[] data = null;
    InputStream is = null;
    ClientServer(Context context){
        this.context = context;
    }

    @Override
    protected Sight[] doInBackground(String... from) {
        try {
            command = from[0];
            Log.d("server", command);
            JSONObject myjson = new JSONObject(command);
            URL url = new URL(server);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            OutputStream os = conn.getOutputStream();
            data = command.getBytes("UTF-8");
            Log.d("server","отпралвяю "+ command);
            os.write(data);
            data = null;

            conn.connect();

            int code = conn.getResponseCode();
            Log.d("server", "код = "+code);

            is = conn.getInputStream();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            data = baos.toByteArray();
            String resultString = new String(data, "UTF-8");
            Log.d("server", "result = "+resultString);

            JSONObject jsonResult = new JSONObject(resultString);
            JSONArray jsonArr = jsonResult.getJSONArray("result");
            int size = jsonArr.length();

            Sight[] resultArr = new Sight[size];

            for(int i=0;i<size;i++)
            {
                JSONObject item = jsonArr.getJSONObject(i);

                int id = 1;
                String title = item.getString("title");
                String description = item.getString("description");
                String img = item.getString("img");

                resultArr[i] = new Sight(id, title, description, img);
            }

            return resultArr;

//            if (myjson.has("getCoordinates")) {
//                Log.d("server", "переписываю");
//                writeBD(resultString);
//                return null;
//            }
//            if (myjson.has("getInfo")){
//                return getInfo(resultString);
//            }
//            if (myjson.has("getAllInfo")){
//                return getAllInfo(resultString);
//            }

        } catch (Exception e){
            e.printStackTrace();
            mExeption = e;
            Log.d("server", e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Sight[] result) {
        if (listener != null) {
            if (mExeption == null) {
                listener.onSuccess(result);
            } else {
                listener.onFailure(mExeption);
            }
        }
    }

    public void setOnResponseListener(OnResponseListener<Sight> listener) {
        this.listener = listener;
    }

    private void writeBD(String json){

        DB_Position db = new DB_Position(context);
        SQLiteDatabase databases = db.getWritableDatabase();
        databases.delete(db.DB_TABLE, null, null);
        try {
            JSONObject myjson = new JSONObject(json);
            JSONArray the_json_array = myjson.getJSONArray("result");
            int size = the_json_array.length();
            ArrayList<JSONObject> arrays = new ArrayList<JSONObject>();
            Log.d("server", "size = "+size);
            for(int i=0;i<size;i++)
            {
                ContentValues contentValues = new ContentValues();
                JSONObject another_json_object = the_json_array.getJSONObject(i);
                contentValues.put(db.COLUMN_ID, another_json_object.getInt("_id"));
                Log.d("server", "вствавил  id = "+another_json_object.getInt("_id"));
                String coord = another_json_object.getString("coordinates");
                Log.d("server", "вствавил  x1 = "+Double.parseDouble(coord.substring(0,coord.indexOf(","))));
                contentValues.put(db.COLUMN_X1, Double.parseDouble(coord.substring(0,coord.indexOf(","))));
                Log.d("server", "вствавил  x2 = "+Double.parseDouble(coord.substring(coord.indexOf(" ")+1, coord.length()-1)));
                contentValues.put(db.COLUMN_X2, Double.parseDouble(coord.substring(coord.indexOf(" ")+1, coord.length()-1)));
                contentValues.put(db.COLUMN_flag, another_json_object.getInt("flag"));
                Log.d("server", "вствавил  flag = "+another_json_object.getInt("flag"));
                databases.insert(db.DB_TABLE, null, contentValues);
            }
            db.close();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private ArrayList<String> getInfo(String json){
        ArrayList<String> cache = new ArrayList<>();
        try {
            JSONObject myjson = new JSONObject(json);
            JSONArray the_json_array = myjson.getJSONArray("result");
            JSONObject another_json_object = the_json_array.getJSONObject(0);
            cache.add(another_json_object.getString("title"));
            Log.d("server", "добавил "+another_json_object.getString("title"));
            cache.add(another_json_object.getString("description"));
            Log.d("server", "добавил "+another_json_object.getString("description"));
            cache.add(another_json_object.getString("img"));
            Log.d("server", "добавил "+another_json_object.getString("img"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cache;
    }

    private ArrayList<String> getAllInfo(String json){
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            JSONObject myjson = new JSONObject(json);
            JSONArray the_json_array = myjson.getJSONArray("result");
            int size = the_json_array.length();
            for(int i=0;i<size;i++)
            {
                JSONObject another_json_object = the_json_array.getJSONObject(i);
                arrayList.add(another_json_object.getString("title"));
                Log.d("server", "добавил "+another_json_object.getString("title"));
            }
            Log.d("server", "список - "+arrayList.get(size-1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }
}
