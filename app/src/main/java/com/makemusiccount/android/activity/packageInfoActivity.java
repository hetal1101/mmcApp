package com.makemusiccount.android.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.makemusiccount.android.R;
import com.makemusiccount.android.util.Util;

public class packageInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setTheme(Util.getTheme(this));             setContentView(      R.layout.activity_package_info_);
    }
}
