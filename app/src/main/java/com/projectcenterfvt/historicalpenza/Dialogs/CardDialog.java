package com.projectcenterfvt.historicalpenza.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.projectcenterfvt.historicalpenza.Adapters.PointAdapter;
import com.projectcenterfvt.historicalpenza.DataBases.DSightHandler;
import com.projectcenterfvt.historicalpenza.DataBases.Sight;
import com.projectcenterfvt.historicalpenza.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Отрисовка списка достопримечательностей
 * @author Roman
 * @version 1.0.0
 * @since 1.0.0
 */

public class CardDialog extends android.support.v4.app.DialogFragment {

    private final String DIALOG_TAG = "dialog_tag";
    private final String KEY_ADAPTER = "adapter";
    onEventListener listener;
    private ArrayList<Sight> sights;
    private View v;
    private ListView listView;
    private Parcelable state;
    private Parcelable state2;
    private PointAdapter adapter;

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_ADAPTER, adapter);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            adapter = savedInstanceState.getParcelable(KEY_ADAPTER);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (onEventListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onSomeEventListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(DIALOG_TAG, "onCreateView");
        v = inflater.inflate(R.layout.infocard, null);
        v.findViewById(R.id.btn_info_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        listView = v.findViewById(R.id.info_list);
        Log.d(DIALOG_TAG, "кол-во в списке " + sights.size());
        adapter = new PointAdapter(getContext(), sights, R.layout.list_item);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Sight sight = (Sight) adapterView.getAdapter().getItem(i);
                Log.d("clicked", "id = " + sight.getId() + " name = " + sight.getTitle() + " loc = " + sight.getLocation().toString());
                LatLng loc = sight.getLocation();
                listener.setPosition(loc);
                dismiss();
            }
        });
        return v;
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

    public interface onEventListener {
        void setPosition(LatLng loc);
    }
}
