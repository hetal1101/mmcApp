package com.makemusiccount.android.fragment;


import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.widget.NestedScrollView;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.makemusiccount.android.R;
import com.makemusiccount.android.activity.SubscribePackageActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class Subcribe_webview extends Fragment {


    public Subcribe_webview() {
        // Required empty public constructor
    }
    LinearLayout llteacher,llstudent;
    TextView tvteacher,tvstudent,tvbanner;
    NestedScrollView scroll;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
            View view=inflater.inflate(R.layout.fragment_subcribe_webview, container, false);

            llstudent=view.findViewById(R.id.llstudent);
        llteacher=view.findViewById(R.id.llteacher);
        tvteacher=view.findViewById(R.id.tvteacher);
        tvstudent=view.findViewById(R.id.tvstudent);
        tvbanner=view.findViewById(R.id.tvbanner);
        scroll=view.findViewById(R.id.scroll);
        tvbanner.setText("Thrive in math with the \nfreshest songs on the piano.");

        YoYo.with(Techniques.Pulse)
                .duration(700)
                .repeat(50)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        view.findViewById(R.id.imgscroll).setVisibility(View.GONE);
                    }
                })
                .playOn(view.findViewById(R.id.imgscroll));








        llteacher.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                TypedValue typedValue=new TypedValue();
                Resources.Theme theme=getActivity().getTheme();
                theme.resolveAttribute(R.attr.new_light,typedValue,true);
                @ColorInt int color =typedValue.data;
                llstudent.setBackground(getActivity().getResources().getDrawable(R.drawable.btn_border_new));
                llteacher.setBackgroundColor(color);
                tvstudent.setTextColor(color);
                tvteacher.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                changeFragment(new Teacher());
                tvbanner.setText("Eliminate math phobia. \n" +
                        "One song at at ime.");

            }
        });
        llstudent.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                TypedValue typedValue=new TypedValue();
                Resources.Theme theme=getActivity().getTheme();
                theme.resolveAttribute(R.attr.new_light,typedValue,true);
                @ColorInt int color =typedValue.data;
                llteacher.setBackground(getActivity().getResources().getDrawable(R.drawable.btn_border_new));
                llstudent.setBackgroundColor(color);
                tvteacher.setTextColor(color);
                tvstudent.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));

                tvbanner.setText("Thrive in math with the \nfreshest songs on the piano.");
                changeFragment(new Student());
            }
        });


        view.findViewById(R.id.tvButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), SubscribePackageActivity.class));
            }
        });
        changeFragment(new Student());
        return view;
    }

    void changeFragment(Fragment fragment)
    {

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_container12, fragment); // f1_container is your FrameLayout container
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();}


}
