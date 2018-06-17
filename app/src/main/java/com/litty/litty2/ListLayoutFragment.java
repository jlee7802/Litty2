package com.litty.litty2;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
            for (locationObj ii : locationList) {
                int i = 0;
                RelativeLayout listLayoutItem = new RelativeLayout(getActivity());

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 250);
                if (i != 0) {
                    params.addRule(RelativeLayout.BELOW, layoutId);
                    layoutId = View.generateViewId();
                }

                TextView titleTV = new TextView(getContext());
                //titleTV.setText(location.locationName());

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
}
