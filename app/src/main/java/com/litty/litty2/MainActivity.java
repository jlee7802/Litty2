package com.litty.litty2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.content.pm.PackageManager;
import android.view.View;

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

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener{

    public FusedLocationProviderClient mFusedLocationClient;
    public Location mCurrentLocation;
    public static userLocation uLocation;
    static boolean accessFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //final View loginView = (View)((Activity)this).FindViewById(R.id.loginLayout);

        /*ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);*/

        startLocationUpdates();

        CardView card_view = findViewById(R.id.cardViewLogin);
        card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do whatever you want to do on click (to launch any fragment or activity you need to put intent here.)
                // Need Logic here to check if login credentials are found in users table
                if (accessFlag == true) {
                    //Go to main list view
                }
                else {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Login Issue");
                    alertDialog.setMessage("Incorrect username and password combination.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
        });
    }

    protected void startLocationUpdates() {
        // If permission granted then start location update. -JL
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(50000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationCallback mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    //waitForDebugger();
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {

                        // Need to create write to aws database here by invoking lambda function to updateUserLocation
                        try {
                            new locationTask(MainActivity.this, location).execute(uLocation);
                        }
                        catch (Exception e){
                            // Need to figure out what to do if there is an error
                            String ee = e.getMessage();
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

    private static class locationTask extends AsyncTask<userLocation, Void, String>{
        private WeakReference<MainActivity> activityReference; //Determine if I need this this to resolve memory leak
        userLocationInterface userLocationInterface;

        // only retain a weak reference to the activity
        locationTask(MainActivity context, Location location) {
            activityReference = new WeakReference<>(context);

            // Create an instance of CognitoCachingCredentialsProvider
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    context.getApplicationContext(), "us-east-1:caa8736d-fa24-483f-bf6a-4ee5b4da1436", Regions.US_EAST_1);

            // Create LambdaInvokerFactory, to be used to instantiate the Lambda proxy.
            LambdaInvokerFactory factory = new LambdaInvokerFactory(context.getApplicationContext(), Regions.US_EAST_1, credentialsProvider);
            //LambdaInvokerFactory factory = LambdaInvokerFactory.build().context(context).region(Regions.US_EAST_1).credentialsProvider(credentialsProvider).build();

            // Create the Lambda proxy object with default Json data binder.
            // You can provide your own data binder by implementing
            // LambdaDataBinder
            userLocationInterface = factory.build(userLocationInterface.class);

            uLocation = new userLocation(String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()),String.valueOf(1)); // Need to figure out how to get user id when user logs in
        }

        @Override
        protected String doInBackground(userLocation... params) {
            // invoke "echo" method. In case it fails, it will throw a
            // LambdaFunctionException.
            try {
                waitForDebugger();
                return userLocationInterface.updateUserLocation(params[0]);
            } catch (LambdaFunctionException lfe) {
                Log.e("TAG", "Failed to invoke updateUserLocation", lfe);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                return;
            }
        }
    }
}
