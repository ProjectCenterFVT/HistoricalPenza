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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Roman on 15.12.2017.
 */


public class ClientServer extends AsyncTask<String, Void, Sight[]>{

    private int id;
    private Exception mException;
    OnResponseListener<Sight> listener;

    public void getInfo(int id) {
        this.execute("{\"getInfo\":\""+id+"\"}");
        this.id = id;
    }

    public void getAllInfo() {
        this.execute("{\"getAllInfo\":\"1\"}");
    }

    public void getCoordinates() {
        this.execute("{\"getCoordinates\":\"0.0.0\"}");
    }

    public interface OnResponseListener<T> {
        public void onSuccess(T[] result);
        public void onFailure(Exception e);
    }

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

            JSONObject myJson = new JSONObject(from[0]);
            JSONArray jsonArr = getJsonArray(from[0]);

            if (myJson.has("getInfo")) {
                return parseGetInfoResponse(jsonArr);
            } else if (myJson.has("getAllInfo")) {
                return parseGetAllInfoResponse(jsonArr);
            } else if (myJson.has("getCoordinates")) {
                return parseGetCoordinatesResponse(jsonArr);
            }

        } catch (Exception e){
            e.printStackTrace();
            mException = e;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Sight[] result) {
        if (listener != null) {
            if (mException == null) {
                listener.onSuccess(result);
            } else {
                listener.onFailure(mException);
            }
        }
    }

    private JSONArray getJsonArray(String command) throws JSONException, IOException {
        Log.d("server", command);

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

        return jsonResult.getJSONArray("result");
    }

    private Sight[] parseGetInfoResponse(JSONArray jsonArr) throws JSONException {

        JSONObject item = jsonArr.getJSONObject(0);

        String title = item.getString("title");
        String description = item.getString("description");
        String img = item.getString("img");

        return new Sight[] {new Sight(this.id, title, description, img)};
    }

    private Sight[] parseGetAllInfoResponse(JSONArray jsonArr) throws JSONException {
        int size = jsonArr.length();
        Sight[] resultArr = new Sight[size];

        for(int i = 0; i < size; i++)
        {
            JSONObject item = jsonArr.getJSONObject(i);

            int id = Integer.parseInt(item.getString("_id"));
            String title = item.getString("title");
            String description = item.getString("description");
            String img = item.getString("img");

            resultArr[i] = new Sight(id, title, description, img);
        }

        return resultArr;
    }

    private Sight[] parseGetCoordinatesResponse(JSONArray jsonArr) throws JSONException {
        int size = jsonArr.length();
        Sight[] resultArr = new Sight[size];

        for(int i = 0; i < size; i++)
        {
            JSONObject item = jsonArr.getJSONObject(i);

            int id = Integer.parseInt(item.getString("_id"));
            int flag = Integer.parseInt(item.getString("flag"));
            String coordRaw = item.getString("coordinates");

            resultArr[i] = new Sight(id);
            resultArr[i].setCoordinates(coordRaw);
            resultArr[i].setFlag(flag);
        }

        return resultArr;
    }

    public void setOnResponseListener(OnResponseListener<Sight> listener) {
        this.listener = listener;
    }

}
