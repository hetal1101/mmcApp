package com.makemusiccount.android.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.StrictMode;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.makemusiccount.android.R;
import com.makemusiccount.android.fragment.TutorialEquationFragment;
import com.makemusiccount.android.listener.onPianoClickListener;
import com.makemusiccount.android.util.Util;
import com.makemusiccount.pianoview.entity.AutoPlayEntity;
import com.makemusiccount.pianoview.entity.Piano;
import com.makemusiccount.pianoview.listener.OnLoadAudioListener;
import com.makemusiccount.pianoview.listener.OnPianoAutoPlayListener;
import com.makemusiccount.pianoview.listener.OnPianoListener;
import com.makemusiccount.pianoview.view.PianoView;

import java.util.ArrayList;
import java.util.List;

public class TutorialEquationActivity extends AppCompatActivity implements OnPianoListener, OnLoadAudioListener, OnPianoAutoPlayListener {

    private ImageView imageView2, imageView3;
    Toolbar toolbar;
    Activity context;
    PianoView pv;
    boolean autoPlay = false;
    FrameLayout frame_container;
    ImageView ivBack, ivNotification;
    String url="";
    ProgressDialog progressDialog;

    public static String tutorialCategoryId = "";
    public static String tutorialCategoryName = "";
    public static String vLine1 = "";
    public static String vLine2 = "";
    public static String vLine3 = "";
    public static String CurrentTutorialEquationPosition = "";
    public static String CurrentTutorialEquationHint = "";
    static onPianoClickListener onPianoClickListener;
    TextView tvName,tvvideobutton;
    TextView tvLine1,tvLine2,tvLine3;
    public static String hintOff="";



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static void setPianoClickListener(onPianoClickListener listener) {
        onPianoClickListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            context = this;
        setTheme(Util.getTheme(context));             setContentView(      R.layout.activity_tutorial_equation);
        context = this;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            tutorialCategoryId = bundle.getString("tutorialCategoryId", "");
            tutorialCategoryName = bundle.getString("tutorialCategoryName", "");
            url = bundle.getString("url", "");
            hintOff=bundle.getString("hintOff","");
        }




        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        initToolbar();
        initComp();
        tvLine1.setText(vLine1);
        tvLine2.setText(vLine2);
        tvLine3.setText(vLine3);
        if (url.equalsIgnoreCase(""))
        {
            tvvideobutton.setVisibility(View.GONE);
        }
        else {
            tvvideobutton.setVisibility(View.VISIBLE);
        }
        tvvideobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TutorialEquationActivity.this,VideoTutorialActivity.class).putExtra("videoURL",url));
            }
        });

     //   tvName.setText(tutorialCategoryName);

        pv = findViewById(R.id.pv);
        pv.setPianoVolume(Float.parseFloat(Util.getPianoSound(context)));
        pv.setPianoListener(this);
        pv.setAutoPlayListener(this);
        pv.setSoundPollMaxStream(100);
        pv.setLoadAudioListener(this);
        openFragment(new TutorialEquationFragment());
        pv.setPianoListener(this);
        pv.setAutoPlayListener(this);
        pv.setSoundPollMaxStream(100);
        pv.setLoadAudioListener(this);

        TutorialEquationFragment.setOnHelpClickListener(type -> {
            if (type.equals("key")) {
                setHelpPianoViewWithPressed("tutorial");
            }
        });



        imageView2.setOnTouchListener((view, motionEvent) -> true);

        imageView3.setOnTouchListener((view, motionEvent) -> true);

    }


    private void initComp() {
        progressDialog = new ProgressDialog(context);
        pv = findViewById(R.id.pv);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        tvLine1 = findViewById(R.id.tvLine1);
        tvLine2 = findViewById(R.id.tvLine2);
        tvLine3 = findViewById(R.id.tvLine3);
        frame_container = findViewById(R.id.frame_container);
     //   tvName = findViewById(R.id.tvName);
        tvvideobutton = findViewById(R.id.tvvideobutton);
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setContentInsetStartWithNavigation(0);
        ivNotification = findViewById(R.id.ivNotification);
        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ivNotification.setOnClickListener(view -> {
            if (Util.getUserId(context) == null) {
                Util.loginDialog(TutorialEquationActivity.this, "You need to be signed in to this action.");
            } else {
                Intent intent = new Intent(context, NotificationActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void slideToRight(final View view) {
        TranslateAnimation animate = new TranslateAnimation(0, view.getWidth(), 0, 0);
        animate.setDuration(500);
        animate.setFillAfter(false);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.startAnimation(animate);
    }

    public void slideToRight1(final View view) {
        view.setVisibility(View.VISIBLE);
        view.startAnimation(AnimationUtils.loadAnimation(TutorialEquationActivity.this, R.anim.right_in));
    }

    private void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    @Override
    protected void onResume() {
        super.onResume();



    }

    private void setHelpPianoViewWithPressed(String type) {

        List<AutoPlayEntity> autoPlayEntities = new ArrayList<>();
        autoPlayEntities.add(Util.getAutoPlayObject(Integer.parseInt(CurrentTutorialEquationPosition), 300));
        autoPlayEntities.add(Util.getAutoPlayObject(Integer.parseInt(CurrentTutorialEquationPosition), 300));
        autoPlayEntities.add(Util.getAutoPlayObject(Integer.parseInt(CurrentTutorialEquationPosition), 300));
        autoPlayEntities.add(Util.getAutoPlayObject(Integer.parseInt(CurrentTutorialEquationPosition), 300));
        pv.autoPlay(autoPlayEntities, "hint");

    }

    @Override
    public void loadPianoAudioStart() {
        //Toast.makeText(context, "Audio loading start...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadPianoAudioFinish() {
        //Toast.makeText(context, "Audio loading end...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadPianoAudioError(Exception e) {
        Toast.makeText(context, "Audio loading error...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadPianoAudioProgress(int progress) {

    }

    @Override
    public void onPianoAutoPlayStart() {
        autoPlay = true;
    }

    @Override
    public void onPianoAutoPlayEnd() {
        autoPlay = false;
    }

    @Override
    public void onPianoInitFinish() {
    }

    @Override
    public void onPianoClick(Piano.PianoKeyType type, Piano.PianoVoice voice, int group, int positionOfGroup) {
        int position = Util.getPianoPosition(type, group, positionOfGroup);
        if (onPianoClickListener != null) {
            if (!autoPlay) {
                onPianoClickListener.onClick(position);
            }
        }
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        if (pv != null) {
            pv.releaseAutoPlay();


        }
        super.onDestroy();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}