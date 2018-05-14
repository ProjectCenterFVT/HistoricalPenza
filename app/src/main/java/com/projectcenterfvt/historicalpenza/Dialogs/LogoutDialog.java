package com.projectcenterfvt.historicalpenza.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.projectcenterfvt.historicalpenza.Activity.MapActivity;
import com.projectcenterfvt.historicalpenza.Activity.SplashActivity;
import com.projectcenterfvt.historicalpenza.R;

import static com.projectcenterfvt.historicalpenza.Activity.SplashActivity.APP_PREFERENCES;


/**
 * Created by MaksimS on 04.05.2018.
 */

public class LogoutDialog  extends android.support.v4.app.DialogFragment {
private Dialog dialog;
    private SharedPreferences mAccount;
    public static final String APP_PREFERENCES_TOKEN = "token";
    static final String KEY_IS_FIRST_TIME = "first_time";

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
        SharedPreferences mAccount = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
            String vakue = " ";
        //SharedPreferences.Editor editor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
        SharedPreferences.Editor editor = mAccount.edit();
        editor.putString(APP_PREFERENCES_TOKEN, vakue);
        editor.putBoolean(KEY_IS_FIRST_TIME, true);
        editor.apply();


        dialog.hide();
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
                    dialog.hide();
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
