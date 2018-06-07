package com.litty.userLocationPackage;

public class userLocation {
    private Double latitude;
    private Double longitude;
    private int user_id;

    public userLocation(Double latitude, Double longitude, int user_id){
        this.latitude = latitude;
        this.longitude = longitude;
        this.user_id = user_id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public int getUserId() {
        return user_id;
    }
}
