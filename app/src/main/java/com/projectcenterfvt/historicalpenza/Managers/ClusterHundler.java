package com.projectcenterfvt.historicalpenza.Managers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.projectcenterfvt.historicalpenza.Activity.InfoActivity;
import com.projectcenterfvt.historicalpenza.DataBases.DSightHandler;
import com.projectcenterfvt.historicalpenza.DataBases.Sight;
import com.projectcenterfvt.historicalpenza.R;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by roman on 12.07.2018.
 */

public class ClusterHundler {
    private ClusterManager<Sight> clusterManager;
    private GoogleMap mMap;
    private Context context;
    private Activity activity;
    private LocationManager locationManager;
    private int CAMERA_KEY = 1;

    public ClusterHundler(GoogleMap mMap, Context context, Activity activity) {
        this.mMap = mMap;
        this.context = context;
        this.activity = activity;
    }

    public void setupClusterManager() {
        clusterManager = new ClusterManager<Sight>(context, mMap);
        locationManager = new LocationManager(context, activity);
        clusterManager.setRenderer(new MarkerRender());
        clusterManager.setAlgorithm(new GridBasedAlgorithm<Sight>());
        mMap.setOnCameraIdleListener(clusterManager);
        clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<Sight>() {
            @Override
            public boolean onClusterClick(Cluster<Sight> cluster) {
                LatLngBounds.Builder builder = LatLngBounds.builder();
                for (ClusterItem item : cluster.getItems()) {
                    builder.include(item.getPosition());
                }
                final LatLngBounds bounds = builder.build();

                try {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            }
        });
        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<Sight>() {
            @Override
            public boolean onClusterItemClick(final Sight sight) {
                Location mLastKnownLocation = locationManager.getDeviceLocation();
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                final LayoutInflater inflater = activity.getLayoutInflater();
                final View view = inflater.inflate(R.layout.dialog, null);
                view.setBackgroundResource(R.drawable.dialog_bgn);

                final TextView info = view.findViewById(R.id.dialog_text_info);
                final TextView distance = view.findViewById(R.id.dialog_text_distance);
                final TextView were = view.findViewById(R.id.dialog_text_were);

                final Button first = view.findViewById(R.id.first_btn);
                final Button second = view.findViewById(R.id.second_btn);

                builder.setView(view);

                final AlertDialog alert = builder.create();
                info.setText(sight.getTitle());
                if (sight.getFlag() | sight.getType() == 1) {
                    were.setText("Вы тут были");
                    first.setText("Узнать больше");

                    if (mLastKnownLocation != null) {
                        int dist = DSightHandler.calculateDistance(mLastKnownLocation, sight.getLatitude(), sight.getLongitude());
                        distance.setText(dist + " м");
                    }

                    first.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, InfoActivity.class);
                            intent.putExtra("title", sight.getTitle());
                            intent.putExtra("description", sight.getDescription());
                            intent.putExtra("uml", sight.getImg());
                            if (sight.getType() == 1) {
                                intent.putExtra("button", true);
                            }
                            activity.startActivityForResult(intent, CAMERA_KEY);
//                            isMarkerClick = false;
                            alert.dismiss();
                        }
                    });

                } else {
                    were.setText("Вы тут еще не были");
                    first.setText("Хочу открыть");
                    first.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(activity, "Доступно в следующих версиях", Toast.LENGTH_SHORT).show();
                        }
                    });
                    if (mLastKnownLocation != null) {
                        int dist = DSightHandler.calculateDistance(mLastKnownLocation, sight.getLatitude(), sight.getLongitude());
                        distance.setText(dist + " м");
                    }
                }
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                second.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                    }
                });
                alert.show();

                return true;
            }
        });

        mMap.setOnMarkerClickListener(clusterManager);
    }

    public void addSights(ArrayList<Sight> sights) {
        clusterManager.addItems(sights);
    }

    public void clearMap(){
        clusterManager.clearItems();
        clusterManager.cluster();
    }

    public void restoreMap(){
        clusterManager.cluster();
    }

    public void refreshMarker(Sight sight) {
        sight.setFlag(true);
        MarkerRender render = (MarkerRender) clusterManager.getRenderer();
        Bitmap bitmap_unlock = BitmapFactory.decodeResource(context.getResources(), context.getResources().
                getIdentifier("unlock", "drawable", context.getPackageName()));
        bitmap_unlock = Bitmap.createScaledBitmap(bitmap_unlock, 74, 100, false);
        render.getMarker(sight).setIcon(BitmapDescriptorFactory.fromBitmap(bitmap_unlock));
    }

    private class MarkerRender extends DefaultClusterRenderer<Sight> {

        public MarkerRender() {
            super(context, mMap, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(Sight item, MarkerOptions markerOptions) {
            switch (item.getType()) {
                case 0:
                    if (item.getFlag()) {
                        Bitmap bitmap_unlock = BitmapFactory.decodeResource(context.getResources(), context.getResources().
                                getIdentifier("unlock", "drawable", context.getPackageName()));
                        bitmap_unlock = Bitmap.createScaledBitmap(bitmap_unlock, 74, 100, false);
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap_unlock));
                    } else {
                        Bitmap bitmap_lock = BitmapFactory.decodeResource(context.getResources(), context.getResources().
                                getIdentifier("lock", "drawable", context.getPackageName()));
                        bitmap_lock = Bitmap.createScaledBitmap(bitmap_lock, 62, 100, false);
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap_lock));
                    }
                    break;

                case 1:
                    Bitmap bitmapHomestead = BitmapFactory.decodeResource(context.getResources(), context.getResources().
                            getIdentifier("homestead", "drawable", context.getPackageName()));
                    bitmapHomestead = Bitmap.createScaledBitmap(bitmapHomestead, 74, 100, false);
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmapHomestead));
                    break;
            }
        }
    }

}
