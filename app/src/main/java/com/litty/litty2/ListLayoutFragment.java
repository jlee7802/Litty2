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

        View view = inflater.inflate(R.layout.activity_main, container, false);
        RelativeLayout listLayout = view.findViewById(R.id.listLayout);

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            setTopLayout(view);
            locationObjParcelable locationDetail = bundle.getParcelable("location_obj_list");
            setLocationListLayout(listLayout, locationDetail.getLocationObjList());
        }

        // Inflate the layout for this fragment
        return view;
    }

    // Programmatically create Location List Layout, will pull 24 top locations but only 6 can show on the UI at a time
    public void setLocationListLayout(RelativeLayout listLayout, List<locationObj> locationList) {
        // Number of rows should be 24 initially but if the user scrolls down and reaches the end then we need to add more list items to relative layout listLayout - JL
        int layoutId = View.generateViewId();

        // Dynamically create the list items in ListLayout
        try {
            int i;
            for (locationObj location : locationList) {
                i = 0;
                RelativeLayout listLayoutItem = new RelativeLayout(getActivity());

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 250);
                if (i != 0) {
                    params.addRule(RelativeLayout.BELOW, layoutId);
                    layoutId = View.generateViewId();
                }

                TextView titleTV = new TextView(getContext());
                titleTV.setText(location.locationName());
                titleTV.setTextColor(Color.WHITE);
                listLayoutItem.addView(titleTV);

                ImageView liPhoto = new ImageView(getContext());
                liPhoto.setImageResource(R.drawable.leopard);
                listLayoutItem.addView(liPhoto);

                params.setMargins(5, 18, 5, 18);

                GradientDrawable gd = new GradientDrawable();
                gd.setCornerRadius(10);
                gd.setStroke(5, Color.WHITE);

                listLayout.setBackgroundColor(Color.BLACK);
                listLayoutItem.setBackground(gd);
                listLayoutItem.setId(layoutId);

                listLayoutItem.setLayoutParams(params);
                listLayout.addView(listLayoutItem);
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
            final LinearLayout topLayout = view.findViewById(R.id.topLayout);
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

                    ImageView liPhoto = new ImageView(getContext());
                    liPhoto.setImageResource(R.drawable.leopard);
                    LinearLayout.LayoutParams lpImage = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
                    liPhoto.setLayoutParams(lpImage);
                    topLayout.addView(liPhoto);

                    LinearLayout.LayoutParams lpDesc = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
                    topDescLayout.setLayoutParams(lpDesc);
                    topLayout.addView(topDescLayout);

                    return true;
                }
            });
        }
        catch(Exception e) {
            String mes = e.getMessage();
        }
    }
}
