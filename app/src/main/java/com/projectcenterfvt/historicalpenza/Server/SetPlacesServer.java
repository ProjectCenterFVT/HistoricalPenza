package com.projectcenterfvt.historicalpenza.Server;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Dmitry on 01.04.2018.
 */

public class SetPlacesServer extends BaseAsyncTask<String, Void> {

    private int id;
    private static final String TAG_JSON = "JSON_SERVER";

    public void setPlaces(int id, String mToken) {
        this.id = id;
        JSONObject JSONToServer = new JSONObject();
        try {
            JSONToServer.put("type", "setPlaces");
            JSONToServer.put("enc_id", mToken);
            JSONToServer.put("id", "" + id);
            this.execute(JSONToServer.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(String... from) {
        try {
            boolean isSuccess = setPlace(from[0]);
            if (!isSuccess) { throw new Exception("Сервер не обработал запрос правильно"); }
        } catch (Exception e) {
            e.printStackTrace();
            mException = e;
        }

        return null;
    }

    private boolean setPlace(String command) throws JSONException, IOException {
        Log.d("server", command);

        byte[] data;

        URL url = new URL(SERVER_ADDR);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36");
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);

        OutputStream os = conn.getOutputStream();
        data = command.getBytes("UTF-8");
        Log.d(TAG_JSON, "отпралвяю " + command);
        os.write(data);
        data = null;

        conn.connect();

        int code = conn.getResponseCode();
        Log.d("server", "код = " + code);

        InputStream is = conn.getInputStream();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        data = baos.toByteArray();
        String resultString = new String(data, "UTF-8");
        Log.d("server", "result = " + resultString);

        return Boolean.parseBoolean(resultString);
    }
}
