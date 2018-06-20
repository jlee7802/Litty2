package com.litty.litty2;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
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
                    setTopLayout(view, (RelativeLayout)view.findViewById(id));
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
                textDescParams.setMargins(imageWidth + 100,100, 8, 30);

                TextView titleTV = new TextView(getContext());
                titleTV.setText(location.locationName());
                titleTV.setTextColor(Color.WHITE);
                titleTV.setLayoutParams(textTitleParams);
                titleTV.setTextSize(20);
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

                listLayoutItem.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        setTopLayout(getView(), (RelativeLayout)v);
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
                    for(int i = 0; i < listItemLayout.getChildCount(); i++) {
                        View child = listItemLayout.getChildAt(i);
                        // your processing...
                    }

                    RelativeLayout rlTitle = view.findViewById(R.id.descriptionLayout_title);
                    RelativeLayout rlDesc = view.findViewById(R.id.descriptionLayout_desc);
                    RelativeLayout rlLocationDetail = view.findViewById(R.id.descriptionLayout_locationDetail);
                    RelativeLayout rlRace = view.findViewById(R.id.descriptionLayout_race);
                    RelativeLayout rlAge = view.findViewById(R.id.descriptionLayout_age);
                    RelativeLayout rlGender = view.findViewById(R.id.descriptionLayout_gender);

                    TextView titleTV = new TextView(getContext());
                    titleTV.setText(String.valueOf(50));
                    titleTV.setTextColor(Color.WHITE);
                    titleTV.setTextSize(20);
                    rlTitle.addView(titleTV);

                    TextView descTV = new TextView(getContext());
                    descTV.setText(String.valueOf(50));
                    descTV.setTextColor(Color.WHITE);
                    descTV.setTextSize(15);
                    rlDesc.addView(descTV);

                    RelativeLayout.LayoutParams genderParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

                    //genderParams.setMargins(21, 20, 0, 0);
                    TextView maleTV = new TextView(getContext());
                    maleTV.setText(String.valueOf(50));
                    maleTV.setTextColor(Color.WHITE);
                    maleTV.setTextSize(20);
                    maleTV.setGravity(Gravity.LEFT);
                    maleTV.setLayoutParams(genderParams);
                    rlGender.addView(maleTV);

                    //genderParams.setMargins(0, 0, 0, 0);
                    TextView femaleTV = new TextView(getContext());
                    femaleTV.setText(String.valueOf(50));
                    femaleTV.setTextColor(Color.RED);
                    femaleTV.setTextSize(20);
                    femaleTV.setGravity(Gravity.RIGHT);
                    femaleTV.setLayoutParams(genderParams);
                    rlGender.addView(femaleTV);


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
