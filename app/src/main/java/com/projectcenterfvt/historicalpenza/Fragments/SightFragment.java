package com.projectcenterfvt.historicalpenza.Fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.projectcenterfvt.historicalpenza.Adapters.RecyclerItemClickListener;
import com.projectcenterfvt.historicalpenza.Adapters.SightAdapter;
import com.projectcenterfvt.historicalpenza.DataBases.DSightHandler;
import com.projectcenterfvt.historicalpenza.DataBases.Sight;
import com.projectcenterfvt.historicalpenza.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by roman on 20.07.2018.
 */

public class SightFragment extends Fragment {

    private ArrayList<Sight> sights;
    onSightItemClickListener listener;
    private Fragment fragment;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sight_list, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if(actionBar!= null) {
            actionBar.setTitle("Достопримечательности");
        }

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.sight_recycler_view);
        SightAdapter adapter;
        Context context;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            context = getContext();
        } else {
            context = view.getContext();
        }
        adapter = new SightAdapter(context, sights);
        fragment = this;
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, SightAdapter adapter, int position) {
                        Sight sight = adapter.getItem(position);
                        listener.setPosition(sight.getLocation());
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.remove(fragment);
                        fragmentTransaction.commit();
                    }

                    @Override
                    public void onLongItemClick(View view, SightAdapter adapter, int position) {

                    }
                })
        );
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sight_list, menu);
    }

    public void setList(final Location mLastKnowLocation, ArrayList<Sight> list) {
        sights = list;
        Collections.sort(sights, new Comparator<Sight>() {
            @Override
            public int compare(Sight s0, Sight s1) {
                s0.setDistance(DSightHandler.calculateDistance(mLastKnowLocation, s0.getLocation()));
                s1.setDistance(DSightHandler.calculateDistance(mLastKnowLocation, s1.getLocation()));
                return s0.getDistance() - s1.getDistance();
            }
        });
    }

    public interface onSightItemClickListener{
        void setPosition(LatLng latLng);
    }
}
