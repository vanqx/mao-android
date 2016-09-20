package com.ichangmao.app.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ichangmao.app.R;
import com.ichangmao.commons.MaoLog;
import com.ichangmao.http.MaoServer;

public class HttpFragment extends Fragment implements View.OnClickListener {

    MaoLog log = MaoLog.getLogger(this.getClass().getSimpleName());
    MaoServer maoServer = new MaoServer();

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
                maoServer.start(5263);
                txt_content.setText("server is start...");
                break;
        }
    }
}
