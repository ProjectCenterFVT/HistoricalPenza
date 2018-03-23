package com.projectcenterfvt.historicalpenza.Server;

import android.content.Context;
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
import java.net.URL;

/**
 * Created by MaksimS on 23.03.2018.
 */

public class LoginServer extends AsyncTask<String,Void,String>{

    byte[] data = null;
    InputStream is = null;
    private String token;
    private int IdAccaunt;
    OnResponseListener listener;
    private Exception mException;
    private Context context;
    private String server = "http://d95344yu.beget.tech/api/api.request.php";

    public void getLogin(String token){

        this.execute("{\"type\":\"login\",\n" +
                "\"token\":\""+token+"\"}");
        this.token = token;

    }

    @Override
    protected String doInBackground(String... from) {
        try {
            JSONArray jsonArr = getJsonArray(from[0]);

            return parseGetLogin(jsonArr);


        } catch (Exception e) {
            e.printStackTrace();
            mException = e;
        }

        return null;
    }

    private JSONArray getJsonArray(String command) throws JSONException, IOException {
        Log.d("server", command);

        URL url = new URL(server);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36");
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);

        OutputStream os = conn.getOutputStream();
        data = command.getBytes("UTF-8");
        Log.d("server", "отпралвяю " + command);
        os.write(data);
        data = null;

        conn.connect();

        int code = conn.getResponseCode();
        Log.d("server", "код = " + code);

        is = conn.getInputStream();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        data = baos.toByteArray();
        String resultString = new String(data, "UTF-8");
        Log.d("server", "result = " + resultString);

        JSONObject jsonResult = new JSONObject(resultString);

        return jsonResult.getJSONArray("result");
    }

    private String parseGetLogin(JSONArray jsonArr) throws JSONException {

        JSONObject item = jsonArr.getJSONObject(0);

        String id = item.getString("enc_id");

        return id;
    }

    protected void onPostExecute(String result) {
        if (listener != null) {
            if (mException == null) {
                listener.onSuccess(result);
            } else {
                listener.onFailure(mException);
            }
        }
    }

    public void setOnResponseListener(OnResponseListener listener) {
        this.listener = listener;
    }


    public interface OnResponseListener {
        void onSuccess(String result);

        void onFailure(Exception e);
    }
}

