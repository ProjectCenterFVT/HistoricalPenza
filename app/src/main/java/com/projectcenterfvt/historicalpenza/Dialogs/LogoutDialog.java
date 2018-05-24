package com.projectcenterfvt.historicalpenza.Dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.projectcenterfvt.historicalpenza.Activity.SplashActivity;
import com.projectcenterfvt.historicalpenza.Managers.PreferencesManager;
import com.projectcenterfvt.historicalpenza.R;

/**
 * Created by MaksimS on 04.05.2018.
 */

public class LogoutDialog  extends android.support.v4.app.DialogFragment {
    private Dialog dialog;

    @Nullable
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setLayout(350, 400);
        dialogWindow.setBackgroundDrawable(new ColorDrawable(0));

        return dialog;
    }
    private void signOut() {
        PreferencesManager preferencesManager = new PreferencesManager(getContext());
        preferencesManager.setToken(" ");
        preferencesManager.setFirstTime(true);

        dialog.dismiss();
        startActivity(new Intent(getActivity(), SplashActivity.class));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.log_out_menu, null);
        v.setBackgroundResource(R.drawable.dialog_bgn);

        v.findViewById(R.id.btnBackForth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        v.findViewById(R.id.button_log_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
        return v;
    }

}
