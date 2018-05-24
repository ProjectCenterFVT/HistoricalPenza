package com.projectcenterfvt.historicalpenza.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.projectcenterfvt.historicalpenza.Activity.MapActivity;
import com.projectcenterfvt.historicalpenza.Activity.SplashActivity;
import com.projectcenterfvt.historicalpenza.R;

import static com.projectcenterfvt.historicalpenza.Activity.SplashActivity.APP_PREFERENCES;


/**
 * Created by MaksimS on 04.05.2018.
 */

public class LogoutDialog  extends android.support.v4.app.DialogFragment {
    public static final String APP_PREFERENCES_TOKEN = "token";
    static final String KEY_IS_FIRST_TIME = "first_time";
    private Dialog dialog;
    private SharedPreferences mAccount;

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
        String value = " ";
        //SharedPreferences.Editor editor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
        SharedPreferences.Editor editor = mAccount.edit();
        editor.putString(APP_PREFERENCES_TOKEN, value);
        editor.putBoolean(KEY_IS_FIRST_TIME, true);
        editor.apply();


        dialog.dismiss();
        Intent intent  = new Intent(getActivity(), SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
        getActivity().finish();
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
