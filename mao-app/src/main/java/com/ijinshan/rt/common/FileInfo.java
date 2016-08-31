package com.ijinshan.rt.common;

import android.os.Parcel;
import android.os.Parcelable;

public class FileInfo implements Parcelable {

    public String fullPath;
    public long size;
    public long create_time;
    public long modify_time;

    public FileInfo() {

    }

    public FileInfo(Parcel p) {
        fullPath = p.readString();
        size = p.readLong();
        create_time = p.readLong();
        modify_time = p.readLong();
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
    }

    public static final Parcelable.Creator<FileInfo> CREATOR = new Parcelable.Creator<FileInfo>() {
        public FileInfo createFromParcel(Parcel p) {
            return new FileInfo(p);
        }

        public FileInfo[] newArray(int size) {
            return new FileInfo[size];
        }
    };
}
