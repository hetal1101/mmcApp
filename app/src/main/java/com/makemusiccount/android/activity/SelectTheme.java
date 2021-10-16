package com.makemusiccount.android.activity;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;

import com.makemusiccount.android.R;
import com.makemusiccount.android.preference.AppPersistence;
import com.makemusiccount.android.preference.AppPreference;
import com.makemusiccount.android.util.Util;
import com.makemusiccount.pianoview.entity.Piano;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class SelectTheme extends AppCompatActivity {


    CircleImageView normal,fire,water;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(Util.getTheme(this));             setContentView(      R.layout.activity_select_theme);

        fire=findViewById(R.id.fire);
        water=findViewById(R.id.water);
        normal=findViewById(R.id.normal);
        TypedValue typedValue=new TypedValue();
        Resources.Theme theme=this.getTheme();
        theme.resolveAttribute(R.attr.new_light,typedValue,true);
        @ColorInt int color =typedValue.data;

       /* if( AppPreference.getPreference(this, AppPersistence.keys.selectedTheme)!=null)
        {
            if(AppPreference.getPreference(this, AppPersistence.keys.selectedTheme).equalsIgnoreCase("0"))
            {
                normal.setBorderColor(color);
                fire.setBorderColor(getResources().getColor(R.color.white));
                water.setBorderColor(getResources().getColor(R.color.white));
            }
            else if(AppPreference.getPreference(this, AppPersistence.keys.selectedTheme).equalsIgnoreCase("1"))
            {
                normal.setBorderColor(getResources().getColor(R.color.white));
                fire.setBorderColor(getResources().getColor(R.color.white));
                water.setBorderColor(color);
            }
            else if(AppPreference.getPreference(this, AppPersistence.keys.selectedTheme).equalsIgnoreCase("2"))
            {
                normal.setBorderColor(getResources().getColor(R.color.white));
                fire.setBorderColor(color);
                water.setBorderColor(getResources().getColor(R.color.white));
            }
        }
        else
        {
            normal.setBorderColor(color);
            fire.setBorderColor(getResources().getColor(R.color.white));
            water.setBorderColor(getResources().getColor(R.color.white));
            AppPreference.setPreference(SelectTheme.this, AppPersistence.keys.selectedTheme, "0");
        }*/


      /*  normal.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                normal.setBorderColor(color);
                fire.setBorderColor(getResources().getColor(R.color.white));
                water.setBorderColor(getResources().getColor(R.color.white));
                AppPreference.setPreference(SelectTheme.this, AppPersistence.keys.selectedTheme, "0");
            }
        });

        water.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                normal.setBorderColor(getResources().getColor(R.color.white));
                fire.setBorderColor(getResources().getColor(R.color.white));
                water.setBorderColor(color);
                AppPreference.setPreference(SelectTheme.this, AppPersistence.keys.selectedTheme, "1");

            }
        });
        fire.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                normal.setBorderColor(getResources().getColor(R.color.white));
                fire.setBorderColor(color);
                water.setBorderColor(getResources().getColor(R.color.white));
                AppPreference.setPreference(SelectTheme.this, AppPersistence.keys.selectedTheme, "2");

            }
        });
*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Util.getKeyTheme(SelectTheme.this);
    }
}