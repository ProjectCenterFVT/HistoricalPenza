package com.projectcenterfvt.historicalpenza.Dialogs;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.projectcenterfvt.historicalpenza.R;

/**
 * Диалоговое окно о приложении
 * @author Roman
 * @version 1.0.0
 * @since 1.0.0
 */

public class AboutDialog extends android.support.v4.app.DialogFragment {


    private Dialog dialog;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setLayout(350, 400);
        dialogWindow.setBackgroundDrawable(new ColorDrawable(0));
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.about_menu, null);
        v.findViewById(R.id.btnBackFifth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        return v;
    }
}
