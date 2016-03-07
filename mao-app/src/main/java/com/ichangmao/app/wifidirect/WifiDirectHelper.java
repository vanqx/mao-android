package com.ichangmao.app.wifidirect;

import android.annotation.TargetApi;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.GroupInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Build;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by yangchangmao on 2016/2/26.
 */

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class WifiDirectHelper {
    private static Method setDeviceName;
    private static final long REQUEST_TIMEOUT = 5 * 1000;

    static {
        Class<?> cls = WifiP2pManager.class;
        for (Method method : cls.getMethods()) {
            String methodName = method.getName();
            if ("setDeviceName".equals(methodName)) {
                setDeviceName = method;
            }
        }
    }

    WifiP2pManager mWifiP2pManager;
    WifiP2pManager.Channel mChannel;

    public WifiDirectHelper(WifiP2pManager wifiP2pManager, WifiP2pManager.Channel channel) {
        mWifiP2pManager = wifiP2pManager;
        mChannel = channel;
    }


    public boolean setDeviceName(String devName) {
        try {
            setDeviceName.invoke(mWifiP2pManager, mChannel, devName, null);
            return true;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public WifiP2pDeviceList getWifiP2pDeviceList() {
        SnapPeerListListener snapPeerListListener = new SnapPeerListListener();
        synchronized (snapPeerListListener.mRequestLock) {
            mWifiP2pManager.requestPeers(mChannel, snapPeerListListener);
            try {
                snapPeerListListener.mRequestLock.wait(REQUEST_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return snapPeerListListener.mWifiP2pDeviceList;
        }
    }

    private final class SnapPeerListListener implements PeerListListener {

        public final Object mRequestLock = new Object();
        public WifiP2pDeviceList mWifiP2pDeviceList = null;

        @Override
        public void onPeersAvailable(WifiP2pDeviceList peers) {
            synchronized (mRequestLock) {
                mWifiP2pDeviceList = peers;
                mRequestLock.notify();
            }
        }
    }

    public WifiP2pInfo getWifiP2pInfo() {
        SnapConnectionInfoListener snapConnectionInfoListener = new SnapConnectionInfoListener();
        synchronized (snapConnectionInfoListener.mRequestLock) {
            mWifiP2pManager.requestConnectionInfo(mChannel, snapConnectionInfoListener);
            try {
                snapConnectionInfoListener.mRequestLock.wait(REQUEST_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return snapConnectionInfoListener.mWifiP2pInfo;
        }
    }

    private final class SnapConnectionInfoListener implements ConnectionInfoListener {
        public final Object mRequestLock = new Object();
        public WifiP2pInfo mWifiP2pInfo = null;

        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            synchronized (mRequestLock) {
                mWifiP2pInfo = info;
                mRequestLock.notify();
            }
        }
    }

    public WifiP2pGroup getWifiP2pGroup() {
        SnapGroupInfoListener groupInfoListener = new SnapGroupInfoListener();
        synchronized (groupInfoListener.mRequestLock) {
            mWifiP2pManager.requestGroupInfo(mChannel, groupInfoListener);
            try {
                groupInfoListener.mRequestLock.wait(REQUEST_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return groupInfoListener.mWifiP2pGroup;
        }
    }

    private final class SnapGroupInfoListener implements GroupInfoListener {

        public final Object mRequestLock = new Object();
        public WifiP2pGroup mWifiP2pGroup = null;

        @Override
        public void onGroupInfoAvailable(WifiP2pGroup group) {
            synchronized (mRequestLock) {
                mWifiP2pGroup = group;
                mRequestLock.notify();
            }
        }
    }
}
