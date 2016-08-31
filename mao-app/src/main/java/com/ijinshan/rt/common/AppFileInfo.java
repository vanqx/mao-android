package com.ijinshan.rt.common;

import android.os.Parcel;
import android.os.Parcelable;

public class AppFileInfo implements Parcelable {

    public String fullPath;
    public long size;
    public long create_time;
    public long modify_time;
    public int filetype;

    public AppFileInfo(String fullPath, long size, long create_time, long modify_time, int filetype) {
        this.fullPath = fullPath;
        this.size = size;
        this.create_time = create_time;
        this.modify_time = modify_time;
        this.filetype = filetype;
    }

    public AppFileInfo() {

    }

    public AppFileInfo(Parcel p) {
        fullPath = p.readString();
        size = p.readLong();
        create_time = p.readLong();
        modify_time = p.readLong();
        filetype = p.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fullPath);
        dest.writeLong(size);
        dest.writeLong(create_time);
        dest.writeLong(modify_time);
        dest.writeInt(filetype);
    }

    public static final Parcelable.Creator<AppFileInfo> CREATOR = new Parcelable.Creator<AppFileInfo>() {
        public AppFileInfo createFromParcel(Parcel p) {
            return new AppFileInfo(p);
        }

        public AppFileInfo[] newArray(int size) {
            return new AppFileInfo[size];
        }
    };
}
