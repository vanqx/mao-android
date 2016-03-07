package com.ichangmao.app.wifidirect;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;

import com.cleanmaster.snapshare.util.CmLog;
import com.cleanmaster.snapshare.util.ShareBaseReceiver;

/**
 * Created by yangchangmao on 2016/2/25.
 */
public class WiFiDirectBroadcastReceiver extends ShareBaseReceiver {
    CmLog log = CmLog.getLogger(this.getClass().getSimpleName());

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel, WiFiDirectGroupServer p2pGroupServer) {
        super();
    }

    @Override
    public void onReceiveInter(Context context, Intent intent) {

    }

    @Override
    public void onReceiveInterAsync(Context context, Intent intent) {
        String action = intent.getAction();
        log.d("action:" + action);
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            log.d("state:" + state);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                //activity.setIsWifiP2pEnabled(true);
            } else {
                //activity.setIsWifiP2pEnabled(false);
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // The peer list has changed!  We should probably do something about
            // that.

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            // Connection state changed!  We should probably do something about
            // that.

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
//            DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager()
//                    .findFragmentById(R.id.frag_list);
//            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
//                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));

        }
    }
}
