package com.makemusiccount.android.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;

import com.airbnb.lottie.utils.Utils;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.iid.FirebaseInstanceId;

import com.makemusiccount.android.BuildConfig;
import com.makemusiccount.android.R;
import com.makemusiccount.android.adapter.DashboardTabAdapter;
import com.makemusiccount.android.model.CategoryList;
import com.makemusiccount.android.preference.AppPersistence;
import com.makemusiccount.android.preference.AppPreference;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Global;
import com.makemusiccount.android.util.ThemeColors;
import com.makemusiccount.android.util.Util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.telephony.PhoneNumberUtils.compare;
import static com.makemusiccount.android.util.Util.convertDpToPixel;

public class DashboardActivity extends AppCompatActivity {

    Context context;
    public static ViewPager viewPager;
    public static String CatId = "0";
    public static String CatName = "";
    public static String CatProgess = "50";
    public static String CatShortDecsription = "description";
    public static String CatbarColor = "#000000";
    public static List<CategoryList> categoryListsNew=new ArrayList<>();
    public static int postion=0;
    public static String CatId1 = "0";
    public static String CatName1 = "";
    public static String CatProgess1 = "50";
    public static String CatShortDecsription1 = "description";
    public static String CatbarColor1 = "#000000";
    public static TabLayout tabLayout;
    Boolean a;
    SharedPreferences sharedpreferences;
    ProgressDialog progressDialog;
    Global global;

    String userId = "", resMessage = "", resCode = "", DeviceID = "", AppVersion = "", version = "",
            version_msg = "", FCMId = "", record_menu = "", trial_package_msg = "", subscription_img = "",
            subscription_msg = "";

    private AppUpdateManager mAppUpdateManager;
    private static final int RC_APP_UPDATE = 11;
    InstallStateUpdatedListener installStateUpdatedListener;

    private void popupSnackbarForCompleteUpdate() {
        //   toolbar = findViewById(R.id.toolbar);
        Snackbar snackbar =
                Snackbar.make(
                        findViewById(R.id.toolbar),
                        "New app is ready!",
                        Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("Install", view -> {
            if (mAppUpdateManager != null){
                mAppUpdateManager.completeUpdate();
            }
        });


        snackbar.setActionTextColor(getResources().getColor(R.color.white));
        snackbar.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        installStateUpdatedListener = new
                InstallStateUpdatedListener() {
                    @Override
                    public void onStateUpdate(InstallState state) {
                        if (state.installStatus() == InstallStatus.DOWNLOADED){
                            //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                            popupSnackbarForCompleteUpdate();
                        } else if (state.installStatus() == InstallStatus.INSTALLED){
                            if (mAppUpdateManager != null){
                                mAppUpdateManager.unregisterListener(installStateUpdatedListener);
                            }

                        } else {
                            Log.i("TAG", "InstallStateUpdatedListener: state: " + state.installStatus());
                        }
                    }
                };


        mAppUpdateManager = AppUpdateManagerFactory.create(this);

        mAppUpdateManager.registerListener(installStateUpdatedListener);

        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {


                try {
                    mAppUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo, AppUpdateType.IMMEDIATE /*AppUpdateType.FLEXIBLE*/, DashboardActivity.this, RC_APP_UPDATE);

                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }


            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED){
                //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                popupSnackbarForCompleteUpdate();
            } else {
                Log.e("TAG", "checkForAppUpdateAvailability: something else");
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAppUpdateManager != null) {
            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
        }    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_APP_UPDATE) {
            if (resultCode != RESULT_OK) {
                Log.e("TAG", "onActivityResult: app download failed");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            context = this;
        setTheme(Util.getTheme(context));

        new ThemeColors(context);

        setContentView(R.layout.activity_dashboard);


        context = this;
        a = isTablet(context);
        initComp();
        userId = Util.getUserId(context);
        if(userId==null) {userId=""; }
        progressDialog = new ProgressDialog(context);
        AppVersion = BuildConfig.VERSION_NAME;
        FirebaseApp.initializeApp(context);
        FCMId = FirebaseInstanceId.getInstance().getToken();
        global=new Global(this);
        DeviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseCrashlytics.getInstance().setUserId(userId.equalsIgnoreCase("")?"Guest":userId);

     /*   if(Util.getUserId(context)!=null)
        {
            if(Util.getUserId(context).equalsIgnoreCase(""))
            {
                Intent intent = new Intent(context, LoginActivity.class);
                intent.putExtra("page", "sign_in");
                //  intent.putExtra("videoURL", AppConstant.Help_Video);
                startActivity(intent);
            }
        }
        else
        {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra("page", "sign_in");
            //  intent.putExtra("videoURL", AppConstant.Help_Video);
            startActivity(intent);
        }
*/

     /*   Button crashButton = new Button(this);
        crashButton.setText("Crash!");
        crashButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                throw new RuntimeException("Test Crash 1"); // Force a crash
            }
        });

        addContentView(crashButton, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));*/


        if (Util.getisBoolean(context) == null) {
            AppPreference.setPreference(context, AppPersistence.keys.isBoolean, "firsttime");
            Intent intent = new Intent(context, SoundCheckActivity.class);
            intent.putExtra("videoURL", AppConstant.Help_Video);
            startActivity(intent);
        } else {
            if(AppPreference.getPreference(context,AppPersistence.keys.isStartTutorial)!=null)
            {
                AppPreference.setPreference(context,AppPersistence.keys.isEndTutorial,"Yes");
              }
            else {
                AppPreference.setPreference(context, AppPersistence.keys.isStartTutorial, "Yes");
                startActivity(new Intent(DashboardActivity.this,TutorialSelectionActivity.class));
            }

        }


        if (global.isNetworkAvailable()) {

            if(Util.getUserId(context)!=null){
                new Dashboard().execute();
            }else
            {
                setTab(false);
            }

        } else {

        }


    }

    private void initComp() {
        viewPager = findViewById(R.id.tabPager);
    }

    @SuppressLint({"ResourceAsColor", "ResourceType"})
    private void setTab(boolean c) {
        tabLayout = findViewById(R.id.tab_layout);
        if (a) {
            tabLayout.addTab(tabLayout.newTab().setText("     Your Profile     "));
            tabLayout.addTab(tabLayout.newTab().setText("     Subjects     "));
            tabLayout.addTab(tabLayout.newTab().setText("     Recently Played     "));
           // tabLayout.addTab(tabLayout.newTab().setText("     Subscriptions     "));
            tabLayout.addTab(tabLayout.newTab().setText("     Shop     "));

        } else {
            tabLayout.addTab(tabLayout.newTab().setText("    Your Profile    "));
            tabLayout.addTab(tabLayout.newTab().setText("    Subjects    "));
            tabLayout.addTab(tabLayout.newTab().setText("    Recently Played    "));
            //tabLayout.addTab(tabLayout.newTab().setText("    Subscriptions    "));
            tabLayout.addTab(tabLayout.newTab().setText("    Shop    "));
        }
        if(c)
        {
            tabLayout.addTab(tabLayout.newTab().setText("    Record    "));
        }

        //tabLayout.setTabTextColors(Color.parseColor("#AAFFA9"), Color.parseColor("#ffffff"));

        DashboardTabAdapter adapter = new DashboardTabAdapter(DashboardActivity.this,getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setCurrentItem(1);
        viewPager.setOffscreenPageLimit(tabLayout.getTabCount());
        tabLayout.getTabAt(1).select();
     //   tabLayout.setSelectedTabIndicator(1);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                TextView tabTextView = new TextView(this);
                tab.setCustomView(tabTextView);
                tabTextView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                tabTextView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                if (a) {
                    tabTextView.setTextSize(27);
                } else {
                    tabTextView.setTextSize(17);
                }

                Typeface tf = Typeface.createFromAsset(context.getAssets(), "sf_regular.ttf");
                tabTextView.setTypeface(tf);
                tabTextView.setTextColor(getResources().getColor(R.color.white));
                tabTextView.setText(tab.getText());
                // First tab is the selected tab, so if i==0 then set BOLD typeface
                if (i == 1) {
                    tabTextView.setTypeface(null, Typeface.BOLD);
                    TypedValue typedValue=new TypedValue();
                    Resources.Theme theme=context.getTheme();
                    theme.resolveAttribute(R.attr.new_dark,typedValue,true);
                    @ColorInt int color =typedValue.data;
                    tabTextView.setTextColor(color);
                    Typeface tf1 = Typeface.createFromAsset(context.getAssets(), "sf_bold.ttf");
                    tabTextView.setTypeface(tf1);
                }
            }
        }
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                TextView text = (TextView) tab.getCustomView();
                assert text != null;
                TypedValue typedValue=new TypedValue();
                Resources.Theme theme=context.getTheme();
                theme.resolveAttribute(R.attr.new_dark,typedValue,true);
                @ColorInt int color =typedValue.data;
                text.setTextColor(color);
                Typeface tf = Typeface.createFromAsset(context.getAssets(), "sf_bold.ttf");
                text.setTypeface(tf);
                if (a) {
                    text.setTextSize(27);
                } else {
                    text.setTextSize(17);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView text = (TextView) tab.getCustomView();
                assert text != null;
                text.setTextColor(getResources().getColor(R.color.white));
                Typeface tf = Typeface.createFromAsset(context.getAssets(), "sf_regular.ttf");
                text.setTypeface(tf);
                if (a) {
                    text.setTextSize(27);
                } else {
                    text.setTextSize(17);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        if(getIntent().getStringExtra("changeTheme")!=null)
        {
            if(getIntent().getStringExtra("changeTheme").equalsIgnoreCase("yes"))
            {
                viewPager.setCurrentItem(0);
                tabLayout.getTabAt(0).select();
            }
        }
        if(getIntent().getStringExtra("changeTheme1")!=null)
        {
            if(getIntent().getStringExtra("changeTheme1").equalsIgnoreCase("yes"))
            {
                viewPager.setCurrentItem(3);
                tabLayout.getTabAt(3).select();
            }
        }
    }

    public boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
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
            startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(startMain);
        }
    }
    private void dismissProgressDialog() {
        if (progressDialog != null ) {
            if( progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }
        }
    }
    AlertDialog dialog;

    AlertDialog dialog_trial;
    private void openExpiryPopup() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") View alert_layout = inflater.inflate(R.layout.popup_subscribe_package, null);

        alertDialogBuilder.setView(alert_layout);

        dialog = alertDialogBuilder.create();

        dialog.setCancelable(false);

        TextView tvTitle = alert_layout.findViewById(R.id.tvTitle);
        TextView tvMsg = alert_layout.findViewById(R.id.tvMsg);
        TextView btnCancel = alert_layout.findViewById(R.id.btnCancel);
        TextView btnSubscribe = alert_layout.findViewById(R.id.btnSubscribe);
        ImageView ivIcon = alert_layout.findViewById(R.id.ivIcon);

        tvMsg.setText(subscription_msg);
        tvTitle.setText(subscription_msg);

        btnCancel.setOnClickListener(view -> dialog.dismiss());

        btnSubscribe.setOnClickListener(view -> {
            dialog.dismiss();
            startActivity(new Intent(context, SubscribePackageActivity.class));
        });

        Glide.with(context)
                .load(subscription_img)
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(ivIcon);

        dialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        assert window != null;
        lp.copyFrom(window.getAttributes());
        lp.width = convertDpToPixel(380, context);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        window.setAttributes(lp);
    }

    private void showSubscriptionPopup() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") View alert_layout = inflater.inflate(R.layout.popup_trial_subscripsion, null);

        alertDialogBuilder.setView(alert_layout);

        dialog_trial = alertDialogBuilder.create();

        dialog_trial.setCancelable(false);

        TextView tvTitle = alert_layout.findViewById(R.id.tvTitle);
        TextView btnCancel = alert_layout.findViewById(R.id.btnCancel);
        TextView btnSubscribe = alert_layout.findViewById(R.id.btnSubscribe);

        tvTitle.setText(trial_package_msg);

        btnCancel.setOnClickListener(view -> dialog_trial.dismiss());

        btnSubscribe.setOnClickListener(view -> {
            dialog_trial.dismiss();
            startActivity(new Intent(context, SubscribePackageActivity.class));
        });

        dialog_trial.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog_trial.getWindow();
        assert window != null;
        lp.copyFrom(window.getAttributes());
        lp.width = convertDpToPixel(460, context);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        window.setAttributes(lp);
    }

    protected void showUpdateDialog(String msg) {
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("Update Available")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Update", (dialog1, id) -> {
                    dialog1.dismiss();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                      finishAffinity();
                    } else {
                        finish();
                    }
                    final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                })
                .setNegativeButton("Close", (dialog12, id) -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                       finishAffinity();
                    } else {
                       finish();
                    }
                })
                .create();
        dialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    private class Dashboard extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          //  dashboardLists.clear();
            resMessage = "";
            resCode = "";
            version = "";
            version_msg = "";
          //  share_image = "";
            //share_msg = "";
            record_menu = "";
            trial_package_msg = "";
            subscription_img = "";
            subscription_msg = "";
            if (!progressDialog.isShowing()) {
                progressDialog.show();
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_Dashboard + userId
                    + "&fcmID=" + FCMId
                    + "&deviceID=" + DeviceID
                    + "&versionID=" + AppVersion
                    + "&app_type=" + "Android";

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPI);
                try {
                    restClient.Execute(RequestMethod.GET);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String response = restClient.getResponse();
                Log.e("API", response);

                if (response != null && response.length() != 0) {
                    jsonObjectList = new JSONObject(response);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        version = jsonObjectList.getString("version");
                        version_msg = jsonObjectList.getString("version_msg");
                     //   share_image = jsonObjectList.getString("share_image");
                      //  share_msg = jsonObjectList.getString("share_msg");
                        record_menu = jsonObjectList.getString("record_menu");
                        trial_package_msg = jsonObjectList.getString("trial_package_msg");
                        subscription_img = jsonObjectList.getString("subscription_img");
                        subscription_msg = jsonObjectList.getString("subscription_msg");
                        if (resCode.equalsIgnoreCase("0")){} /*{
                            JSONArray jsonArray1 = jsonObjectList.getJSONArray("dasboard_data");
                            {
                                if (jsonArray1 != null && jsonArray1.length() != 0) {
                                    for (int i = 0; i < jsonArray1.length(); i++) {
                                        DashboardList dashboardList = new DashboardList();
                                        JSONObject jsonObjectList = jsonArray1.getJSONObject(i);
                                        dashboardList.setImage(jsonObjectList.getString("image"));
                                        dashboardList.setValue(jsonObjectList.getString("value"));
                                        dashboardList.setTitle(jsonObjectList.getString("title"));
                                        dashboardList.setTitle_sup(jsonObjectList.getString("title_sup"));
                                        dashboardList.setColor_code(jsonObjectList.getString("color_code"));
                                        dashboardLists.add(dashboardList);
                                    }
                                }
                            }
                        }*/
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            dismissProgressDialog();
            if (resCode.equalsIgnoreCase("0")) {
                PackageInfo pInfo;
                boolean new_version_available = false;
                try {
                    pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                    new_version_available = compare(pInfo.versionName, version);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if (new_version_available) {
                    if (context != null) {
                        showUpdateDialog(version_msg);
                    }
                }
                if (!trial_package_msg.isEmpty()) {
                    showSubscriptionPopup();
                }
                if (!subscription_msg.isEmpty()) {
                    openExpiryPopup();
                }
                if(record_menu.equalsIgnoreCase("Yes"))
                {
                    setTab(true);
                }
                else
                {
                    setTab(false);
                }
              //  onHomeAdiCallListener.recordMenuStatus(record_menu);

               /* sharedpreferences = getSharedPreferences("showcase", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();

                String isFirst = sharedpreferences.getString("isFirst", "");
                assert isFirst != null;
                if (!isFirst.equals("")) {
                    ivWelcome.setVisibility(View.GONE);
                } else {
                    ivWelcome.setVisibility(View.VISIBLE);
                    editor.putString("isFirst", "Yes");
                    editor.apply();
                    editor.commit();
                }*/
            } else if (resCode.equalsIgnoreCase("2")) {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
                Util.Logout(context);
                startActivity(new Intent(context, LoginActivity.class));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                  finishAffinity();
                } else {
                  finish();
                }
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        doExitApp();
    }
}