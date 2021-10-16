package com.makemusiccount.android.activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.makemusiccount.android.R;
import com.makemusiccount.android.preference.AppPersistence;
import com.makemusiccount.android.preference.AppPreference;
import com.makemusiccount.android.util.Util;

public class TutorialS2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setTheme(Util.getTheme(this));             setContentView(      R.layout.activity_tutorial_s2);

        AppPreference.setPreference(TutorialS2.this, AppPersistence.keys.isEndTutorial,"Yes");

        findViewById(R.id.tvmore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(TutorialS2.this,DashboardActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        });

        findViewById(R.id.tvTutorial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppPreference.setPreference(TutorialS2.this,AppPersistence.keys.isEndTutorial,null);
                startActivity(new Intent(TutorialS2.this,TutorialSelectionActivity.class));
                finish();
               /* DashboardActivity.CatId = "0";
                DashboardActivity.CatName = "Tutorial";
                DashboardActivity.CatProgess ="0";
                DashboardActivity.CatShortDecsription = "";
                DashboardActivity.CatbarColor = "#ffffff";
                Intent i = new Intent(TutorialS2.this, SubCategoryActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);*/
            }
        });
    }
}
