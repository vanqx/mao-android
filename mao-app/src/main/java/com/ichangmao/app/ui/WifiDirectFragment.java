package com.ichangmao.app.ui;


import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;

import com.ichangmao.app.R;
import com.ichangmao.app.wifidirect.WiFiDirectBroadcastReceiver;
import com.ichangmao.app.wifidirect.WifiDirectHelper;
import com.ichangmao.commons.MaoLog;

public class WifiDirectFragment extends Fragment {

    MaoLog log = MaoLog.getLogger(this.getClass().getSimpleName());
    WiFiDirectBroadcastReceiver mWiFiDirectBroadcastReceiver;
    WifiDirectHelper mWifiDirectHelper;
    WifiP2pDevice myDevice;
    EditText txt_device_name;
    Button btn_save;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mWifiDirectHelper = new WifiDirectHelper(getContext());
        View rootView = inflater.inflate(R.layout.fragment_wifi_direct, container, false);

        txt_device_name = (EditText) rootView.findViewById(R.id.txt_device_name);
        txt_device_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                btn_save.setEnabled(s.length() > 0);
            }
        });

        btn_save = (Button) rootView.findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String devName = txt_device_name.getText().toString();
                mWifiDirectHelper.setDeviceName(devName);
            }
        });

        rootView.findViewById(R.id.btn_device_list).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        WifiP2pDeviceList wifiP2pDeviceList = mWifiDirectHelper.getWifiP2pDeviceList();
                        int i = 0;
                        for (WifiP2pDevice device : wifiP2pDeviceList.getDeviceList()) {
                            i++;
                            log.i(i + " --> " + device.toString());
                        }
                    }
                }.start();
            }
        });

        rootView.findViewById(R.id.btn_connection_info).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        WifiP2pInfo wifiP2pInfo = mWifiDirectHelper.getConnectionInfo();
                        log.i(wifiP2pInfo.toString());
                    }
                }.start();
            }
        });

        rootView.findViewById(R.id.btn_p2p_group).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        WifiP2pGroup wifiP2pGroup = mWifiDirectHelper.getWifiP2pGroup();
                        log.i(wifiP2pGroup == null ? "null" : wifiP2pGroup.toString());
                    }
                }.start();
            }
        });

        registerReceiver();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        getContext().unregisterReceiver(mWiFiDirectBroadcastReceiver);
        super.onDestroyView();
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mWiFiDirectBroadcastReceiver = new WiFiDirectBroadcastReceiver(this);
        getContext().registerReceiver(mWiFiDirectBroadcastReceiver, intentFilter);
    }

    public void updateThisDevice(WifiP2pDevice device) {
        myDevice = device;
        if (TextUtils.isEmpty(txt_device_name.getText())) {
            txt_device_name.setText(device.deviceName);
        }
    }

}
