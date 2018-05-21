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
        mInflater = LayoutInflater.from(context);
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

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(mResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Sight sight = (Sight) getItem(position);
        viewHolder.card_name.setText(sight.getTitle());
        viewHolder.card_dist.setText(String.format("%d", sight.getDistance()));

        return convertView;
    }

    private class ViewHolder {
        final TextView card_name, card_dist;

        ViewHolder(View view) {
            card_name = view.findViewById(R.id.card_name);
            card_dist = view.findViewById(R.id.card_distance);
        }
    }

}
