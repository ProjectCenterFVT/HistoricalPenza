package com.projectcenterfvt.historicalpenza;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Dmitry on 13.12.2017.
 */

public class PageFragment extends Fragment {

    int mNum;

    int[] pages = {
            R.drawable.first_pager,
            R.drawable.second_pager,
            R.drawable.third_pager,
    };

    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

    public static PageFragment newInstance(int page) {
        PageFragment pageFragment = new PageFragment();

        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        pageFragment.setArguments(arguments);

        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt(ARGUMENT_PAGE_NUMBER) : 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, null);

        ImageView ivPage = (ImageView) view.findViewById(R.id.ivPage);
        ivPage.setImageResource(pages[mNum]);

        return view;
    }
}