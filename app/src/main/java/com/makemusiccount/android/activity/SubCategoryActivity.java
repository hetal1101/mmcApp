package com.makemusiccount.android.activity;

import android.content.Context;
import android.content.res.Configuration;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.makemusiccount.android.R;

import com.makemusiccount.android.fragment.SubCategoryFragment;
import com.makemusiccount.android.util.Util;

public class SubCategoryActivity extends AppCompatActivity {

    Context context;
    FrameLayout frame_container;
    public static String SongId = "";
    public static String SongName = "";
    public static String SongHintImage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            context = this;
        setTheme(Util.getTheme(context));             setContentView(      R.layout.activity_sub_category);

        context = this;
        initComp();

        Boolean a = isTablet(context);
        if (a) {
            //Tablet
           // Toast.makeText(context, "1111", Toast.LENGTH_LONG).show();
        } else {
            //Mobile
           // Toast.makeText(context, "2222", Toast.LENGTH_LONG).show();
        }

        Fragment fragment = new SubCategoryFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
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