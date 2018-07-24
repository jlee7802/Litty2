package com.litty.litty2;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.litty.userLocationPackage.locationObj;
import com.litty.userLocationPackage.locationObjParcelable;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;

public class ListLayoutFragment extends Fragment implements OnMapReadyCallback {

    public List<locationObj> topLocationList = new ArrayList<>();
    public locationObj currLocObj = new locationObj();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.activity_main, container, false);
        final RelativeLayout listLayout = view.findViewById(R.id.listLayout);

        //SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);

        // Retrieve Parcelable object containing list of locations from Main Activity
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            final locationObjParcelable locationDetail = bundle.getParcelable("location_obj_list");

            // Create list layout items when bottom relative layout is rendered
            ViewTreeObserver vto = listLayout.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    listLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    setLocationListLayout(listLayout, locationDetail.getLocationObjList(), view);

                    // Display top location from location list on topLayout
                    int id = getResources().getIdentifier(String.valueOf(1), "id", getActivity().getPackageName());
                    setTopLayout(view, (RelativeLayout) getView().findViewById(R.id.listLayout).findViewById(id));
                }
            });
        }

        // Inflate the layout for this fragment
        return view;
    }

    // Programmatically create Location List Layout, will pull 24 top locations but only 6 can show on the UI at a time
    public void setLocationListLayout(RelativeLayout listLayout, List<locationObj> locationList, View view) {
        // Number of rows should be 24 initially but if the user scrolls down and reaches the end then we need to add more list items to relative layout listLayout - JL
        int layoutId = View.generateViewId();
        topLocationList = locationList;

        // Dynamically create the list items in ListLayout
        try {
            int i = 0;
            for (locationObj location : locationList) {
                final RelativeLayout listLayoutItem = new RelativeLayout(getActivity());

                RelativeLayout.LayoutParams liParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 250);

                if (i != 0) {
                    liParams.addRule(RelativeLayout.BELOW, layoutId);
                    layoutId = View.generateViewId();
                }

                liParams.setMargins(5, 18, 5, 18);

                GradientDrawable gd = new GradientDrawable();
                gd.setCornerRadius(10);
                gd.setStroke(5, Color.parseColor("#946bff"));

                listLayout.setBackgroundColor(Color.BLACK);
                listLayoutItem.setBackground(gd);
                listLayoutItem.setId(layoutId);
                listLayout.setPadding(5, 7,5,7);

                listLayoutItem.setLayoutParams(liParams);
                listLayout.addView(listLayoutItem);

                // Add image and text for List item.
                RelativeLayout layout = view.findViewById(R.id.listLayout); // Do i need this? can't i just use the input parameter
                int liWidth  = layout.getMeasuredWidth();
                int oneWidth = liWidth/4;

                //RelativeLayout.LayoutParams mapParams = new RelativeLayout.LayoutParams(imageWidth, ViewGroup.LayoutParams.MATCH_PARENT);
                RelativeLayout.LayoutParams textTitleParams = new RelativeLayout.LayoutParams(oneWidth*3, ViewGroup.LayoutParams.MATCH_PARENT);
                RelativeLayout.LayoutParams textDescParams = new RelativeLayout.LayoutParams(oneWidth*3, ViewGroup.LayoutParams.MATCH_PARENT);
                RelativeLayout.LayoutParams distanceParams = new RelativeLayout.LayoutParams(oneWidth, ViewGroup.LayoutParams.MATCH_PARENT);

                //mapParams.setMargins(8,21,8,21);
                textTitleParams.setMargins(22, 12, 22, 12);
                textDescParams.setMargins(22, 12, 22, 12);
                distanceParams.setMargins(oneWidth*3, 21, 8, 21);

                TextView titleTV = new TextView(getContext());
                titleTV.setText(location.locationName());
                titleTV.setTextColor(Color.WHITE);
                titleTV.setTextSize(20);
                titleTV.setTag("titleTV");
                titleTV.setLayoutParams(textTitleParams);
                titleTV.setGravity(Gravity.START|Gravity.TOP);
                titleTV.setSingleLine(true);
                titleTV.setEllipsize(TextUtils.TruncateAt.END);
                listLayoutItem.addView(titleTV);

                TextView descTV = new TextView(getContext());
                descTV.setText(location.locationDesc());
                descTV.setTextColor(Color.WHITE);
                descTV.setTag("descTV");
                descTV.setLayoutParams(textDescParams);
                descTV.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);
                descTV.setSingleLine(false);
                descTV.setEllipsize(TextUtils.TruncateAt.END);
                listLayoutItem.addView(descTV);

                //Calculate distance between user and each location in locationList
                float[] distance = new float[1];
                Location.distanceBetween(40.762954, -73.9712007,location.locationLat(), location.locationLong(),distance); // Need to get the users lat/long data for the first 2 params
                TextView distanceTV = new TextView(getContext());
                distanceTV.setText(String.valueOf(Math.round(getMiles(distance[0]) * 100) /100.0)); // multiply meters by 0.000621371192 to convert meters to miles
                distanceTV.setTextColor(Color.WHITE);
                distanceTV.setTag("distanceTV");
                distanceTV.setLayoutParams(distanceParams);
                distanceTV.setGravity(Gravity.CENTER|Gravity.CENTER_VERTICAL);
                listLayoutItem.addView(distanceTV);

                TextView idTV = new TextView(getContext());
                idTV.setText(String.valueOf(location.locationId()));
                idTV.setTag("idTV");
                listLayoutItem.addView(idTV);

                // Set the latitude and longitude for location obj with the lat/long from db.
                // This will be used to create the map in the topLayout when the onMapReady listener is called.
                //locationObj locationObj = new locationObj(location.locationId(), location.locationName(), location.mCount(), location.fCount(), location.mfCount(), location.locationDesc(),
                //        location.address(), location.businessHours(), location.locationLat(), location.locationLong()); // Need to put the other fields in this object instead of putting them in textviews, delete the textviews
               // topLocationList.add(locationObj);

                listLayoutItem.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        try {
                            setTopLayout(getView(), (RelativeLayout) v);
                            ViewGroup vg = getView().findViewById(R.id.topLayout);
                            vg.invalidate();
                        }
                        catch(Exception e){
                            String af = e.getMessage();
                        }
                    }
                });

                ++i;
            }
        }
        catch(Exception e) {
            String mes = e.getMessage();
        }
    }

    // Create the widgets for topLayout, passing in view and a listItemLayout
    public void setTopLayout(final View view, final RelativeLayout listItemLayout) {
        try {
            final RelativeLayout topDescLayout = view.findViewById(R.id.descriptionLayout);
            TextView id;
            for (locationObj obj : topLocationList){
                id = listItemLayout.findViewWithTag("idTV");
                if (obj.locationId() == Integer.parseInt(id.getText().toString()))
                {
                    currLocObj = obj;
                    break;
                }
            }

            SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            ViewTreeObserver vto = topDescLayout.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    topDescLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                    TextView titleTV = view.findViewById(R.id.titleTV);
                    TextView descTV = view.findViewById(R.id.descTV);
                    TextView maleTV = view.findViewById(R.id.maleTV);
                    TextView femaleTV = view.findViewById(R.id.femaleTV);
                    TextView addressTV = view.findViewById(R.id.addressTV);

                    titleTV.setText(currLocObj.locationName());
                    descTV.setText(currLocObj.locationDesc());

                    long mCount = round(((double)currLocObj.mCount()/(double)currLocObj.mfCount())*100);
                    maleTV.setText(String.valueOf(round(((double)currLocObj.mCount()/(double)currLocObj.mfCount())*100)));
                    femaleTV.setText(String.valueOf(Math.abs(mCount - 100)));
                    addressTV.setText(currLocObj.address());

                    RelativeLayout rlTitle = view.findViewById(R.id.descriptionLayout_title);
                    RelativeLayout rlDesc = view.findViewById(R.id.descriptionLayout_desc);
                    RelativeLayout rlLocationDetail = view.findViewById(R.id.descriptionLayout_locationDetail);
                    RelativeLayout rlGender = view.findViewById(R.id.descriptionLayout_gender);

                    return true;
                }
            });
        }
        catch(Exception e) {
            String mes = e.getMessage();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng loc = new LatLng(currLocObj.locationLat(), currLocObj.locationLong());
        if (ContextCompat.checkSelfPermission( getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED )
            googleMap.setMyLocationEnabled(true);
        googleMap.addMarker(new MarkerOptions().position(loc).title("Marker in Sydney"));

        float zoomLevel = 16.0f;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, zoomLevel));
    }

    // Convert meters to miles
    public double getMiles(double i) {
        return i*0.000621371192;
    }

    // Convert miles to meters
    public double getMeters(double i) {
        return i*1609.344;
    }
}
