package com.litty.litty2;

import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.util.Log;
import android.content.pm.PackageManager;
import android.widget.LinearLayout;

import static android.os.Debug.waitForDebugger;

import com.amazonaws.mobileconnectors.lambdainvoker.*;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.gson.reflect.TypeToken;

import com.litty.userLocationPackage.MyLambdaDataBinder;
import com.litty.userLocationPackage.locationObj;
import com.litty.userLocationPackage.locationObjParcelable;
import com.litty.userLocationPackage.userLocation;
import com.litty.userLocationPackage.userLocationInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    public FusedLocationProviderClient mFusedLocationClient;
    public Location mCurrentLocation;
    public static userLocation uLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        // Create layouts and widgets for mainActivity
        LinearLayout mainLayout = findViewById(R.id.mainLayout);
        mainLayout.setBackgroundColor(Color.BLACK);

        // Get Location data
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
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
                            /*Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 5);
                            for (Address a : addresses) {
                                String me = a.getFeatureName();
                            }*/
                            //getGoogleMapsData(location);

                            new locationTask(MainActivity.this, location).execute(uLocation);
                        }
                        catch (Exception e){
                            // Need to figure out what to do if there is an error
                            String ee = e.getMessage();
                        }
                    }
                }

                @Override
                public void onLocationAvailability(LocationAvailability locationAvailability) {
                    Location l = new Location("");
                    l.setLatitude(40.7630525);
                    l.setLongitude(-73.9721337);
                    new locationTask(MainActivity.this, l).execute(uLocation);
                    //getGoogleMapsData(l);
                }
            };

            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);

            new getTopLocationsTask(this).execute();
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
        userLocationInterface userLocationInterface;

        // only retain a weak reference to the activity
        locationTask(MainActivity context, Location location) {
            // Create an instance of CognitoCachingCredentialsProvider
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    context.getApplicationContext(), "us-east-1:caa8736d-fa24-483f-bf6a-4ee5b4da1436", Regions.US_EAST_1);

            // Create LambdaInvokerFactory, to be used to instantiate the Lambda proxy.
            LambdaInvokerFactory factory = LambdaInvokerFactory.builder().context(context).region(Regions.US_EAST_1).credentialsProvider(credentialsProvider) .build();

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
                waitForDebugger();
                return userLocationInterface.updateUserLocation(params[0]);
            } catch (LambdaFunctionException lfe) {
                Log.e("TAG", "Failed to invoke updateUserLocation", lfe);
                return null;
            }
        }
    }

    // Method to get top locations based on male and female count(mfCount) at location.
    private class getTopLocationsTask extends AsyncTask<Void, Void, List<locationObj>>{ //Check if memory leak is an issue -JL
        userLocationInterface userLocationInterface;
        private WeakReference<MainActivity> activityReference;


        // only retain a weak reference to the activity
        getTopLocationsTask(MainActivity context) {
            try {
                activityReference = new WeakReference<>(context);

                // Create an instance of CognitoCachingCredentialsProvider
                CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                        context.getApplicationContext(), "us-east-1:caa8736d-fa24-483f-bf6a-4ee5b4da1436", Regions.US_EAST_1);

                // Create LambdaInvokerFactory, to be used to instantiate the Lambda proxy.
                LambdaInvokerFactory factory = LambdaInvokerFactory.builder().context(context).region(Regions.US_EAST_1).credentialsProvider(credentialsProvider) .build();

                // Create the Lambda proxy object with default Json data binder.
                // You can provide your own data binder by implementing
                // LambdaDataBinder.  This is important because the return type will be LinkedTreeMap so
                // you need to change it to the correct class type by using LambdaDataBinder - JL
                userLocationInterface = factory.build(userLocationInterface.class, new MyLambdaDataBinder(new TypeToken<List<locationObj>>() {
                }.getType()));
            }
            catch(Exception e) {
                String mes  = e.getMessage();
            }
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

            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            // Use this if i need to pull the mfCount from our database in order to populate
            // some of the fields in the locationObj. - JL
            locationObjParcelable locationDetail = new locationObjParcelable(result);

            // Add ListLayout fragment to MainActivity
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            ListLayoutFragment fragment = new ListLayoutFragment();
            fragmentTransaction.add(R.id.mainLayout, fragment);
            fragmentTransaction.commit();

            Bundle bundle = new Bundle();
            bundle.putParcelable("location_obj_list", locationDetail);
            fragment.setArguments(bundle);
        }
    }

    // Method to get the Google Maps data after making call to API with user's location data
    protected void getGoogleMapsData(Location location) {
        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        sb.append("location=" + location.getLatitude() + "," + location.getLongitude());
        sb.append("&rankby=distance");
        sb.append("&types=" + "night_club");
        sb.append("&sensor=true");
        sb.append("&key=AIzaSyCbtL331iP2ok4j8ZMwi4A7LrIhFCDvqnk");

        new JsonTask().execute(String.valueOf(sb));
    }

    protected void getGoogleMapsData(Location location, String nextPageToken) {
        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        sb.append("location=" + location.getLatitude() + "," + location.getLongitude());
        sb.append("&rankby=distance");
        sb.append("&types=" + "night_club");
        sb.append("&sensor=true");
        sb.append("&pagetoken=" + nextPageToken);
        sb.append("&key=AIzaSyCbtL331iP2ok4j8ZMwi4A7LrIhFCDvqnk");

        new JsonTask().execute(String.valueOf(sb));
    }

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            // Show progress dialog box here, in this case it will be a page
            // where we show the loading bar. Still need to create the loading page - JL
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                connection.disconnect();
                String result = buffer.toString();

                String endpoint = "";
                String port = "";
                String db_name = "";
                String username = "";
                String pw = "";
                String urls = "jdbc:postgresql://" + endpoint + ":" + port + "/" + db_name + "?user=" + username + "&password=" + pw;

                Class.forName("org.postgresql.Driver");
                Connection conn = DriverManager.getConnection(urls);
                Statement stmt = conn.createStatement();


                JSONObject obj = new JSONObject(result);
                JSONArray jArray = obj.getJSONArray("results");
                List<locationObj> locationList = new ArrayList<>();


                String nextPageToken = obj.has("next_page_token") ? obj.getString("next_page_token") : "";

                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jObject = jArray.getJSONObject(i);
                    JSONObject geometryObj = jObject.getJSONObject("geometry");
                    JSONObject lObj = geometryObj.getJSONObject("location");
                    JSONObject pObj = jObject.getJSONObject("plus_code");

                    locationObj LocationObj = new locationObj(0, jObject.getString("name"), 1, 4, 5, "blah blah",
                            jObject.getString("vicinity"), "wheneva", lObj.getDouble("lat"), lObj.getDouble("lng"));

                    locationList.add(LocationObj);

                    String placeName = jObject.getString("name");
                    if (placeName.contains("'"))
                        placeName = placeName.replace("'","''");

                    String query = "INSERT INTO location VALUES (nextval('location_location_id_seq'), '" +
                            placeName + "', 1, 0, 0, '', 0, '" + jObject.getString("vicinity") + "', '', " + lObj.getDouble("lat") + ", " + lObj.getDouble("lng") + ", '" +
                            jObject.getString("place_id") + "', '" + pObj.getString("compound_code") + "', '" + pObj.getString("global_code") + "')";
                    stmt.executeUpdate(query);
                }

                locationObjParcelable locationDetail = new locationObjParcelable(locationList);


                conn.close();
                stmt.close();

                Location l = new Location("");
                l.setLatitude(40.7630525);
                l.setLongitude(-73.9721337);

                if (!nextPageToken.equals(""))
                    getGoogleMapsData(l, nextPageToken);

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e ){
                e.printStackTrace();
            } catch (SQLException e ){
                e.printStackTrace();
            } catch (JSONException e ){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

     /*   @Override
        protected void onPostExecute(String result) {

            try {
                String endpoint = ";
                String port = "";
                String db_name = "";
                String username = "";
                String pw = "";
                String url = "jdbc:postgresql://" + endpoint + ":" + port + "/" + db_name + "?user=" + username + "&password=" + pw;

                Class.forName("org.postgresql.Driver");
                Connection conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement();


                JSONObject obj = new JSONObject(result);
                JSONArray jArray = obj.getJSONArray("results");
                List<locationObj> locationList = new ArrayList<>();*/


                //while (nextPageToken != null || nextPageToken.equals("")) {
            /*        String nextPageToken = obj.getString("next_page_token");

                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jObject = jArray.getJSONObject(i);
                        JSONObject geometryObj = jObject.getJSONObject("geometry");
                        JSONObject lObj = geometryObj.getJSONObject("location");
                        JSONObject pObj = jObject.getJSONObject("plus_code");

                        locationObj LocationObj = new locationObj(0, jObject.getString("name"), 1, 4, 5, "blah blah",
                                jObject.getString("vicinity"), "wheneva", lObj.getDouble("lat"), lObj.getDouble("lng"));

                        locationList.add(LocationObj);


                        String query = "INSERT INTO location VALUES (nextval('location_location_id_seq'), " +
                                jObject.getString("name") + "1, 0, 0, '', 0, " + jObject.getString("vicinity") + ", '', " + lObj.getDouble("lat") + ", " + lObj.getDouble("lng") + ", " +
                                jObject.getString("place_id") + ", " + pObj.getString("compound_code") + ", " + pObj.getString("global_code") + ")";
                        stmt.executeQuery(query);
                    }*/
                //}

             /*   locationObjParcelable locationDetail = new locationObjParcelable(locationList);


                conn.close();
                stmt.close();

                Location l = new Location("");
                l.setLatitude(40.7630525);
                l.setLongitude(-73.9721337);

                if (nextPageToken != null && nextPageToken.equals(""))
                    getGoogleMapsData(l, nextPageToken);*/


                // Add ListLayout fragment to MainActivity
                /*FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                ListLayoutFragment fragment = new ListLayoutFragment();
                fragmentTransaction.add(R.id.mainLayout, fragment);
                fragmentTransaction.commit();

                Bundle bundle = new Bundle();
                bundle.putParcelable("location_obj_list", locationDetail);
                fragment.setArguments(bundle);*/
      /*      }
            catch(JSONException e){
                String mes = e.getMessage();
            }
            catch(SQLException e){

            }
            catch(ClassNotFoundException e) {

            }
        }*/
    }
}