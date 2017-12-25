package com.projectcenterfvt.historicalpenza;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Roman on 15.12.2017.
 */

public class Card_dialog extends android.support.v4.app.DialogFragment {

    private ArrayList<ActivityMap.Point> listPoint;
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
        Log.d("adapter", "кол-во в списке "+listPoint.size());
        PointAdapter adapter = new PointAdapter(getContext(), listPoint);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ActivityMap.Point point = (ActivityMap.Point) adapterView.getAdapter().getItem(i);
                Log.d("clicked", "id = "+point.id+" name = "+point.name+" loc = "+point.location.toString());
                LatLng loc = point.location;
                listener.setPosition(loc);
                dismiss();
            }
        });
        return v;
    }
    //здесь был я
    public void setList(ArrayList<ActivityMap.Point> list){
        listPoint = list;
        ClientServer call = new ClientServer(getContext());
        call.execute("{\"getAllInfo\":\"1\"}");
        try {

            ArrayList <String> titles = new ArrayList<>();
           titles = call.get();
           if (titles==null){
               Log.d("server", "все пошло по пизде");
           } else {
               Log.d("server", "все пошло нормальды");
               for (int i = 0; i <listPoint.size(); i++) {
                   ActivityMap.Point point = list.get(i);
                   int id = point.id-1;
                   Log.d("List", "id = "+id+" titile "+(id)+" = "+titles.get(id));
                   point.name = titles.get(id);
                   list.set(i, point);
               }
           }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
