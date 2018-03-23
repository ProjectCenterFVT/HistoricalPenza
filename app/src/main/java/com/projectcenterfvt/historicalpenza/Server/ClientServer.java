package com.projectcenterfvt.historicalpenza.Server;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.projectcenterfvt.historicalpenza.DataBases.Sight;

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
 * Клиент-серверная часть. Нужно вспоминать, что делают запросы. Возможна переработка сервера. Пока не трогать!
 * @author Roman, Dmitry
 * @version 1.0.0
 * @since 1.0.0
 */


public class ClientServer extends AsyncTask<String, Void, Sight[]> {

    OnResponseListener<Sight> listener;
    byte[] data = null;
    InputStream is = null;
    private int id;
    private int IdAccaunt;
    private Exception mException;
    private Context context;
    private String server = "http://d95344yu.beget.tech/api/api.request.php";

    public ClientServer(Context context) {
        this.context = context;
    }

    public void getInfo(int id) {
        this.execute("{\"getInfo\":\"" + id + "\"}");
        this.id = id;
    }

    public void getAllInfo() {
        this.execute("{\"getAllInfo\":\"1\"}");
    }

    public void getCoordinates() {
        this.execute("{\"getCoordinates\":\"0.0.0\"}");
    }
    public void login() {
        this.execute("{\"login\":\"0.0.0\"}");
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
            else   if (myJson.has("login")) {
                return parselogin(jsonArr);
            }


        } catch (Exception e) {
            e.printStackTrace();
            mException = e;
        }

        return null;
    }

    private Sight[] parselogin(JSONArray jsonArr) throws JSONException {
        JSONObject item = jsonArr.getJSONObject(0);

       int code = 0;

        return new Sight[]{new Sight(code)};

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

    private Sight[] parseGetInfoResponse(JSONArray jsonArr) throws JSONException {

        JSONObject item = jsonArr.getJSONObject(0);

        String title = item.getString("title");
        String description = item.getString("description");
        String img = item.getString("img");

        return new Sight[]{new Sight(this.id, title, description, img)};
    }

    private Sight[] parseGetAllInfoResponse(JSONArray jsonArr) throws JSONException {
        int size = jsonArr.length();
        Sight[] resultArr = new Sight[size];

        for (int i = 0; i < size; i++) {
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

        for (int i = 0; i < size; i++) {
            JSONObject item = jsonArr.getJSONObject(i);

            int id = Integer.parseInt(item.getString("_id"));
            int flag = Integer.parseInt(item.getString("flag"));
            String coordRaw = item.getString("coordinates");
            int type = item.getInt("type");

            resultArr[i] = new Sight(id);
            resultArr[i].setCoordinates(coordRaw);
            resultArr[i].setFlag(flag == 1);
            resultArr[i].setType(type);
        }

        return resultArr;
    }

    public void setOnResponseListener(OnResponseListener<Sight> listener) {
        this.listener = listener;
    }

    public interface OnResponseListener<T> {
        void onSuccess(T[] result);

        void onFailure(Exception e);
    }

}
