package com.makemusiccount.android.activity;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.makemusiccount.android.R;
import com.makemusiccount.android.util.Util;

public class SoundInformationActivity extends AppCompatActivity {

    Context context;
    LinearLayout tvGotIt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setTheme(Util.getTheme(this));
          setContentView(R.layout.activity_sound_information);


        tvGotIt = findViewById(R.id.tvGotIt);
        tvGotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
