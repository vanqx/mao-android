package com.ijinshan.rt.common;

import android.os.Parcel;
import android.os.Parcelable;

public class JunkFileInfo implements Parcelable {
    public String path;
    public long size;
    public long modifyDate;

    public JunkFileInfo() {

    }

    public JunkFileInfo(Parcel p) {
        path = p.readString();
        size = p.readLong();
        modifyDate = p.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeLong(size);
        dest.writeLong(modifyDate);
    }

    public static final Parcelable.Creator<JunkFileInfo> CREATOR = new Parcelable.Creator<JunkFileInfo>() {
        public JunkFileInfo createFromParcel(Parcel p) {
            return new JunkFileInfo(p);
        }

        public JunkFileInfo[] newArray(int size) {
            return new JunkFileInfo[size];
        }
    };
}
