package com.ichangmao.app.ui;

import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ichangmao.app.R;
import com.ichangmao.app.nsd.ChatConnection;
import com.ichangmao.app.nsd.NsdHelper;
import com.ichangmao.commons.MaoLog;

public class NsdFragment extends Fragment {

    private MaoLog log = MaoLog.getLogger(this.getClass().getSimpleName());
    NsdHelper mNsdHelper;

    private View rootView;
    private TextView mStatusView;
    private Handler mUpdateHandler;

    public static final String TAG = "NsdChat";

    ChatConnection mConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_nsd, container, false);
        mStatusView = (TextView) rootView.findViewById(R.id.status);
        rootView.findViewById(R.id.btn_register).setOnClickListener(mOnClickListener);
        rootView.findViewById(R.id.btn_discover).setOnClickListener(mOnClickListener);
        rootView.findViewById(R.id.btn_connect).setOnClickListener(mOnClickListener);
        rootView.findViewById(R.id.btn_send).setOnClickListener(mOnClickListener);

        mUpdateHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String chatLine = msg.getData().getString("msg");
                addChatLine(chatLine);
            }
        };

        mConnection = new ChatConnection(mUpdateHandler);

        mNsdHelper = new NsdHelper(this.getContext());
        mNsdHelper.initializeNsd();
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_register:
                    clickRegister(v);
                    break;
                case R.id.btn_discover:
                    clickDiscover(v);
                    break;
                case R.id.btn_connect:
                    clickConnect(v);
                    break;
                case R.id.btn_send:
                    clickSend(v);
                    break;
            }
        }
    };

    public void clickRegister(View v) {
        // Register service
        if (mConnection.getLocalPort() > -1) {
            mNsdHelper.registerService(mConnection.getLocalPort());
        } else {
            log.d("ServerSocket isn't bound.");
        }
    }

    public void clickDiscover(View v) {
        mNsdHelper.discoverServices();
    }

    public void clickConnect(View v) {
        NsdServiceInfo service = mNsdHelper.getChosenServiceInfo();
        if (service != null) {
            log.d("Connecting.");
            mConnection.connectToServer(service.getHost(),
                    service.getPort());
        } else {
            log.d("No service to connect to!");
        }
    }

    public void clickSend(View v) {
        EditText messageView = (EditText) rootView.findViewById(R.id.chatInput);
        if (messageView != null) {
            String messageString = messageView.getText().toString();
            if (!messageString.isEmpty()) {
                mConnection.sendMessage(messageString);
            }
            messageView.setText("");
        }
    }

    public void addChatLine(String line) {
        mStatusView.append("\n" + line);
    }

    @Override
    public void onPause() {
        if (mNsdHelper != null) {
            //mNsdHelper.stopDiscovery();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mNsdHelper != null) {
            //mNsdHelper.discoverServices();
        }
    }

    @Override
    public void onDestroy() {
        mNsdHelper.tearDown();
        mConnection.tearDown();
        super.onDestroy();
    }
}
