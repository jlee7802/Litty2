package com.litty.litty2;

import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.app.Activity;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import android.content.pm.PackageManager;

public class MainActivity extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener{
    private FusedLocationProviderClient mFusedLocationClient;
    protected LocationCallback mLocationCallback = new LocationCallback();
    protected Location mCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        /*ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);*/

        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback,null);

            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        TextView textView = findViewById(R.id.textView);
                        textView.setText(String.valueOf(999));
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        TextView textView = findViewById(R.id.textView);
                        textView.setText(String.valueOf(99999));

                        TextView textView2 = findViewById(R.id.textView3);
                        textView2.setText(String.valueOf(location.getLatitude()));
                    }
                };
            };
        }
        else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

          /*  Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);*/ //Might not need this
        }
    }

    public void onConnected(Bundle conn) {

    }

    public void onConnectionFailed(ConnectionResult result) {

    }

    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
    }

    public void onConnectionSuspended(int cause) {

    }
}
