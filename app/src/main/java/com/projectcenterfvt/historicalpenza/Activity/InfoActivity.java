package com.projectcenterfvt.historicalpenza.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.projectcenterfvt.historicalpenza.CustomTextView.TextViewEx;
import com.projectcenterfvt.historicalpenza.Dialogs.HomestadeDialog;
import com.projectcenterfvt.historicalpenza.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Активити
 * Класс - карточка достопримечательности
 * @author Roman
 * @version 1.0.0
 * @since 1.0.0
 */

public class InfoActivity extends AppCompatActivity {

    /**
     * Метод получает от <b>MapActivity</b> данные и отрисовывает на экране
     *
     * @param savedInstanceState сохраненное состояние
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        TextViewEx tvDescription = findViewById(R.id.tvDescription);
        tvDescription.setMovementMethod(new ScrollingMovementMethod());

        TextView tvTitle = findViewById(R.id.tvTitle);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String uml = intent.getStringExtra("uml");
        Boolean check = intent.getBooleanExtra("button", false);
        if (check) {
            Button btn = findViewById(R.id.homestadeButton);
            btn.setVisibility(View.VISIBLE);
        }
        tvTitle.setText(title);

        new DownloadImage((ImageView) findViewById(R.id.ivPhoto)).execute("http://" + uml);
        tvDescription.setText(description, true);
    }

    /**Обработчик нажатия кнопки назад в телефоне */
    public void onBackClick(View view) {
        setResult(RESULT_OK, null);
        finish();
    }

    /**Обработчик нажатия кнопки камеры */

    public void onCameraClick(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CAMERA_BUTTON);
        intent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_CAMERA));
        sendOrderedBroadcast(intent, null);
    }

    public void homestadeClick(View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        HomestadeDialog dialog = new HomestadeDialog();
        dialog.show(fragmentManager, "dialog");
    }

    /**
     * Класс для получения изображения от сервера
     * @author Roman
     * @version 1.0.0
     * @since 1.0.0
     */
    class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        ImageView bmImage;

        public DownloadImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {

            String urldisplay = urls[0];
            //String server = "http://d95344yu.beget.tech/api/api.request.php";
            Bitmap mIcon11 = null;
            try {

                URL url = new URL(urldisplay);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36");
                conn.setRequestMethod("GET");
                conn.setDoOutput(true);
                conn.connect();

                int code = conn.getResponseCode();
                String info = conn.getRequestMethod();
                info = conn.getContentType();
                info = conn.getRequestMethod();
                InputStream in = conn.getInputStream();

                mIcon11 = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
