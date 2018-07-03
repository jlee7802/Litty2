package com.litty.userLocationPackage;

import android.os.Parcel;
import android.os.Parcelable;

public class locationObj implements Parcelable {
    private int locationId;
    private String locationName;
    private int mCount;
    private int fCount;
    private int mfCount;
    private String locationDesc;
    private String address;
    private String businessHours;
    private double locationLat;
    private double locationLong;

    public locationObj(){

    }

    public locationObj(int locationId, String locationName, int mCount, int fCount, int mfCount, String locationDesc, String address, String businessHours, double locationLat, double locationLong) {
        this.locationId = locationId;
        this.locationName = locationName;
        this.mCount = mCount;
        this.fCount = fCount;
        this.mfCount = mfCount;
        this.locationDesc = locationDesc;
        this.address = address;
        this.businessHours = businessHours;
        this.locationLat = locationLat;
        this.locationLong = locationLong;
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

    public int locationId() { return locationId; }

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

    public String address() {
        return address;
    }

    public String businessHours() {
        return businessHours;
    }

    public double locationLat() { return locationLat; }

    public double locationLong() { return locationLong; }

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
