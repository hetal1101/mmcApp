package com.makemusiccount.android.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.makemusiccount.android.R;
import com.makemusiccount.android.activity.PaymentActivity;
import com.makemusiccount.android.adapter.SubscribeAdapter;
import com.makemusiccount.android.model.PackageList;
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

public class SubscriptionFragment extends Fragment {

    View view;
    Activity context;
    Global global;

    RecyclerView rvPackage;

    List<PackageList> packageLists = new ArrayList<>();

    SubscribeAdapter subscribeAdapter;

    ProgressDialog progressDialog;

    String resMessage = "", resCode = "", sub_title, sub_point_1, sub_point_2, sub_point_3, notes, main_title;

    TextView tvHeading, tvSubHeading, tvPoint1, tvPoint2, tvPoint3, tvNote, tvContinue,tvsubhead,tvhead;

    LinearLayout tvPromoCode;

    TextView tvFailMsg, tvMsg, tvMsg1,tvName;

    RelativeLayout rvMain, rvSuccess;

    String PromoCode = "";

    String message = "", message1 = "";

    LottieAnimationView lottieAnimationView;

    LinearLayout llData;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_subscriptions, container, false);

        context = getActivity();

        global = new Global(context);

        initComp();

        llData.setVisibility(View.GONE);
        lottieAnimationView.setVisibility(View.VISIBLE);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        rvPackage.setLayoutManager(mLayoutManager);
        rvPackage.setHasFixedSize(true);
        subscribeAdapter = new SubscribeAdapter(context, packageLists);
        rvPackage.setAdapter(subscribeAdapter);

        if (global.isNetworkAvailable()) {
            new GetPackage().execute();
        } else {
            global.retryInternet("package");
        }


        tvContinue.setOnClickListener(view -> {
            Intent intent = new Intent(context, PaymentActivity.class);
            intent.putExtra("package", packageLists.get(subscribeAdapter.getSelectedPosition()));
            startActivity(intent);
        });

        tvPromoCode.setOnClickListener(view -> {
            openPromoCodePopup();
        });


        return view;

    }

    @SuppressLint("SetTextI18n")
    private void openPromoCodePopup() {
        try {

            final Dialog dialog = new Dialog(context);
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
                dialog.dismiss();
            });

            btnSuccess.setOnClickListener(v -> {
                Util.hideKeyboard(context);
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
            *//*Display display = SubscribePackageActivity.this.getWindowManager().getDefaultDisplay();
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
            lottieAnimationView.setVisibility(View.VISIBLE);
            llData.setVisibility(View.GONE);
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
            lottieAnimationView.setVisibility(View.GONE);
            llData.setVisibility(View.VISIBLE);
            if (resCode.equalsIgnoreCase("0")) {
               // packageLists.add(0,new PackageList("0","Free","","",""));
                subscribeAdapter.notifyDataSetChanged();
                tvsubhead.setText(main_title);
                tvHeading.setText(main_title);
                tvSubHeading.setText(sub_title);
                tvhead.setText(sub_title);
                tvPoint1.setText(sub_point_1);
                tvPoint2.setText(sub_point_2);
                tvPoint3.setText(sub_point_3);
                tvNote.setText(notes);
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initComp() {
        rvPackage = view.findViewById(R.id.rvPackage);
        tvContinue = view.findViewById(R.id.tvContinue);
        tvPromoCode = view.findViewById(R.id.tvPromoCode);
        tvHeading = view.findViewById(R.id.tvHeading);
        tvSubHeading = view.findViewById(R.id.tvSubHeading);
        tvsubhead = view.findViewById(R.id.tvsubhead);
        tvhead = view.findViewById(R.id.tvhead);
        tvPoint1 = view.findViewById(R.id.tvPoint1);
        tvPoint2 = view.findViewById(R.id.tvPoint2);
        tvPoint3 = view.findViewById(R.id.tvPoint3);
        tvNote = view.findViewById(R.id.tvNote);
        lottieAnimationView = view.findViewById(R.id.lottieAnimationView);
        llData = view.findViewById(R.id.llData);
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
}
