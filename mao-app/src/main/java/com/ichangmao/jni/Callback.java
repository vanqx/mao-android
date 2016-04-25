package com.ichangmao.jni;

/**
 * Created by yangchangmao on 2016/4/25.
 */
public class Callback {
    private String name;

    public Callback(String name) {
        this.name = name;
    }

    public void callback(int index) {
        //System.out.println("jni callback " + index);
    }
}
