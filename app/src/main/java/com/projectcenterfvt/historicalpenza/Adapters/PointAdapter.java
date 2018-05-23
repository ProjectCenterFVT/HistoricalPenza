package com.projectcenterfvt.historicalpenza.Adapters;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
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

public class PointAdapter extends BaseAdapter implements Parcelable {

    public static final Creator<PointAdapter> CREATOR = new Creator<PointAdapter>() {
        @Override
        public PointAdapter createFromParcel(Parcel in) {
            return new PointAdapter(in);
        }

        @Override
        public PointAdapter[] newArray(int size) {
            return new PointAdapter[size];
        }
    };
    private ArrayList<Sight> sights;
    private LayoutInflater mInflater;
    private int mResource;
    private ArrayList<String> strings;

    public PointAdapter(Context context, ArrayList<Sight> sights, int resource) {
        this.mResource = resource;
        this.sights = sights;
        mInflater = LayoutInflater.from(context);
        strings = new ArrayList<>();
        strings.add("Проверка");
        Log.d("adapter", "кол-во в списке " + sights.size());
    }

    protected PointAdapter(Parcel in) {
        sights = in.createTypedArrayList(Sight.CREATOR);
        mResource = in.readInt();
    }

    @Override
    public PointAdapter clone() throws CloneNotSupportedException {
        return (PointAdapter) super.clone();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(sights);
        parcel.writeInt(mResource);
    }

    private class ViewHolder {
        final TextView card_name, card_dist;

        ViewHolder(View view) {
            card_name = view.findViewById(R.id.card_name);
            card_dist = view.findViewById(R.id.card_distance);
        }
    }


}
