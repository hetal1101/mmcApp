package com.makemusiccount.android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.makemusiccount.android.R;
import com.makemusiccount.android.adapter.SubscribeAdapter;
import com.makemusiccount.android.fragment.Subscrption_new;
import com.makemusiccount.android.model.PackageList;
import com.makemusiccount.android.preference.AppPersistence;
import com.makemusiccount.android.preference.AppPreference;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Global;
import com.makemusiccount.android.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.makemusiccount.android.util.AppConstant.NO_NETWORK_REQUEST_CODE;

public class SubscribePackageActivity extends AppCompatActivity {

    RecyclerView rvPackage;

    Activity context;

    List<PackageList> packageLists = new ArrayList<>();

    SubscribeAdapter subscribeAdapter;
    TextView freeTextview,paidTextview;

    LinearLayout llbg1,llbg2,llel1,llel2;

    ProgressDialog progressDialog;

    String resMessage = "",screen="", resCode = "", sub_title, sub_point_1, sub_point_2, sub_point_3, notes, main_title;

    TextView tvHeading, tvSubHeading, tvPoint1, tvPoint2, tvPoint3, tvNote, tvContinue;

    LinearLayout tvPromoCode;

    Global global;

    TextView tvFailMsg, tvMsg, tvMsg1,main_header,main_subheader;

    RelativeLayout rvMain, rvSuccess;
    Switch aSwitch;


    String PromoCode = "";
    LinearLayout paidLL,freeLL;

    String message = "", message1 = "";

    public boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            context = this;


        setTheme(Util.getTheme(context));             setContentView(      R.layout.activity_subscribe_package);

        context=this;
        global = new Global(context);

        initComp();


        if(isTablet(context))
        {
            llbg1=findViewById(R.id.llbackgrond1);
            llbg2=findViewById(R.id.llbackground2);
            llel1=findViewById(R.id.llelevation1);
            llel2=findViewById(R.id.llelevation2);

            llel1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    llbg1.setBackground(context.getResources().getDrawable(R.drawable.btn_border_new2));
                    llbg2.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    llel1.setElevation(25f);
                    llel2.setElevation(0f);
                }
            });

            llel2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    llbg2.setBackground(context.getResources().getDrawable(R.drawable.btn_border_new2));
                    llbg1.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                    llel2.setElevation(25f);
                    llel1.setElevation(0f);
                }
            });

            llbg1.setBackground(context.getResources().getDrawable(R.drawable.btn_border_new2));
            llbg2.setBackgroundColor(context.getResources().getColor(R.color.transparent));
            llel1.setElevation(25f);
            llel2.setElevation(0f);

        }

        if (Util.getUserId(context) == null) {
            startActivity(new Intent(context, LoginActivity.class));
            //finish();
        } else {
            if (Util.getUserId(context).isEmpty()) {
                startActivity(new Intent(context, LoginActivity.class));
                // finish();
            }
        }
        LinearLayoutManager mLayoutManager;
        if(isTablet(context)) {
            mLayoutManager  = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        }
        else {
            mLayoutManager  = new LinearLayoutManager(context);
        }

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(aSwitch.isChecked())
                {
                    main_header.setText("Eliminate Math Phobia");
                    main_subheader.setText("one song at a time.");
                }
                else
                {
                    main_header.setText("Thrive in math ");
                    main_subheader.setText("With the freshest song on the piano.");
                }
            }
        });


        rvPackage.setLayoutManager(mLayoutManager);
        rvPackage.setHasFixedSize(true);
        subscribeAdapter = new SubscribeAdapter(context, packageLists);
        rvPackage.setAdapter(subscribeAdapter);



        if (global.isNetworkAvailable()) {
            new GetPackage().execute();
        } else {
            global.retryInternet("package");
        }
        freeTextview=findViewById(R.id.freeTextview);
        paidTextview=findViewById(R.id.paidTextview);

        paidLL=findViewById(R.id.paidLL);
        paidLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(aSwitch.isChecked())
                {
                    Intent intent = new Intent(context, PaymentActivity.class);
                    intent.putExtra("package",packageLists.get(1));
                    startActivity(intent);
                }

            }
        });
        freeLL=findViewById(R.id.freeLL);
        freeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(aSwitch.isChecked())
                {
                    if(AppPreference.getPreference(context, AppPersistence.keys.isStartTutorial)==null)

                    {
                        Intent intent = new Intent(context, TutorialSelectionActivity.class);
                        startActivity(intent);}
                    else {

                        Intent intent = new Intent(context, DashboardActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });


        tvContinue.setOnClickListener(view1 -> {

            if(!packageLists.get(subscribeAdapter.getSelectedPosition()).getPackID().equalsIgnoreCase("0")) {
                Intent intent = new Intent(context, PaymentActivity.class);
                intent.putExtra("package", packageLists.get(subscribeAdapter.getSelectedPosition()));
                startActivity(intent);
            }
            else
            {

                if(AppPreference.getPreference(context,AppPersistence.keys.isStartTutorial)==null)

                {
                    Intent intent = new Intent(context, TutorialSelectionActivity.class);
                    startActivity(intent);}
                else {
                    Intent intent = new Intent(context, DashboardActivity.class);
                    startActivity(intent);
                }


            }
        });

        tvPromoCode.setOnClickListener(view1 -> {
            openPromoCodePopup();
        });

        tvSubHeading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        tvNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, WebViewActivity.class);
                startActivity(i);
            }
        });


    }


    @SuppressLint("SetTextI18n")
    private void openPromoCodePopup() {
        try {

            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.popup_promocode);

            EditText etSignUpPromoCode = dialog.findViewById(R.id.etSignUpPromoCode);
            ImageView ivClose = dialog.findViewById(R.id.ivClose);
            ImageView ivCloseSuccess = dialog.findViewById(R.id.ivCloseSuccess);
            TextView btnOk = dialog.findViewById(R.id.btnOk);
            TextView btnSuccess = dialog.findViewById(R.id.btnSuccess);

            rvMain = dialog.findViewById(R.id.rvMain);
            rvSuccess = dialog.findViewById(R.id.rvSuccess);
            tvFailMsg = dialog.findViewById(R.id.tvFailMsg);
            tvMsg = dialog.findViewById(R.id.tvMsg);
            tvMsg1 = dialog.findViewById(R.id.tvMsg1);

            rvMain.setVisibility(View.VISIBLE);
            rvSuccess.setVisibility(View.GONE);
            tvFailMsg.setVisibility(View.GONE);

            ivClose.setOnClickListener(v -> {
                Util.hideKeyboard(context);
                dialog.dismiss();
            });

            ivCloseSuccess.setOnClickListener(v -> {
                Util.hideKeyboard(context);
                // finish();
                Intent i = new Intent(context, MainActivity.class);
                startActivity(i);
                dialog.dismiss();
            });

            btnSuccess.setOnClickListener(v -> {
                Util.hideKeyboard(context);
                // finish();
                Intent i = new Intent(context, MainActivity.class);
                startActivity(i);
                dialog.dismiss();
            });

            btnOk.setOnClickListener(v -> {
                Util.hideKeyboard(context);
                PromoCode = etSignUpPromoCode.getText().toString().trim();
                if (PromoCode.equalsIgnoreCase("")) {
                    Toast.makeText(context, "Enter PromoCode", Toast.LENGTH_SHORT).show();
                } else {
                    if (global.isNetworkAvailable()) {
                        new CheckPromoCode().execute();
                    } else {
                        global.retryInternet("promocode");
                    }
                }
            });

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;
            dialog.getWindow().setAttributes(lp);
            dialog.show();

            /*AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
            AlertDialog alertDialog;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            @SuppressLint("InflateParams") final View alertLayout = inflater.inflate(R.layout.popup_promocode, null);
            EditText etSignUpPromoCode = alertLayout.findViewById(R.id.etSignUpPromoCode);
            ImageView ivClose = alertLayout.findViewById(R.id.ivClose);
            TextView btnOk = alertLayout.findViewById(R.id.btnOk);
            alertDialogBuilder.setView(alertLayout);
            alertDialog = alertDialogBuilder.create();
            final AlertDialog finalAlertDialog = alertDialog;

            ivClose.setOnClickListener(v -> {
                finalAlertDialog.dismiss();
            });

            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalAlertDialog.dismiss();
                }
            });

            alertDialog.show();
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(Objects.requireNonNull(alertDialog.getWindow()).getAttributes());
            *//*Display display = this.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);*//*
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;
            alertDialog.getWindow().setAttributes(lp);*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NO_NETWORK_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String extraValue = data.getStringExtra("extraValue");
                if (extraValue.equalsIgnoreCase("package")) {
                    new GetPackage().execute();
                } else if (extraValue.equalsIgnoreCase("promocode")) {
                    new CheckPromoCode().execute();
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class CheckPromoCode extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!progressDialog.isShowing()) {
                progressDialog.show();
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_CHECK_PROMOCODE + Util.getUserId(context)
                    + "&userpromocode=" + PromoCode + "&app_type=" + "Android";

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPI);
                try {
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String Register = restClient.getResponse();
                Log.e("API", Register);

                if (Register != null && Register.length() != 0) {
                    jsonObjectList = new JSONObject(Register);
                    if (jsonObjectList.length() != 0) {
                        resCode = jsonObjectList.getString("msgcode");
                        if (resCode.equalsIgnoreCase("0")) {
                            message = jsonObjectList.getString("message");
                            message1 = jsonObjectList.getString("message1");
                        } else {
                            message = jsonObjectList.getString("message");
                        }
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
                rvMain.setVisibility(View.GONE);
                rvSuccess.setVisibility(View.VISIBLE);
                tvMsg.setText(message);
                tvMsg1.setText(message1);
            } else {
                tvFailMsg.setText(message);
                tvFailMsg.setVisibility(View.VISIBLE);
                rvMain.setVisibility(View.VISIBLE);
                rvSuccess.setVisibility(View.GONE);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetPackage extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!progressDialog.isShowing()) {
                progressDialog.show();
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_PACKAGE_LIST + Util.getUserId(context)
                    + "&app_type=" + "Android";

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPI);
                try {
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String Register = restClient.getResponse();
                Log.e("API", Register);

                if (Register != null && Register.length() != 0) {
                    jsonObjectList = new JSONObject(Register);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        screen  = jsonObjectList.getString("screen");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("package_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        PackageList packageList = new PackageList();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        packageList.setPackID(jsonObjectList.getString("packID"));
                                        packageList.setName(jsonObjectList.getString("name"));
                                        packageList.setPlan_price_info(jsonObjectList.getString("plan_price_info"));
                                        packageList.setPackage_desc(jsonObjectList.getString("package_desc"));

                                        packageList.setPrice(jsonObjectList.getString("price"));
                                        packageLists.add(packageList);
                                    }
                                }
                            }

                            JSONArray jsonArray1 = jsonObjectList.getJSONArray("package_info");
                            {
                                JSONObject jsonObjectList = jsonArray1.getJSONObject(0);
                                main_title = jsonObjectList.getString("main_title");
                                sub_title = jsonObjectList.getString("sub_title");
                                sub_point_1 = jsonObjectList.getString("sub_point_1");
                                sub_point_2 = jsonObjectList.getString("sub_point_2");
                                sub_point_3 = jsonObjectList.getString("sub_point_3");
                                notes = jsonObjectList.getString("notes");
                            }
                        }
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
                subscribeAdapter.notifyDataSetChanged();
                tvHeading.setText(main_title);
                tvSubHeading.setText(sub_title);
                tvPoint1.setText(sub_point_1);
                tvPoint2.setText(sub_point_2);
                tvPoint3.setText(sub_point_3);
                freeTextview.setText(packageLists.get(0).getPlan_price_info());
                paidTextview.setText(packageLists.get(1).getPlan_price_info());
                //tvNote.setText(notes);
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initComp() {
        rvPackage = findViewById(R.id.rvPackage);

        tvContinue = findViewById(R.id.tvContinue);
        tvPromoCode = findViewById(R.id.tvPromoCode);
        tvHeading = findViewById(R.id.tvsubhead);
        tvSubHeading = findViewById(R.id.tvhead);
        tvPoint1 = findViewById(R.id.tvPoint1);
        tvPoint2 = findViewById(R.id.tvPoint2);
        tvPoint3 = findViewById(R.id.tvPoint3);
        main_subheader = findViewById(R.id.main_subheader);
        main_header = findViewById(R.id.main_header);
        aSwitch = findViewById(R.id.switch1);
        tvNote = findViewById(R.id.tvNote);
        progressDialog = new ProgressDialog(context);


    }

    @Override
    public void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    private long exitTime = 0;

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "Press Again For Open Dashboard", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Intent i = new Intent(context, DashboardActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                //finish();
            } else {
                Intent i = new Intent(context, DashboardActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                //finish();
            }
           /* Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(startMain);*/
        }
    }


}