package com.projectcenterfvt.historicalpenza.Managers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

/**
 * Created by roman on 23.07.2018.
 */

public class ImageCacheManager {

    private Activity activity;
    private static final String UML_ADDR = "http://hpenza.creativityprojectcenter.ru/img/";

    public ImageCacheManager(Context context, Activity activity) {
        this.activity = activity;

        try{
            Picasso.Builder builder = new Picasso.Builder(context);
            builder.downloader(new OkHttp3Downloader(context,Integer.MAX_VALUE));
            Picasso built = builder.build();
            built.setIndicatorsEnabled(true);
            built.setLoggingEnabled(true);
            Picasso.setSingletonInstance(built);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void setImage(final ImageView image, final String filename){
        Picasso.with(activity)
                .load(UML_ADDR+filename)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        try {
                            FileOutputStream out = activity.openFileOutput(filename, Context.MODE_PRIVATE);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                            out.flush();
                            out.close();
                        } catch (IOException ex){
                            ex.printStackTrace();
                        }
                        try{
                            FileInputStream fin = activity.openFileInput(filename);
                            byte[] bytes = new byte[fin.available()];
                            fin.read(bytes);
                            Picasso.with(activity)
                                    .load(activity.getFileStreamPath(filename))
                                    .into(image);

                        }catch (IOException ex){
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }


}
