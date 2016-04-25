package com.ichangmao.app.ui;


import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ichangmao.app.R;
import com.ichangmao.commons.MaoLog;
import com.ichangmao.jni.MaoJni;

public class JniFragment extends Fragment implements View.OnClickListener {

    MaoLog log = MaoLog.getLogger(this.getClass().getSimpleName());

    MaoJni mMaoJni = new MaoJni();

    TextView txt_content;
    Button btn_ok;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log.i("onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_sample, container, false);
        txt_content = (TextView) rootView.findViewById(R.id.txt_content);
        btn_ok = (Button) rootView.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        log.i("onDestroyView");
        super.onDestroyView();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                int times = 100000;
                mMaoJni.test(times);
                test(times);
        }
    }

    void test(int times) {
        long start =System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            emptyMethod();
        }
        long end = System.currentTimeMillis();
        long time = end - start;
        log.d("call " + times + " times emptyMethod,spend:" + time);
    }

    void emptyMethod() {

    }

    static {
        System.loadLibrary("mao-jni");
    }
}
