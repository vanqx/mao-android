package com.ichangmao.app.wifidirect;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.HandlerThread;

import com.ichangmao.commons.MaoLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by yangchangmao on 2016/2/26.
 */
public class Wifip2pHelper {

    MaoLog log = MaoLog.getLogger("Wifip2pHelper");

    WifiP2pManager mWifiP2pManager;
    WifiP2pManager.Channel mChannel;
    HandlerThread mHandlerThread = new HandlerThread("Wifip2pHelperThread");

    private static Method setDeviceName;

    static {
        Class<?> cls = WifiP2pManager.class;
        for (Method method : cls.getMethods()) {
            String methodName = method.getName();
            if ("setDeviceName".equals(methodName)) {
                setDeviceName = method;
            }
        }
    }

    public Wifip2pHelper(Context context) {
        mWifiP2pManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);

        mHandlerThread.start();
        mChannel = mWifiP2pManager.initialize(context, mHandlerThread.getLooper(), new WifiP2pManager.ChannelListener() {
            @Override
            public void onChannelDisconnected() {
                log.i("onChannelDisconnected");
            }
        });
    }

    public void setDeviceName(String devName) {
        try {
            setDeviceName.invoke(mWifiP2pManager, mChannel, devName, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
