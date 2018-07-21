package com.projectcenterfvt.historicalpenza.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.projectcenterfvt.historicalpenza.CustomTextView.TextViewEx;
import com.projectcenterfvt.historicalpenza.Dialogs.HomestadeDialog;
import com.projectcenterfvt.historicalpenza.R;
import com.projectcenterfvt.historicalpenza.Server.BaseAsyncTask;

import java.io.InputStream;

/**
 * Активити
 * Класс - карточка достопримечательности
 * @author Roman
 * @version 1.0.0
 * @since 1.0.0
 */
@Deprecated
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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

        new DownloadImage((ImageView) findViewById(R.id.ivPhoto)).execute(uml);
        tvDescription.setText(description, true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK, null);
        finish();
    }

    /**Обработчик нажатия кнопки камеры */

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
            String urldisplay = BaseAsyncTask.UML_ADDR + urls[0];
            //String urldisplay = urls[0].replaceAll("api/" ,"");
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
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
