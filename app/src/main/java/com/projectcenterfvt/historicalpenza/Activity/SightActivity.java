package com.projectcenterfvt.historicalpenza.Activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.projectcenterfvt.historicalpenza.Adapters.RecyclerItemClickListener;
import com.projectcenterfvt.historicalpenza.Adapters.SightAdapter;
import com.projectcenterfvt.historicalpenza.DataBases.DSightHandler;
import com.projectcenterfvt.historicalpenza.DataBases.DataBaseHandler;
import com.projectcenterfvt.historicalpenza.DataBases.Sight;
import com.projectcenterfvt.historicalpenza.Managers.LocationManager;
import com.projectcenterfvt.historicalpenza.R;
import com.projectcenterfvt.historicalpenza.UI.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.widget.LinearLayout.HORIZONTAL;

/**
 * Created by roman on 21.07.2018.
 */

public class SightActivity extends AppCompatActivity {

    private final int SIGHT_KEY = 2;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sight_list, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_OK, null);
        finish();
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sight_list);

        Toolbar toolbar = findViewById(R.id.stoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Пенза");

        RecyclerView recyclerView = findViewById(R.id.sight_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        ArrayList<Sight> sights = intent.getParcelableArrayListExtra("sights");
        SightAdapter adapter = new SightAdapter(sights);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getResources()));
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, SightAdapter adapter, int position) {
                        Sight sight = adapter.getItem(position);
                        LatLng latLng = sight.getLocation();
                        //listener.setPosition(latLng);
                        Intent data = new Intent();
                        data.putExtra("latitude", sight.getLatitude());
                        data.putExtra("longitude", sight.getLongitude());
                        setResult(RESULT_OK, data);
                        finish();
                    }

                    @Override
                    public void onLongItemClick(View view, SightAdapter adapter, int position) {

                    }
                })
        );
    }

}
