package com.projectcenterfvt.historicalpenza;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Roman on 15.12.2017.
 */

public class CardDialog extends android.support.v4.app.DialogFragment {

    private ArrayList<Sight> sights;
    private ArrayList<String> listString = new ArrayList<>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        return dialog;
    }

    public interface onEventListener {
        public void setPosition(LatLng loc);
    }

    onEventListener listener;

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
        View v = inflater.inflate(R.layout.infocard, null);
        v.setBackgroundResource(R.drawable.dialog_bgn);
        v.findViewById(R.id.btn_info_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        ListView listView = (ListView) v.findViewById(R.id.info_list);
        Log.d("adapter", "кол-во в списке "+sights.size());
        PointAdapter adapter = new PointAdapter(getContext(), sights, R.layout.list_item);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Sight sight = (Sight) adapterView.getAdapter().getItem(i);
                Log.d("clicked", "id = "+sight.getId()+" name = "+sight.getTitle()+" loc = "+sight.getLocation().toString());
                LatLng loc = sight.getLocation();
                listener.setPosition(loc);
                dismiss();
            }
        });
        return v;
    }

    public void setList(ArrayList<Sight> list){
        sights = list;
    }
}
