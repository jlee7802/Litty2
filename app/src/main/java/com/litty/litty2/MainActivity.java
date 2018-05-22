package com.litty.litty2;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.util.Log;
import android.widget.TextView;
import android.content.pm.PackageManager;
import android.widget.Toast;

import static android.os.Debug.waitForDebugger;

import com.amazonaws.mobileconnectors.lambdainvoker.*;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.litty.userLocationPackage.userLocation;
import com.litty.userLocationPackage.userLocationInterface;

import org.json.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener{

    public FusedLocationProviderClient mFusedLocationClient;
    public Location mCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        /*ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);*/

        startLocationUpdates(this);
    }

    protected void startLocationUpdates(final Context context) {
        // If permission granted then start location update. -JL
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationCallback mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    waitForDebugger();
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        TextView textView = findViewById(R.id.textView3);
                        textView.setText(String.valueOf(location.getLatitude()));

                        // Need to create write to aws database here by invoking lambda function to updateUserLocation
                        try {
                            // Create an instance of CognitoCachingCredentialsProvider
                            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                                    context.getApplicationContext(), "us-east-1:caa8736d-fa24-483f-bf6a-4ee5b4da1436", Regions.US_EAST_1);

                            // Create LambdaInvokerFactory, to be used to instantiate the Lambda proxy.
                            LambdaInvokerFactory factory = new LambdaInvokerFactory(context.getApplicationContext(),
                                    Regions.US_EAST_1, credentialsProvider);

                            // Create the Lambda proxy object with default Json data binder.
                            // You can provide your own data binder by implementing
                            // LambdaDataBinder
                            final userLocationInterface userLocationInterface = factory.build(userLocationInterface.class);

                            userLocation uLocation = new userLocation(String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()),""); // Need to figure out how to get user id when user logs in

                            // The Lambda function invocation results in a network call
                            // Make sure it is not called from the main thread
                            new AsyncTask<userLocation, Void, Void>() {
                                @Override
                                protected Void doInBackground(userLocation... params) {
                                    // invoke "echo" method. In case it fails, it will throw a
                                    // LambdaFunctionException.
                                    try {
                                        return userLocationInterface.updateUserLocation(params[0]);
                                    } catch (LambdaFunctionException lfe) {
                                        Log.e("TAG", "Failed to invoke updateUserLocation", lfe);
                                        return null;
                                    }
                                }

                                // Decide if i want to do anything after execution
                               /* @Override
                                protected void onPostExecute(String result) {
                                    if (result == null) {
                                        return;
                                    }

                                    // Do a toast
                                    Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
                                }*/
                            }.execute(uLocation);
                        }
                        catch (Exception e){
                            // Need to figure out what to do if there is an error
                        }
                    }
                }
            };

            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
        else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

          /*  Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);*/ //Might not need this
        }
    }

    @Override
    public void onConnected(Bundle conn) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
    }

    @Override
    public void onConnectionSuspended(int cause) {

    }
}
