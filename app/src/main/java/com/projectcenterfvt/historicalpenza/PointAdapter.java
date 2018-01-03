package com.projectcenterfvt.historicalpenza;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Roman on 15.12.2017.
 */

public class PointAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    ArrayList<Sight> sights;


    PointAdapter(Context context, ArrayList<Sight> sights){
        this.context = context;
        this.sights = sights;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.d("adapter", "кол-во в списке "+sights.size());
    }

    @Override
    public int getCount() {
        return sights.size();
    }

    @Override
    public Object getItem(int i) {
        return sights.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        v = inflater.inflate(R.layout.list_item, viewGroup, false);
        Sight sight = (Sight) getItem(i);
        TextView card_name = (TextView) v.findViewById(R.id.card_name);
        TextView card_dist = (TextView) v.findViewById(R.id.card_distance);
        card_name.setText(sight.getTitle());
        card_dist.setText(sight.getDistance()+" м");
        return v;
    }
}
