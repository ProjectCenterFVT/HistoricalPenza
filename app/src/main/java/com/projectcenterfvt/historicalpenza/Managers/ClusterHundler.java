package com.projectcenterfvt.historicalpenza.Managers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.projectcenterfvt.historicalpenza.Activity.InfoActivity;
import com.projectcenterfvt.historicalpenza.BuildConfig;
import com.projectcenterfvt.historicalpenza.DataBases.DSightHandler;
import com.projectcenterfvt.historicalpenza.DataBases.Sight;
import com.projectcenterfvt.historicalpenza.Dialogs.LandmarkDialog;
import com.projectcenterfvt.historicalpenza.R;
import com.projectcenterfvt.historicalpenza.data.Landmark;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by roman on 12.07.2018.
 */

public class ClusterHundler {
    private static final int READ_EXTERNAL_RESPONSE_KEY = 36;
    private static final int WRITE_EXTERNAL_RESPONSE_KEY = 37;
    private int CAMERA_KEY = 1;
    private ClusterManager<Sight> clusterManager;
    private GoogleMap mMap;
    private Context context;
    private FragmentActivity activity;
    private LocationManager locationManager;
    private BillingManager mBillingManager;
   // private AcquireFragment mAcquireFragment;
    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    public ClusterHundler(GoogleMap mMap, Context context, FragmentActivity activity,BillingManager billingManager) {
        this.mMap = mMap;
        this.context = context;
        this.activity = activity;
        this.mBillingManager = billingManager;
    }

    public void setupClusterManager() {
        clusterManager = new ClusterManager<Sight>(context, mMap);
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
                LatLng lastKnownLocation =
                        new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                URL photoUrl = null;
                try {
                    photoUrl = new URL(BuildConfig.API_ENDPOINT + "img/" + sight.getImg());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                Landmark landmark = new Landmark(
                        sight.getId(),
                        sight.getTitle(),
                        sight.getDescription(),
                        photoUrl,
                        new LatLng(sight.getLatitude(), sight.getLongitude()),
                        Landmark.Type.EXTRA,
                        sight.getFlag()
                );

                LandmarkDialog dialog = LandmarkDialog.Companion.newInstance(landmark, lastKnownLocation);

                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                Fragment prev = activity.getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                dialog.show(ft, "dialog");

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

    class MarkerRender extends DefaultClusterRenderer<Sight> {

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
