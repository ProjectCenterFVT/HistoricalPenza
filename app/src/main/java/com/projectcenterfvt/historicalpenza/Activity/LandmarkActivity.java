package com.projectcenterfvt.historicalpenza.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.single.PermissionListener;
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener;
import com.nineoldandroids.view.ViewHelper;
import com.projectcenterfvt.historicalpenza.Dialogs.HomestadeDialog;
import com.projectcenterfvt.historicalpenza.Managers.ImageCacheManager;
import com.projectcenterfvt.historicalpenza.R;
import com.projectcenterfvt.historicalpenza.Server.BaseAsyncTask;

import java.io.InputStream;

/**
 * Created by MaksimS on 19.07.2018.
 */

public class LandmarkActivity  extends AppCompatActivity implements ObservableScrollViewCallbacks {

    private static final int PERMISSION_REQUEST_CODE = 777;

    private View mImageView;
    private View mToolbarView;
    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mImageView = findViewById(R.id.image_info);
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String uml = intent.getStringExtra("uml");
        Boolean check = intent.getBooleanExtra("button", false);
        mToolbarView = findViewById(R.id.toolbar);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.colorPrimary)));
        mScrollView = findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);
        ImageCacheManager imageCacheManager = new ImageCacheManager(getApplicationContext(), this);
        imageCacheManager.setImage((ImageView) mImageView, uml);
        //new DownloadImageLand((ImageView) findViewById(R.id.image_info)).execute(uml);

        TextView body = findViewById(R.id.body);
        body.setText(description);
getSupportActionBar().setTitle(title);
        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);

        if (check) {
            Button btn = findViewById(R.id.homestadeButtonland);
            btn.setVisibility(View.VISIBLE);
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK, null);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_OK, null);
        finish();
        return true;
    }

    /**Обработчик нажатия кнопки камеры */

    public void homestadeClick(View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        HomestadeDialog dialog = new HomestadeDialog();
        dialog.show(fragmentManager, "dialog");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onScrollChanged(mScrollView.getCurrentScrollY(), false, false);
    }
    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = getResources().getColor(R.color.colorPrimary);
        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(mImageView, scrollY /2);
     int   Y_scrol = scrollY;
        Log.d("scroll"," У скролено" + Y_scrol);

    }


    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {


    }

    public void homestadeClickLand(View view) {
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
    class DownloadImageLand extends AsyncTask<String, Void, Bitmap> {

        ImageView bmImage;

        public DownloadImageLand(ImageView bmImage) {
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
