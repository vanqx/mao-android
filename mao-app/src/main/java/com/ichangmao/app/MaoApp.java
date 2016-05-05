package com.ichangmao.app;

import android.app.Application;
import android.content.Context;

import com.ichangmao.commons.MaoLog;

public class MaoApp extends Application {
    private static Context mContext;

    MaoLog log = MaoLog.getLogger(this.getClass().getSimpleName());

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getBaseContext();
        log.d("onCreate");
    }

    @Override
    public void onTerminate() {
        log.d("onTerminate");
        super.onTerminate();
    }

    public static Context getContext() {
        return mContext;
    }
}
