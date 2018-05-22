package com.litty.userLocationPackage;

public class userLocation {
    private String latitude;
    private String longitude;
    private String user_id;

    public userLocation(String latitude, String longitude, String user_id){
        this.latitude = latitude;
        this.longitude = longitude;
        this.user_id = user_id;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getUserId() {
        return user_id;
    }
}
