package com.makemusiccount.android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.makemusiccount.android.R;
import com.makemusiccount.android.fragment.HomeFragment;
import com.makemusiccount.android.fragment.SongEquationFragment;

import com.makemusiccount.android.fragment.TestFragment;
import com.makemusiccount.android.fragment.TutorialEquationFragment;
import com.makemusiccount.android.listener.onPianoClickListener;
import com.makemusiccount.android.preference.AppPersistence;
import com.makemusiccount.android.preference.AppPreference;
import com.makemusiccount.android.util.ThemeColors;
import com.makemusiccount.android.util.Util;
import com.makemusiccount.pianoview.entity.AutoPlayEntity;
import com.makemusiccount.pianoview.entity.Piano;
import com.makemusiccount.pianoview.listener.OnLoadAudioListener;
import com.makemusiccount.pianoview.listener.OnPianoAutoPlayListener;
import com.makemusiccount.pianoview.listener.OnPianoListener;
import com.makemusiccount.pianoview.view.PianoView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements
        OnPianoListener, OnLoadAudioListener, OnPianoAutoPlayListener {

    private ImageView imageView2, imageView3;

    Toolbar toolbar;

    LinearLayout llLogout, llSongList, llSubject, llProfile, llSettings, llTutorial, llTutorial1, llTutorial2,
            llLeaderboard, llTopProfile, llMyAccount, llNotification, llRecord, llBadges, llProgress, llRate,
            llRefer, llMeetTheFounder, llHelp, llFreeSongList, llAppSettings, llSoundSettings, llSettingList;

    ImageView ivSetting;

    // View vRecord;

    String UserId = "", Name = "", Image = "";

    Activity context;

    TextView tvName, tvLogout, tvSubscribe;

    public static int isHome = 0;

    public static String isSubject = "";

    public static boolean a = false;

    public static String CatId = "";

    public static String SubCatId = "";

    public static String SongId = "";

    public static String SongName = "";

    public static String SongHintImage = "";

    public static String tutorialCategoryId = "";

    public static String tutorialCategoryName = "";

    public static String CurrentEquationSr = "";

    public static String CurrentEquationLabel = "";

    public static String CurrentEquationValue = "";

    public static String CurrentEquationPosition = "0";

    public static String CurrentTutorialEquationPosition = "";

    public static String CurrentTutorialEquationHint = "";

    public static String CurrentEquationType = "";

    public static String CurrentEquationImage = "";

    public static String CurrentEquationChange_char = "";

    public static String CurrentEquationHint = "";

    static onPianoClickListener onPianoClickListener;

    ImageView ivDashboard;

    ImageView ivImage, ivNotification, ivArrow;

    TextView tvType, tvSignIn, tvSignUp;

    public static TextView tvCheck;

    String screenType = "";

    PianoView pv;

    boolean autoPlay = false;

    public static String songType = "";

    ImageView ivBack;

    @SuppressLint("StaticFieldLeak")
    public static RelativeLayout ivWelcome, ivWelcomeSong;

    ProgressDialog progressDialog;

    public static DrawerLayout drawer;

    public static LinearLayout llPianoView;

    FrameLayout frame_container;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static void setPianoClickListener(onPianoClickListener listener) {
        onPianoClickListener = listener;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setTheme(Util.getTheme(this));



        setContentView(R.layout.activity_main);
        //add FirebaseCrashAna
        context = this;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            screenType = bundle.getString("screenType", "");
            SongId = bundle.getString("SongId", "");
            SongName = bundle.getString("SongName", "");
            SongHintImage = bundle.getString("SongHintImage", "");
        }

        UserId = Util.getUserId(context); if(UserId==null) {UserId=""; }
        Name = Util.getUserName(context);
        Image = Util.getUserImage(context);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        initToolbar();

        initComp();

        //initDrawerMenu();

        pv.setPianoListener(this);
        pv.setAutoPlayListener(this);
        pv.setSoundPollMaxStream(100);
        pv.setLoadAudioListener(this);

        HomeFragment.setOnHelpClickListener(type -> {
            if (type.equals("dashboard")) {
                Toast.makeText(context, "Dashboard Click", Toast.LENGTH_SHORT).show();
            }
        });



        HomeFragment.initHomeApiListener(record_status -> {
            Log.e("record_status :-", record_status);
            if (record_status.equals("Yes")) {
                //  vRecord.setVisibility(View.VISIBLE);
                //   llRecord.setVisibility(View.VISIBLE);
            } else {
                //  vRecord.setVisibility(View.GONE);
                //  llRecord.setVisibility(View.GONE);
            }
        });

        ivDashboard.setOnClickListener(view -> {
            if (isHome != 0) {
                llPianoView.setVisibility(View.VISIBLE);
                // toolbar.setBackgroundColor(context.getResources().getColor(R.color.white));
                Fragment fragment = new HomeFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, fragment);
                fragmentTransaction.commit();
                isHome = 0;
            }
        });

        SongEquationFragment.setOnHelpClickListener(type -> {
            if (type.equals("key")) {
                setHelpPianoViewWithPressed("song");
            }
        });

        TutorialEquationFragment.setOnHelpClickListener(type -> {
            if (type.equals("key")) {
                setHelpPianoViewWithPressed("tutorial");
            }
        });

        imageView2.setOnTouchListener((view, motionEvent) -> true);

        imageView3.setOnTouchListener((view, motionEvent) -> true);

        //  openFragment(new SongEquationFragment());

        switch (screenType) {
          /*  case "Leaderboard":
                llPianoView.setVisibility(View.VISIBLE);
                isHome = 1;
                openFragment(new LeaderBoardFragment());
                break;*/
         /*   case "Songs":
                llPianoView.setVisibility(View.VISIBLE);
                isHome = 1;
                openFragment(new SongListFragment());
                break;*/
          /*  case "Tutorial_2":
                llPianoView.setVisibility(View.GONE);
                isHome = 1;
                openFragment(new TutorialCategoryFragment());
                break;*/
         /*   case "Category":
                llPianoView.setVisibility(View.GONE);
                isHome = 1;
                openFragment(new SubjectFragment());
                break;*/
           /*case "Tutorial":
                llPianoView.setVisibility(View.VISIBLE);
                isHome = 1;
                openFragment(new TutorialFragment());
                break;*/
            case "test_question":
                llPianoView.setVisibility(View.GONE);
                isHome = 4;
                openFragment(new TestFragment());
                break;
            case "question":
                llPianoView.setVisibility(View.VISIBLE);
                isHome = 3;
                openFragment(new SongEquationFragment());
                break;
            case "tutorial_question":
                llPianoView.setVisibility(View.VISIBLE);
                isHome = 6;
                openFragment(new TutorialEquationFragment());
                break;
            default:
                llPianoView.setVisibility(View.VISIBLE);
                isHome = 0;
                openFragment(new SongEquationFragment());
                break;
        }

        /*if (Util.getisBoolean(context) == null) {
            AppPreference.setPreference(context, AppPersistence.keys.isBoolean, "firsttime");
            Intent intent = new Intent(context, SoundCheckActivity.class);
            intent.putExtra("videoURL", AppConstant.Help_Video);
            startActivity(intent);
        } else {

        }*/
    }

    private void initComp() {
        Util.getKeyTheme(MainActivity.this);
        progressDialog = new ProgressDialog(context);
        pv = findViewById(R.id.pv);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        ivDashboard = findViewById(R.id.ivDashboard);
        llPianoView = findViewById(R.id.llPianoView);
        frame_container = findViewById(R.id.frame_container);
        ivWelcome = findViewById(R.id.ivWelcome);
        ivWelcomeSong = findViewById(R.id.ivWelcomeSong);
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setContentInsetStartWithNavigation(0);
        ivNotification = findViewById(R.id.ivNotification);
        tvSignIn = findViewById(R.id.tvSignIn);
        tvSignUp = findViewById(R.id.tvSignUp);
        ivBack = findViewById(R.id.ivBack);
        ivBack.setVisibility(View.VISIBLE);
        tvCheck = findViewById(R.id.tvCheck);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ivNotification.setOnClickListener(view -> {
            if (Util.getUserId(context) == null) {
                Util.loginDialog(MainActivity.this, "You need to be signed in to this action.");
            } else {
                Intent intent = new Intent(context, NotificationActivity.class);
                startActivity(intent);
            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LoginActivity.class);
                intent.putExtra("page", "sign_in");
                startActivity(intent);
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LoginActivity.class);
                intent.putExtra("page", "sign_up");
                startActivity(intent);
            }
        });

        tvCheck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (getIsHome() == 2 || getIsHome() == 5) {
                    // newnavigation();
                    //Toast.makeText(context, String.valueOf(isHome), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    public static int getIsHome() {
        return isHome;
    }

    public static ActionBarDrawerToggle toggle;

    /* @SuppressLint({"SetTextI18n", "RtlHardcoded"})
     public void initDrawerMenu() {
         drawer = findViewById(R.id.drawer_layout);
         toggle = new ActionBarDrawerToggle(
                 MainActivity.this,
                 drawer,
                 toolbar,
                 R.string.navigation_drawer_open,
                 R.string.navigation_drawer_close);
         drawer.setDrawerListener(toggle);
         toggle.syncState();
         toggle.setDrawerIndicatorEnabled(false);

         toggle.setToolbarNavigationClickListener(v -> {
             if (drawer.isDrawerVisible(GravityCompat.START)) {
                 drawer.closeDrawer(GravityCompat.START);
             } else {
                 drawer.openDrawer(GravityCompat.START);
             }
         });

         if (Util.getUserId(context) == null) {
             drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
         } else {
             Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.menu, MainActivity.this.getTheme());
             toggle.setHomeAsUpIndicator(drawable);
             drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
         }

         ivBack.setVisibility(View.GONE);

         NavigationView navigationView = findViewById(R.id.nav_view);
         View headerView = navigationView.getHeaderView(0);
         navigationView.setNavigationItemSelectedListener(this);
         //View headerView = getLayoutInflater().inflate(R.layout.nav_header_main, navigationView, false);
         //navigationView.addHeaderView(headerView);

         llLogout = headerView.findViewById(R.id.llLogout);
         llSongList = headerView.findViewById(R.id.llSongList);
         llSubject = headerView.findViewById(R.id.llSubject);
         tvName = headerView.findViewById(R.id.tvName);
         tvType = headerView.findViewById(R.id.tvType);
         llProfile = headerView.findViewById(R.id.llProfile);
         llSettings = headerView.findViewById(R.id.llSettings);
         llTutorial = headerView.findViewById(R.id.llTutorial);
         llTutorial1 = headerView.findViewById(R.id.llTutorial1);
         llTutorial2 = headerView.findViewById(R.id.llTutorial2);
         llLeaderboard = headerView.findViewById(R.id.llLeaderboard);
         llTopProfile = headerView.findViewById(R.id.llTopProfile);
         llMyAccount = headerView.findViewById(R.id.llMyAccount);
         llNotification = headerView.findViewById(R.id.llNotification);
         llRecord = headerView.findViewById(R.id.llRecord);
         vRecord = headerView.findViewById(R.id.vRecord);
         llBadges = headerView.findViewById(R.id.llBadges);
         llProgress = headerView.findViewById(R.id.llProgress);
         llRate = headerView.findViewById(R.id.llRate);
         llRefer = headerView.findViewById(R.id.llRefer);
         llMeetTheFounder = headerView.findViewById(R.id.llMeetTheFounder);
         llHelp = headerView.findViewById(R.id.llHelp);
         tvLogout = headerView.findViewById(R.id.tvLogout);
         ivImage = headerView.findViewById(R.id.ivImage);
         ivArrow = headerView.findViewById(R.id.ivArrow);
         tvSubscribe = headerView.findViewById(R.id.tvSubscribe);
         llFreeSongList = headerView.findViewById(R.id.llFreeSongList);
         llAppSettings = headerView.findViewById(R.id.llAppSettings);
         llSettingList = headerView.findViewById(R.id.llSettingList);
         llSoundSettings = headerView.findViewById(R.id.llSoundSettings);
         ivSetting = headerView.findViewById(R.id.ivSetting);
         LinearLayout llBack = headerView.findViewById(R.id.llBack);
         final LinearLayout llMainList = headerView.findViewById(R.id.llMainList);
         final LinearLayout llProfileList = headerView.findViewById(R.id.llProfileList);

         tvName.setText(Name);
         tvType.setText(Util.getUserType(context));

         if (Util.getUserType(context).equals("User")) {
             tvSubscribe.setVisibility(View.VISIBLE);
         } else {
             tvSubscribe.setVisibility(View.GONE);
         }

         Glide.with(context)
                 .load(Image)
                 .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                 .placeholder(R.drawable.app_logo)
                 .into(ivImage);

         llLogout.setOnClickListener(view -> {
             drawer.closeDrawer(Gravity.LEFT);
             if (tvLogout.getText().toString().equalsIgnoreCase("sign out")) {
                 AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                 alertDialogBuilder.setMessage("Are you sure you want to sign out?");
                 alertDialogBuilder.setPositiveButton("Yes", (arg0, arg1) -> {
                     arg0.dismiss();
                     Util.Logout(context);
                     Intent intent = new Intent(context, LoginActivity.class);
                     intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                     startActivity(intent);
                 });

                 alertDialogBuilder.setNegativeButton("No", (arg0, which) -> arg0.dismiss());
                 AlertDialog alertDialog = alertDialogBuilder.create();
                 alertDialog.show();
             } else {
                 Intent intent = new Intent(context, LoginActivity.class);
                 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                 startActivity(intent);
             }
         });

         llSongList.setOnClickListener(view -> {
             drawer.closeDrawer(Gravity.LEFT);
             if (Util.getUserId(context) == null) {
                 Util.loginDialog(MainActivity.this, "You need to be signed in to this action.");
             } else {
                 isHome = 1;
                 llPianoView.setVisibility(View.VISIBLE);
                 MainActivity.songType = "Premium";
                 openFragment(new SongListFragment());
             }
         });

         llFreeSongList.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 drawer.closeDrawer(Gravity.LEFT);
                 isHome = 1;
                 MainActivity.songType = "Free";
                 llPianoView.setVisibility(View.VISIBLE);
                 isSubject = "";
                 openFragment(new SongListFragment());
             }
         });

         llTutorial2.setOnClickListener(view -> {
             drawer.closeDrawer(Gravity.LEFT);
             if (Util.getUserId(context) == null) {
                 Util.loginDialog(MainActivity.this, "You need to be signed in to this action.");
             } else {
                 llPianoView.setVisibility(View.GONE);
                 isHome = 1;
                 openFragment(new TutorialCategoryFragment());
             }
         });

         llRate.setOnClickListener(view -> {

             try {
                 Intent i = new Intent(Intent.ACTION_VIEW);
                 i.setData(Uri.parse("market://details?id=" + getPackageName()));
                 startActivity(i);
             } catch (Exception e) {
                 e.printStackTrace();
             }
         });

         llSoundSettings.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 drawer.closeDrawer(Gravity.LEFT);
                 startActivity(new Intent(context, SettingsActivity.class));
             }
         });

         llAppSettings.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if (llSettingList.getVisibility() == View.VISIBLE) {
                     ivSetting.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_down));
                     llSettingList.setVisibility(View.GONE);
                 } else {
                     ivSetting.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_up));
                     llSettingList.setVisibility(View.VISIBLE);
                 }
             }
         });

         llRefer.setOnClickListener(view -> {
             drawer.closeDrawer(Gravity.LEFT);
             String ImgListPath = HomeFragment.share_image;
             new DownloadSelectedIMF().execute(ImgListPath);
         });

         llMeetTheFounder.setOnClickListener(view -> {
             drawer.closeDrawer(Gravity.LEFT);
             startActivity(new Intent(context, MeetTheFounderActivity.class));
         });

         tvSubscribe.setOnClickListener(view -> {
             drawer.closeDrawer(Gravity.LEFT);
             if (Util.getUserId(context) == null) {
                 Util.loginDialog(MainActivity.this, "You need to be signed in get subscribe.");
             } else {
                 drawer.closeDrawer(Gravity.LEFT);
                 startActivity(new Intent(context, SubscribePackageActivity.class));
             }
         });

         llHelp.setOnClickListener(view -> {
             if (Util.getUserId(context) == null) {
                 Util.loginDialog(MainActivity.this, "You need to be signed in to this action.");
             } else {
                 drawer.closeDrawer(Gravity.LEFT);
                 startActivity(new Intent(context, HelpActivity.class));
             }
         });

         llProfile.setOnClickListener(view -> {
             *//*slideToLeft(llMainList);
            slideToLeft1(llProfileList);*//*
            drawer.closeDrawer(Gravity.LEFT);
            if (Util.getUserId(context) == null) {
                Util.loginDialog(MainActivity.this, "You need to be signed in to go to my profile.");
            } else {
                drawer.closeDrawer(Gravity.LEFT);
                startActivity(new Intent(context, MyAccountActivity.class));
            }
        });

        llSubject.setOnClickListener(view -> {
            drawer.closeDrawer(Gravity.LEFT);
            if (Util.getUserId(context) == null) {
                Util.loginDialog(MainActivity.this, "You need to be signed in to this action.");
            } else {
                llPianoView.setVisibility(View.GONE);
                isHome = 1;
                openFragment(new SubjectFragment());
            }
        });

        llTutorial.setOnClickListener(view -> {
            *//*if (llTutorial1.getVisibility() == View.GONE) {
                llTutorial1.setVisibility(View.VISIBLE);
                llTutorial2.setVisibility(View.VISIBLE);
                ivArrow.setImageResource(R.drawable.arrow_up);
            } else {
                llTutorial1.setVisibility(View.GONE);
                llTutorial2.setVisibility(View.GONE);
                ivArrow.setImageResource(R.drawable.arrow_down);
            }*//*

            drawer.closeDrawer(Gravity.LEFT);
            if (Util.getUserId(context) == null) {
                Util.loginDialog(MainActivity.this, "You need to be signed in to this action.");
            } else {
                llPianoView.setVisibility(View.GONE);
                isHome = 1;
                openFragment(new TutorialCategoryFragment());
            }

        });

        llTutorial1.setOnClickListener(view -> {
            drawer.closeDrawer(Gravity.LEFT);
            if (Util.getUserId(context) == null) {
                Util.loginDialog(MainActivity.this, "You need to be signed in to this action.");
            } else {
                llPianoView.setVisibility(View.VISIBLE);
                isHome = 1;
                openFragment(new TutorialFragment());
            }
        });

        llLeaderboard.setOnClickListener(view -> {
            drawer.closeDrawer(Gravity.LEFT);
            if (Util.getUserId(context) == null) {
                Util.loginDialog(MainActivity.this, "You need to be signed in to this action.");
            } else {
                llPianoView.setVisibility(View.VISIBLE);
                isHome = 1;
                openFragment(new LeaderBoardFragment());
            }
        });

        llProgress.setOnClickListener(view -> {
            drawer.closeDrawer(Gravity.LEFT);
            if (Util.getUserId(context) == null) {
                Util.loginDialog(context, "You need to be signed in to this action.");
            } else {

                Intent intent = new Intent(context, ProgressNewActivity.class);
                startActivity(intent);
               *//* Intent intent = new Intent(context, ProgressActivity.class);
                startActivity(intent);*//*
            }
        });

        if (Util.getUserId(context) == null) {
            tvLogout.setText("Sign in");
        }

        llSettings.setOnClickListener(view -> {
            if (Util.getUserId(context) == null) {
                Util.loginDialog(MainActivity.this, "You need to be signed in to change your profile.");
            } else {
                drawer.closeDrawer(Gravity.LEFT);
                startActivity(new Intent(context, ChangeProfileActivity.class));
            }
        });

        llRecord.setOnClickListener(view -> {
            if (Util.getUserId(context) == null) {
                Util.loginDialog(MainActivity.this, "You need to be signed in to record song.");
            } else {
                drawer.closeDrawer(Gravity.LEFT);
                startActivity(new Intent(context, RecordSongListActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        llNotification.setOnClickListener(view -> {
            drawer.closeDrawer(Gravity.LEFT);
            if (Util.getUserId(context) == null) {
                Util.loginDialog(MainActivity.this, "You need to be signed in to this action.");
            } else {
                startActivity(new Intent(context, NotificationActivity.class));
            }
        });

        llBadges.setOnClickListener(view -> {
            drawer.closeDrawer(Gravity.LEFT);
            if (Util.getUserId(context) == null) {
                Util.loginDialog(context, "You need to be signed in to this action.");
            } else {
                Intent intent = new Intent(context, BadgesActivity.class);
                startActivity(intent);
            }
        });

        llTopProfile.setOnClickListener(view -> {
            if (Util.getUserId(context) == null) {
                Util.loginDialog(MainActivity.this, "You need to be signed in to view your profile.");
            } else {
                drawer.closeDrawer(Gravity.LEFT);
                startActivity(new Intent(context, MyAccountActivity.class));
            }
        });

        llMyAccount.setOnClickListener(view -> {

        });

        llBack.setOnClickListener(view -> {
            slideToRight(llProfileList);
            slideToRight1(llMainList);
        });


    }
*/
    @SuppressLint("StaticFieldLeak")
    class DownloadSelectedIMF extends AsyncTask<String, String, Void> {
        String ImgPath = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("loading");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected Void doInBackground(String... arg0) {
            String filename = "MMC.jpg";
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory().toString() + "/MMC");
            wallpaperDirectory.mkdirs();
            ImgPath = wallpaperDirectory.getPath() + "/" + filename;
            String mys_received_image = arg0[0];
            int count;
            try {
                URL my_url = new URL(mys_received_image);
                URLConnection connection = my_url.openConnection();

                int lengthOfFile = connection.getContentLength();
                connection.connect();
                InputStream input = new BufferedInputStream(my_url.openStream());
                OutputStream output = new FileOutputStream(ImgPath);
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            try {
                File file = new File(ImgPath);
                Uri imageUri = Uri.fromFile(file);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "MMC");
                intent.putExtra(Intent.EXTRA_TEXT, HomeFragment.share_msg);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(intent, "Share via"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        view.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.right_in));
    }

    /*public void slideToLeft(final View view) {
        TranslateAnimation animate = new TranslateAnimation(0, -view.getWidth(), 0, 0);
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

    public void slideToLeft1(final View view) {
        view.setVisibility(View.VISIBLE);
        view.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.left_in));
    }*/

    private void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
/*

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
*/

    @Override
    protected void onResume() {
        super.onResume();
      Util.getKeyTheme(MainActivity.this);
        pv.setPianoVolume(Float.parseFloat(Util.getPianoSound(context)));
       /* tvName.setText(Util.getUserName(context));
        Glide.with(context)
                .load(Util.getUserImage(context))
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.app_logo)
                .into(ivImage);
        tvType.setText(Util.getUserType(context));*/

    }

    private void setHelpPianoViewWithPressed(String type) {
        if (type.equals("song")) {
            List<AutoPlayEntity> autoPlayEntities = new ArrayList<>();
            autoPlayEntities.add(Util.getAutoPlayObject(Integer.parseInt(CurrentEquationPosition), 300));
            autoPlayEntities.add(Util.getAutoPlayObject(Integer.parseInt(CurrentEquationPosition), 300));
            autoPlayEntities.add(Util.getAutoPlayObject(Integer.parseInt(CurrentEquationPosition), 300));
            autoPlayEntities.add(Util.getAutoPlayObject(Integer.parseInt(CurrentEquationPosition), 300));
            pv.autoPlay(autoPlayEntities, "hint");
        } else {
            List<AutoPlayEntity> autoPlayEntities = new ArrayList<>();
            autoPlayEntities.add(Util.getAutoPlayObject(Integer.parseInt(CurrentTutorialEquationPosition), 300));
            autoPlayEntities.add(Util.getAutoPlayObject(Integer.parseInt(CurrentTutorialEquationPosition), 300));
            autoPlayEntities.add(Util.getAutoPlayObject(Integer.parseInt(CurrentTutorialEquationPosition), 300));
            autoPlayEntities.add(Util.getAutoPlayObject(Integer.parseInt(CurrentTutorialEquationPosition), 300));
            pv.autoPlay(autoPlayEntities, "hint");
        }
    }

    private long exitTime = 0;

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, R.string.press_again_exit_app, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            } else {
                finish();
            }
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        /*if (isHome == 0) {
            doExitApp();
        } else if (isHome == 1) {
            Fragment fragment = new HomeFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.commit();
            isHome = 0;
        } else if (isHome == 2) {
            Fragment fragment = new SubjectFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.commit();
            initDrawerMenu();
            isHome = 1;
        } else if (isHome == 5) {
            Fragment fragment = new SubCategoryFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.commit();
            newnavigation();
            isHome = 2;

        } else if (isHome == 3) {
            Fragment fragment = new SongListFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.commit();
            newnavigation();
            isHome = 1;

        } else if (isHome == 4) {
            Fragment fragment = new SongListFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.commit();
            isHome = 3;
        } else if (isHome == 6) {
            Fragment fragment = new TutorialCategoryFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.commit();
            isHome = 1;
        }*/
        /*if (isHome == 0) {
            doExitApp();
        } else if (isHome == 1) {
            llPianoView.setVisibility(View.VISIBLE);
            //toolbar.setBackgroundColor(context.getResources().getColor(R.color.white));
            Fragment fragment = new HomeFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.commit();
            initDrawerMenu();
            isSubject = "";
            isHome = 0;
        } else if (isHome == 2) {
            llPianoView.setVisibility(View.GONE);
            //toolbar.setBackgroundColor(context.getResources().getColor(R.color.toolbar_bg));
            Fragment fragment = new SubjectFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.commit();
            initDrawerMenu();
            isSubject = "";
            isHome = 1;
        } else if (isHome == 5) {
            llPianoView.setVisibility(View.GONE);
            Fragment fragment = new SubCategoryFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.commit();
            newnavigation();
            isHome = 2;

        } else if (isHome == 3) {
            llPianoView.setVisibility(View.VISIBLE);
            Fragment fragment = new SongListFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.commit();
            if (isSubject.equalsIgnoreCase("Yes")) {
                newnavigation();
                isHome = 2;
            } else {
                isHome = 1;
            }

        } else if (isHome == 4) {
            llPianoView.setVisibility(View.VISIBLE);
            Fragment fragment = new SongListFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.commit();
            isHome = 3;
        } else if (isHome == 6) {
            llPianoView.setVisibility(View.GONE);
            Fragment fragment = new TutorialCategoryFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.commit();
            isHome = 1;
        }*/
    }

    private void newnavigation() {
        toolbar.setNavigationIcon(null);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        ivBack.setVisibility(View.VISIBLE);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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