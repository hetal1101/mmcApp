package com.makemusiccount.android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.makemusiccount.android.R;
import com.makemusiccount.android.fragment.TestFragment;
import com.makemusiccount.android.util.Util;

public class TestFrgActivity extends AppCompatActivity {

    Context context;
    FrameLayout frame_container;
    Toolbar toolbar;
    TextView tvHelpHint;
    ImageView ivImage, ivNotification, ivHelp, ivBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            context = this;
        setTheme(Util.getTheme(context));             setContentView(      R.layout.activity_test_frg);

        context = this;
        initComp();
        initToolbar();

        Boolean a = isTablet(context);
        if (a) {
            //Tablet
            // Toast.makeText(context, "1111", Toast.LENGTH_LONG).show();
        } else {
            //Mobile
            // Toast.makeText(context, "2222", Toast.LENGTH_LONG).show();
        }

        Fragment fragment = new TestFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ivNotification = findViewById(R.id.ivNotification);
        tvHelpHint = findViewById(R.id.tvHelpHint);
        ivHelp = findViewById(R.id.ivHelp);
        ivBack = findViewById(R.id.ivBack);

        ivHelp.setVisibility(View.GONE);
        tvHelpHint.setVisibility(View.GONE);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ivNotification.setOnClickListener(view -> {
            if (Util.getUserId(context) == null) {
                Util.loginDialog(this, "You need to be signed in to this action.");
            } else {
                Intent intent = new Intent(context, NotificationActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initComp() {
        frame_container = findViewById(R.id.frame_container);

    }

    public boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }
}