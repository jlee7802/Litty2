package com.litty.litty2;

import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.util.Log;
import android.content.pm.PackageManager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

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
import com.litty.userLocationPackage.locationObj;
import com.litty.userLocationPackage.userLocation;
import com.litty.userLocationPackage.userLocationInterface;

import java.lang.ref.WeakReference;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener{

    public FusedLocationProviderClient mFusedLocationClient;
    public Location mCurrentLocation;
    public static userLocation uLocation;
    public static List<locationObj> locationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setTopLayout();
        /*ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);*/

        startLocationUpdates();
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
                    waitForDebugger();
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

            //call top location  list async here
            new getTopLocationsTask(MainActivity.this).execute();
            setLocationListLayout();
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

    // async task to store location on user (latitude and longitude) in users table
    private static class locationTask extends AsyncTask<userLocation, Void, Void>{
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

            uLocation = new userLocation(location.getLatitude(),location.getLongitude(),1); // Need to figure out how to get user id when user logs in
        }

        @Override
        protected Void doInBackground(userLocation... params) {
            // invoke "userLocationInterface" method. In case it fails, it will throw a
            // LambdaFunctionException.
            try {
                //waitForDebugger();
                return userLocationInterface.updateUserLocation(params[0]);
            } catch (LambdaFunctionException lfe) {
                Log.e("TAG", "Failed to invoke updateUserLocation", lfe);
                return null;
            }
        }
    }

    // Method to get top locations based on male and female count(mfCount) at location.
    private static class getTopLocationsTask extends AsyncTask<Void, Void, List<locationObj>>{
        private WeakReference<MainActivity> activityReference; //Determine if I need this this to resolve memory leak
        userLocationInterface userLocationInterface;

        // only retain a weak reference to the activity
        getTopLocationsTask(MainActivity context) {
            activityReference = new WeakReference<>(context);

            // Create an instance of CognitoCachingCredentialsProvider
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    context.getApplicationContext(), "us-east-1:caa8736d-fa24-483f-bf6a-4ee5b4da1436", Regions.US_EAST_1);

            // Create LambdaInvokerFactory, to be used to instantiate the Lambda proxy.
            LambdaInvokerFactory factory = new LambdaInvokerFactory(context.getApplicationContext(), Regions.US_EAST_1, credentialsProvider);

            // Create the Lambda proxy object with default Json data binder.
            // You can provide your own data binder by implementing
            // LambdaDataBinder
            userLocationInterface = factory.build(userLocationInterface.class);
        }

        @Override
        protected List<locationObj> doInBackground(Void... params) {
            // invoke "userLocationInterface" method. In case it fails, it will throw a
            // LambdaFunctionException.
            try {
                waitForDebugger();
                return userLocationInterface.getTopMFCountLocations();
            } catch (LambdaFunctionException lfe) {
                Log.e("TAG", "Failed to invoke getTopMFCountLocations", lfe);
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<locationObj> result) {
            if (result == null) {
                return;
            }
            locationList = result;
        }
    }

    // Programmatically sets the height/position and other properties for the layouts in the top layout. This is
    // to avoid nested sum weights with Linear layouts which can cause performance issues
    public void setTopLayout() {
        final RelativeLayout topDescLayout = findViewById(R.id.descriptionLayout);
        ViewTreeObserver vto = topDescLayout.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                topDescLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                int h = topDescLayout.getMeasuredHeight();
                int topLayoutDescRowHeight = h/3; //Need to figure out if there is a way to dynamically get number of rows instead of hardcoding - JL

                RelativeLayout rlGender = findViewById(R.id.descriptionLayout_gender);
                RelativeLayout rlRace = findViewById(R.id.descriptionLayout_race);
                RelativeLayout rlAge = findViewById(R.id.descriptionLayout_age);

                RelativeLayout.LayoutParams paramsGender = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, topLayoutDescRowHeight);
                rlGender.setLayoutParams(paramsGender);

                RelativeLayout.LayoutParams paramsRace = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, topLayoutDescRowHeight);
                paramsRace.addRule(RelativeLayout.BELOW, R.id.descriptionLayout_gender);
                rlRace.setLayoutParams(paramsRace);

                RelativeLayout.LayoutParams paramsAge = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, topLayoutDescRowHeight);
                paramsAge.addRule(RelativeLayout.BELOW, R.id.descriptionLayout_race);
                rlAge.setLayoutParams(paramsAge);
                return true;
            }
        });
    }

    // Programmatically create Location List Layout, will pull 24 top locations but only 6 can show on the UI at a time
    public void setLocationListLayout() {
        RelativeLayout listLayout = findViewById(R.id.listLayout);
        int rowNum = 6;  // This number should be 24 initially but if the user scrolls down and reaches the end then we need to add more to relative layout listLayout - JL
        int layoutId = View.generateViewId();

        // Dynamically create the relative layout items in ListLayout
        for (int i = 0; i < rowNum; i++) {
            RelativeLayout listItem = new RelativeLayout(this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 250);
            if (i != 0) {
                params.addRule(RelativeLayout.BELOW, layoutId);
                layoutId = View.generateViewId();
            }

            if (i % 2 == 0)
                listItem.setBackgroundColor(Color.RED);
            else
                listItem.setBackgroundColor(Color.BLUE);

            listItem.setId(layoutId);

            listItem.setLayoutParams(params);
            listLayout.addView(listItem);
        }

    }
}
