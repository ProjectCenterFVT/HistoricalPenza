package com.projectcenterfvt.historicalpenza.Dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.projectcenterfvt.historicalpenza.R;

/**
 * Created by roman on 02.03.2018.
 */

public class HomestadeDialog extends android.support.v4.app.DialogFragment {

    private Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_homestade, null);
        v.setBackgroundResource(R.drawable.dialog_bgn);
        v.findViewById(R.id.homestadeButtonWeb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri address = Uri.parse("https://vk.com/po_sledam_usadeb58");
                Intent openlinkIntent = new Intent(Intent.ACTION_VIEW, address);
                startActivity(openlinkIntent);
            }
        });
        return v;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setBackgroundDrawable(new ColorDrawable(0));
        return dialog;
    }
}