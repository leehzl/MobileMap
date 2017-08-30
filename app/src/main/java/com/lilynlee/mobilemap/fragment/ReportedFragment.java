package com.lilynlee.mobilemap.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lilynlee.mobilemap.R;

/**
 * Created by admin on 2016/8/4.
 */
public class ReportedFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.reported_layout,container,false);
    }
}
