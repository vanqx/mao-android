package com.ichangmao.app.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ichangmao.app.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WifiDirectFragment extends Fragment {


    public WifiDirectFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wifi_direct, container, false);
    }

}
