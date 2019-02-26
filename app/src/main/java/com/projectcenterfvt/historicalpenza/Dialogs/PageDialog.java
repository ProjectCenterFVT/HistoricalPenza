package com.projectcenterfvt.historicalpenza.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.projectcenterfvt.historicalpenza.Adapters.MyGuideFragmentPagerAdapter;
import com.projectcenterfvt.historicalpenza.R;

/**
 * Отрисовка справки
 * @author Roman, Dmitry
 * @version 1.0.0
 * @since 1.0.0
 */

public class PageDialog extends DialogFragment {


    private ViewPager pager;
    private PagerAdapter pagerAdapter;
    private Dialog dialog;

    public PageDialog() {
        this.setStyle(STYLE_NO_TITLE, R.style.MyAlertDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_guide, null);

        pager = v.findViewById(R.id.pager1);
        pagerAdapter = new MyGuideFragmentPagerAdapter(getChildFragmentManager());
        pager.setAdapter(pagerAdapter);

        TabLayout tabLayout = v.findViewById(R.id.tabDots_second);
        tabLayout.setupWithViewPager(pager, true);

        v.findViewById(R.id.btnBackSecond).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
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
