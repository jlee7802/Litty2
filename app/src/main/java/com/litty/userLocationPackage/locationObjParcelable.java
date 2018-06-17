package com.litty.userLocationPackage;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class locationObjParcelable implements Parcelable {
    private List<locationObj> locationObjList;

    public locationObjParcelable(List<locationObj> objList) {
        this.locationObjList = objList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public locationObjParcelable(Parcel source) {
        source.readList(locationObjList, locationObj.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(locationObjList);
    }

    public static final Parcelable.Creator<locationObjParcelable> CREATOR = new Parcelable.Creator<locationObjParcelable>() {
        public locationObjParcelable createFromParcel(Parcel source) {
            return new locationObjParcelable(source);
        }
        public locationObjParcelable[] newArray(int size) {
            return new locationObjParcelable[size];
        }
    };

    public List<locationObj> getLocationObjList() {
        return locationObjList;
    }
}
