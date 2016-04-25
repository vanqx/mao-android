package com.ichangmao.app.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ichangmao.app.R;
import com.ichangmao.app.net.TcpServer;
import com.ichangmao.commons.MaoLog;

public class TcpFragment extends Fragment {

    MaoLog log = MaoLog.getLogger(this.getClass().getSimpleName());
    TcpServer mTcpServer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log.i("onCreateView");
        new Thread() {
            @Override
            public void run() {
                mTcpServer = new TcpServer(5263);
                mTcpServer.start();
            }

        }.start();
        View rootView = inflater.inflate(R.layout.fragment_sample, container, false);
        TextView txt_content = (TextView) rootView.findViewById(R.id.txt_content);
        txt_content.setText("Tcp server is running...");
        return rootView;
    }

    @Override
    public void onDestroyView() {
        log.i("onDestroyView");
        super.onDestroyView();

        new Thread() {
            @Override
            public void run() {
                if (mTcpServer != null) {
                    mTcpServer.stop();
                    mTcpServer = null;
                }
            }

        }.start();
    }

}
