package com.projectcenterfvt.historicalpenza;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
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

import java.util.ArrayList;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
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

            }
        });
        return v;
    }

    public void setList(ArrayList<ActivityMap.Point> list){
        listPoint = list;
    }
}
