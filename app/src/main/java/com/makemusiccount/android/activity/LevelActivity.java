package com.makemusiccount.android.activity;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makemusiccount.android.R;
import com.makemusiccount.android.ui.CustomProgressViewGroup;
import com.makemusiccount.android.util.Util;

public class LevelActivity extends AppCompatActivity {

    CustomProgressViewGroup customProgressViewGroup;

    public static int child_count=60;
    public static int total_wave_count=60;
    public static int progress_wave_count=5-1;
    public static String usercoin="";
    public static String user_level_title="";
    RelativeLayout llForwidth;
    TextView userCoin;
    TextView UserLevel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Util.getTheme(this));
        setContentView(R.layout.activity_level);
        userCoin=findViewById(R.id.userCoin);
        UserLevel=findViewById(R.id.UserLevel);
        userCoin.setText(usercoin);
        UserLevel.setText(user_level_title);
        customProgressViewGroup=findViewById(R.id.customProgressViewGroup);
        customProgressViewGroup.setLayoutParams(new RelativeLayout.LayoutParams(total_wave_count*300, FrameLayout.LayoutParams.MATCH_PARENT));
      //  llForwidth=findViewById(R.id.llForwidth);
        for(int i = 0; i < child_count; i++){
            View view = getLayoutInflater().inflate(R.layout.layout_animation_icon,customProgressViewGroup,false);
            TextView imageView=view.findViewById(R.id.imageView);
            TextView textView=view.findViewById(R.id.textView);
            if(i<(progress_wave_count))
            {
                TypedValue typedValue3=new TypedValue();
                Resources.Theme theme3=getTheme();
                theme3.resolveAttribute(R.attr.democolor4,typedValue3,true);
                @ColorInt int color3 =typedValue3.data;
                imageView.setBackgroundTintList(ColorStateList.valueOf(color3));
            }
            else
            {
                TypedValue typedValue3=new TypedValue();
                Resources.Theme theme3=getTheme();
                theme3.resolveAttribute(R.attr.democolor3,typedValue3,true);
                @ColorInt int color3 =typedValue3.data;
                imageView.setBackgroundTintList(ColorStateList.valueOf(color3));
            }

            if(i==progress_wave_count)
            {
                textView.setVisibility(View.VISIBLE);
            }
            imageView.setText((i+1)+"");
            customProgressViewGroup.addView(view);
        }
        findViewById(R.id.ivback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }
}