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

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            setTopLayout(view);
            final locationObjParcelable locationDetail = bundle.getParcelable("location_obj_list");

            // Create list layout items when bottom relative layout is rendered
            ViewTreeObserver vto = listLayout.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    listLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    setLocationListLayout(listLayout, locationDetail.getLocationObjList(), view);

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
                RelativeLayout listLayoutItem = new RelativeLayout(getActivity());

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
                textTitleParams.setMargins(imageWidth + 100, 30, 8, 130);
                textDescParams.setMargins(imageWidth + 100,100, 8, 30);

                TextView titleTV = new TextView(getContext());
                titleTV.setText(location.locationName());
                titleTV.setTextColor(Color.WHITE);
                titleTV.setLayoutParams(textTitleParams);
                listLayoutItem.addView(titleTV);

                TextView descTV = new TextView(getContext());
                descTV.setText(location.locationDesc());
                descTV.setTextColor(Color.WHITE);
                descTV.setLayoutParams(textDescParams);
                listLayoutItem.addView(descTV);

                ImageView liPhoto = new ImageView(getContext());
                liPhoto.setImageResource(R.drawable.leopard);
                liPhoto.setLayoutParams(imageParams);
                listLayoutItem.addView(liPhoto);
                ++i;
            }
        }
        catch(Exception e) {
            String mes = e.getMessage();
        }
    }

    // Programmatically sets the height/position and other properties for the layouts in the top layout. This is
    // to avoid nested sum weights with Linear layouts which can cause performance issues
    public void setTopLayout(final View view) {
        try {
            final RelativeLayout topDescLayout = view.findViewById(R.id.descriptionLayout);
            ViewTreeObserver vto = topDescLayout.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    topDescLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                    int h = topDescLayout.getMeasuredHeight();
                    int topLayoutDescRowHeight = h / 3; //Need to figure out if there is a way to dynamically get number of rows instead of hardcoding - JL

                    RelativeLayout rlGender = view.findViewById(R.id.descriptionLayout_gender);
                    RelativeLayout rlRace = view.findViewById(R.id.descriptionLayout_race);
                    RelativeLayout rlAge = view.findViewById(R.id.descriptionLayout_age);

                    RelativeLayout.LayoutParams paramsGender = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, topLayoutDescRowHeight);
                    rlGender.setLayoutParams(paramsGender);

                    RelativeLayout.LayoutParams paramsRace = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, topLayoutDescRowHeight);
                    paramsRace.addRule(RelativeLayout.BELOW, R.id.descriptionLayout_gender);
                    rlRace.setLayoutParams(paramsRace);

                    RelativeLayout.LayoutParams paramsAge = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, topLayoutDescRowHeight);
                    paramsAge.addRule(RelativeLayout.BELOW, R.id.descriptionLayout_race);
                    rlAge.setLayoutParams(paramsAge);

                    ImageView liPhoto = view.findViewById(R.id.topImage);
                    liPhoto.setImageResource(R.drawable.leopard);

                    return true;
                }
            });
        }
        catch(Exception e) {
            String mes = e.getMessage();
        }
    }
}
