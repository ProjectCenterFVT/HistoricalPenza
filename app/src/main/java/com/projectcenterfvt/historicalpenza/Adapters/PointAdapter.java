package com.projectcenterfvt.historicalpenza.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.projectcenterfvt.historicalpenza.DataBases.Sight;
import com.projectcenterfvt.historicalpenza.R;

import java.util.ArrayList;

/**
 * Created by Roman on 15.12.2017.
 */

public class PointAdapter extends BaseAdapter {

    ArrayList<Sight> sights;
    private LayoutInflater mInflater;
    private int mResource;


    public PointAdapter(Context context, ArrayList<Sight> sights, int resource) {
        this.mResource = resource;
        this.sights = sights;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.d("adapter", "кол-во в списке " + sights.size());
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
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mResource);
    }

    private View createViewFromResource(int position, View convertView, ViewGroup parent, int resource) {
        View v;
        if (convertView == null) {
            v = mInflater.inflate(resource, parent, false);
        } else {
            v = convertView;
        }

        bindView(position, v);

        return v;
    }

    private void bindView(int position, View view) {
        Sight sight = (Sight) getItem(position);
        TextView card_name = view.findViewById(R.id.card_name);
        TextView card_dist = view.findViewById(R.id.card_distance);
        card_name.setText(sight.getTitle());
        card_dist.setText(sight.getDistance() + " м");
    }
}
