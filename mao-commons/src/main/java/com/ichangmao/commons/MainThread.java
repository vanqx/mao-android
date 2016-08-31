package com.ichangmao.commons;

import android.os.Handler;
import android.os.Looper;

public class MainThread {
    private static Handler sHandler = new Handler(Looper.getMainLooper());

    public static void remove(Runnable runnable) {
        synchronized (MainThread.class) {
            sHandler.removeCallbacks(runnable);
        }
    }

    public static void post(Runnable runnable) {
        synchronized (MainThread.class) {
            sHandler.post(runnable);
        }

    }

    public static void postDelayed(Runnable runnable, long delay) {
        synchronized (MainThread.class) {
            sHandler.postDelayed(runnable, delay);
        }
    }

}