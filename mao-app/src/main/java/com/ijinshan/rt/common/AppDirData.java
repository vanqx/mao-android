package com.ijinshan.rt.common;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class AppDirData implements Parcelable {

    public String pkgName;
    public List<AppFileInfo> fileInfos;

    public AppDirData(String pkgName, List<AppFileInfo> fileInfos) {
        this.pkgName = pkgName;
        this.fileInfos = fileInfos;
    }

    @SuppressWarnings("unchecked")
    public AppDirData(Parcel p) {
        pkgName = p.readString();
        fileInfos = p.readArrayList(AppFileInfo.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pkgName);
        dest.writeList(fileInfos);
    }

    public static final Parcelable.Creator<AppDirData> CREATOR = new Parcelable.Creator<AppDirData>() {
        public AppDirData createFromParcel(Parcel p) {
            return new AppDirData(p);
        }

        public AppDirData[] newArray(int size) {
            return new AppDirData[size];
        }
    };
}
