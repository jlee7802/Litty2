package com.litty.userLocationPackage;

public class locationObj {
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
}
