package com.makemusiccount.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.FirebaseApp;
import com.makemusiccount.android.R;
import com.makemusiccount.android.preference.AppPersistence;
import com.makemusiccount.android.preference.AppPreference;
import com.makemusiccount.android.util.Util;



public class SplashActivity extends AppCompatActivity {

    private int SPLASH_TIME_OUT = 2000;

    Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            context = this;
        setTheme(Util.getTheme(context));             setContentView(      R.layout.activity_splash);

        context = this;

        FirebaseApp.initializeApp(this);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Util.getUserId(context) == null) {
                    AppPreference.setPreference(context, AppPersistence.keys.USER_NAME, "Guest");
                    startActivity(new Intent(context, DashboardActivity.class));//MainActivity
                    finish();
                } else {
                    startActivity(new Intent(context, DashboardActivity.class));//MainActivity
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }
}
