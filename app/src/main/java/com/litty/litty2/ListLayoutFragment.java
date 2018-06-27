package com.litty.litty2;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.litty.userLocationPackage.locationObj;
import com.litty.userLocationPackage.locationObjParcelable;

import java.util.List;

public class ListLayoutFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.activity_main, container, false);
        final RelativeLayout listLayout = view.findViewById(R.id.listLayout);

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
                gd.setStroke(5, Color.WHITE);

                listLayout.setBackgroundColor(Color.BLACK);
                listLayoutItem.setBackground(gd);
                listLayoutItem.setId(layoutId);

                listLayoutItem.setLayoutParams(liParams);
                listLayout.addView(listLayoutItem);

                // Add image and text for List item.
                RelativeLayout layout = view.findViewById(R.id.listLayout);
                int liWidth  = layout.getMeasuredWidth();
                int imageWidth = liWidth/4;

                RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(imageWidth, ViewGroup.LayoutParams.MATCH_PARENT);
                RelativeLayout.LayoutParams textTitleParams = new RelativeLayout.LayoutParams(liWidth - imageWidth, ViewGroup.LayoutParams.MATCH_PARENT);
                RelativeLayout.LayoutParams textDescParams = new RelativeLayout.LayoutParams(liWidth - imageWidth, ViewGroup.LayoutParams.MATCH_PARENT);

                imageParams.setMargins(8,21,8,21);
                textTitleParams.setMargins(imageWidth + 100, 20, 8, 130);
                textDescParams.setMargins(imageWidth + 100, 100, 8, 30);

                TextView titleTV = new TextView(getContext());
                titleTV.setText(location.locationName());
                titleTV.setTextColor(Color.WHITE);
                titleTV.setTextSize(20);
                titleTV.setTag("titleTV");
                titleTV.setLayoutParams(textTitleParams);
                listLayoutItem.addView(titleTV);

                TextView descTV = new TextView(getContext());
                descTV.setText(location.locationDesc());
                descTV.setTextColor(Color.WHITE);
                descTV.setTag("descTV");
                descTV.setLayoutParams(textDescParams);
                listLayoutItem.addView(descTV);

                ImageView liPhoto = new ImageView(getContext());
                liPhoto.setImageResource(R.drawable.leopard);
                liPhoto.setTag("liPhoto");
                liPhoto.setLayoutParams(imageParams);
                listLayoutItem.addView(liPhoto);

                TextView maleTV = new TextView(getContext());
                maleTV.setText(String.valueOf(Math.round(((double)location.mCount()/(double)location.mfCount())*100)));
                maleTV.setTag("maleTV");
                listLayoutItem.addView(maleTV);

                TextView femaleTV = new TextView(getContext());
                femaleTV.setText(String.valueOf(Math.round(((double)location.fCount()/(double)location.mfCount())*100)));
                femaleTV.setTag("femaleTV");
                listLayoutItem.addView(femaleTV);

                TextView addressTV = new TextView(getContext());
                addressTV.setText(location.address());
                addressTV.setTag("addressTV");
                listLayoutItem.addView(addressTV);

                TextView businessHoursTV = new TextView(getContext());
                businessHoursTV.setText(location.businessHours());
                businessHoursTV.setTag("businessHoursTV");
                listLayoutItem.addView(businessHoursTV);

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
            ViewTreeObserver vto = topDescLayout.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    topDescLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                    TextView titleTV = view.findViewById(R.id.titleTV);
                    TextView descTV = view.findViewById(R.id.descTV);
                    //ImageView topImage = view.findViewById(R.id.topImage);
                    TextView maleTV = view.findViewById(R.id.maleTV);
                    TextView femaleTV = view.findViewById(R.id.femaleTV);
                    TextView addressTV = view.findViewById(R.id.addressTV);
                    TextView businessHoursTV = view.findViewById(R.id.businessHoursTV);

                    for(int i = 0; i < listItemLayout.getChildCount(); i++) {
                        View child = listItemLayout.getChildAt(i);

                        if (child.getTag() == "titleTV")
                            titleTV.setText(((TextView)child).getText());

                        if (child.getTag() == "descTV")
                            descTV.setText(((TextView)child).getText());

                        // Need to figure out how to store the location pictures and get the set of images from layout list item. Or just add the google maps image
                     //   if (child.getTag() == "liPhoto")
                     //       topImage.setImageDrawable(((ImageView)child).getDrawable());

                        if (child.getTag() == "maleTV")
                            maleTV.setText(((TextView)child).getText());

                        if (child.getTag() == "femaleTV")
                            femaleTV.setText(((TextView)child).getText());

                        if (child.getTag() == "addressTV")
                            addressTV.setText(((TextView)child).getText());

                        if (child.getTag() == "businessHoursTV")
                            businessHoursTV.setText(((TextView)child).getText());
                    }

                    RelativeLayout rlTitle = view.findViewById(R.id.descriptionLayout_title);
                    RelativeLayout rlDesc = view.findViewById(R.id.descriptionLayout_desc);
                    RelativeLayout rlLocationDetail = view.findViewById(R.id.descriptionLayout_locationDetail);
                    RelativeLayout rlRace = view.findViewById(R.id.descriptionLayout_race);
                    RelativeLayout rlAge = view.findViewById(R.id.descriptionLayout_age);
                    RelativeLayout rlGender = view.findViewById(R.id.descriptionLayout_gender);

                    //ImageView liPhoto = view.findViewById(R.id.topImage);
                    //liPhoto.setImageResource(R.drawable.leopard);

                    return true;
                }
            });
        }
        catch(Exception e) {
            String mes = e.getMessage();
        }
    }
}
