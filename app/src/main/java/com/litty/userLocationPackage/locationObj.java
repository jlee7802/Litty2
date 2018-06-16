package com.litty.userLocationPackage;

import android.os.Parcel;
import android.os.Parcelable;

public class locationObj implements Parcelable {
    private String locationName;
    private int mCount;
    private int fCount;
    private int mfCount;
    private String locationDesc;

    public locationObj(String locationName, int mCount, int fCount, int mfCount, String locationDesc) {
        this.locationName = locationName;
        this.mCount = mCount;
        this.fCount = fCount;
        this.mfCount = mfCount;
        this.locationDesc = locationDesc;
    }

    public locationObj(Parcel source) {
        locationName = source.readString();
        mCount = source.readInt();
        fCount = source.readInt();
        mfCount = source.readInt();
        locationDesc = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String locationName() {
        return locationName;
    }

    public int mCount() {
        return mCount;
    }

    public int fCount() {
        return fCount;
    }

    public int mfCount() {
        return mfCount;
    }

    public String locationDesc() {
        return locationDesc;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(locationName);
        dest.writeInt(mCount);
        dest.writeInt(fCount);
        dest.writeInt(mfCount);
        dest.writeString(locationDesc);
    }

    public static final Parcelable.Creator<locationObj> CREATOR = new Parcelable.Creator<locationObj>() {
        public locationObj createFromParcel(Parcel source) {
            return new locationObj(source);
        }
        public locationObj[] newArray(int size) {
            return new locationObj[size];
        }
    };
}
