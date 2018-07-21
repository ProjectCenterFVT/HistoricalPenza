package com.projectcenterfvt.historicalpenza.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.projectcenterfvt.historicalpenza.DataBases.IDatabaseHandler;
import com.projectcenterfvt.historicalpenza.DataBases.Sight;
import com.projectcenterfvt.historicalpenza.R;

import java.util.ArrayList;

/**
 * Created by roman on 20.07.2018.
 */

public class SightAdapter extends RecyclerView.Adapter<SightAdapter.ViewHolder> {

    private ArrayList<Sight> sights;

    public SightAdapter(ArrayList<Sight> sights){
        this.sights = sights;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sight_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Sight sight = sights.get(position);
        holder.tlItem.setText(sight.getTitle());
        double distance = sight.getDistance();
        if (distance>1000){
            distance /= 1000;
            holder.sItem.setText(String.format("Расстояние : %.2f км", distance));
        } else {
            holder.sItem.setText("Расстояние : " + sight.getDistance()+" м");
        }
    }

    @Override
    public int getItemCount() {
        return sights.size();
    }

    public Sight getItem(int position){
        return sights.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tlItem, sItem;
        ViewHolder(View view){
            super(view);
            tlItem = (TextView) view.findViewById(R.id.two_line_item);
            sItem = (TextView) view.findViewById(R.id.secondary_item);
        }

    }

}
