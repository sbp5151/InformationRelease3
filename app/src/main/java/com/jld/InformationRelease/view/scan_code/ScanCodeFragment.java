package com.jld.InformationRelease.view.scan_code;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jld.InformationRelease.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScanCodeFragment extends Fragment {


    public ScanCodeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scan_code, container, false);
    }

}
