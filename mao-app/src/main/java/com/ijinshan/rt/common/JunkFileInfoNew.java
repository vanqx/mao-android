package com.ijinshan.rt.common;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class JunkFileInfoNew implements Parcelable {
    public long size;
    public ArrayList<String> pathList = new ArrayList<String>();

    public JunkFileInfoNew() {

    }

    public JunkFileInfoNew(Parcel p) {
        try {
            size = p.readLong();
            pathList = p.readArrayList(String.class.getClassLoader());
        } catch (Exception e) {
            //读取异常，则不要这段数据
            e.printStackTrace();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(size);
        dest.writeList(pathList);
    }

    public static final Parcelable.Creator<JunkFileInfoNew> CREATOR = new Parcelable.Creator<JunkFileInfoNew>() {
        public JunkFileInfoNew createFromParcel(Parcel p) {
            return new JunkFileInfoNew(p);
        }

        public JunkFileInfoNew[] newArray(int size) {
            return new JunkFileInfoNew[size];
        }
    };
}
