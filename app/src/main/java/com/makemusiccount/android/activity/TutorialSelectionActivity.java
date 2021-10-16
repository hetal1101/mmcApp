package com.makemusiccount.android.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.makemusiccount.android.R;
import com.makemusiccount.android.adapter.TutorialSelectAdapter;
import com.makemusiccount.android.model.TutorialSelectList;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Global;
import com.makemusiccount.android.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.makemusiccount.android.util.AppConstant.NO_NETWORK_REQUEST_CODE;
import static com.makemusiccount.android.util.AppConstant.TutorialLists;

public class TutorialSelectionActivity extends AppCompatActivity {

    Context context;
    RecyclerView recyclerView;
    LinearLayout tvContinue;
    TextView tvSubscription;
    List<TutorialSelectList> tutorialLists = new ArrayList<>();
    TutorialSelectAdapter tutorialSelectAdapter;
    Global global;
    ProgressDialog progressDialog;
    int tutorial_count=3;
    String UserId = "", resMessage = "", resCode = "";
    public static String ID = "", Name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            context = this;
        setTheme(Util.getTheme(context));             setContentView(      R.layout.activity_tutorial_selection);

        context = this;

        initComp();
        global=new Global(context);
        progressDialog=new ProgressDialog(context);
        RecyclerView.LayoutManager mLayoutManagerBestProduct = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManagerBestProduct);
        recyclerView.setHasFixedSize(true);
        tutorialSelectAdapter = new TutorialSelectAdapter(context, tutorialLists);
        recyclerView.setAdapter(tutorialSelectAdapter);

        if (global.isNetworkAvailable()) {
            new GetTutorialList().execute();
        } else {
            retryInternet("tutorial_list");
        }


        tutorialSelectAdapter.setOnItemClickListener((position, view, i) -> {

           /* Intent intent = new Intent(context, VideoTutorialActivity.class);
            intent.putExtra("videoURL", tutorialLists.get(position).getUrl());
            startActivity(intent);*/

        });

        tvContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ID.equalsIgnoreCase("")) {
                    Toast.makeText(context, "Select", Toast.LENGTH_SHORT).show();
                } else {

                    Intent i1 = new Intent(context, DashboardActivity.class);
                    i1.putExtra("tutorialCategoryId", ID);
                    i1.putExtra("tutorialCategoryName", Name);
                    startActivity(i1);
                    Intent i = new Intent(context, TutorialEquationActivity.class);
                    i.putExtra("tutorialCategoryId", TutorialLists.get(0).getID());
                    i.putExtra("tutorialCategoryName", TutorialLists.get(0).getName());
                    startActivity(i);
                    finish();
                }

            }
        });

        tvSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    private void initComp() {
        recyclerView = findViewById(R.id.recyclerView);
        tvSubscription = findViewById(R.id.tvSubscription);
        tvContinue = findViewById(R.id.tvContinue);
    }

    public void retryInternet(String extraValue) {
        Intent i = new Intent(context, NoNetworkActivity.class);
        i.putExtra("extraValue", extraValue);
        startActivityForResult(i, NO_NETWORK_REQUEST_CODE);
    }

    @SuppressLint("StaticFieldLeak")
    private class GetTutorialList extends AsyncTask<String, Void, String> {
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

            String strAPI = AppConstant.API_TUTORIALS_SELECT + UserId
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
                        tutorial_count = Integer.parseInt(jsonObjectList.getString("tutorial_count").equalsIgnoreCase("")?"0":jsonObjectList.getString("tutorial_count"));
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("tutorial_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < (tutorial_count==0?jsonArray.length():tutorial_count); i++) {
                                        TutorialSelectList tutorialList = new TutorialSelectList();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        tutorialList.setID(jsonObjectList.getString("ID"));
                                        tutorialList.setName(jsonObjectList.getString("name"));
                                        tutorialList.setUrl(jsonObjectList.getString("video"));
                                        tutorialList.setImage(jsonObjectList.getString("image"));
                                        tutorialList.setStatus(jsonObjectList.getString("status"));
                                        tutorialLists.add(tutorialList);
                                    }
                                }
                            }
                            TutorialLists=new ArrayList<>();
                            JSONArray jsonArray1 = jsonObjectList.getJSONArray("selected_play_tutorial_list");
                            {
                                if (jsonArray1 != null && jsonArray1.length() != 0) {
                                    for (int i = 0; i < (tutorial_count==0?jsonArray1.length():tutorial_count); i++) {
                                        TutorialSelectList tutorialList = new TutorialSelectList();
                                        JSONObject jsonObjectList = jsonArray1.getJSONObject(i);
                                        tutorialList.setID(jsonObjectList.getString("ID"));
                                        tutorialList.setName(jsonObjectList.getString("name"));
                                        tutorialList.setUrl(jsonObjectList.getString("video"));
                                        tutorialList.setImage(jsonObjectList.getString("image"));
                                        tutorialList.setStatus(jsonObjectList.getString("status"));
                                        tutorialList.setLine1(jsonObjectList.getString("line1"));
                                        tutorialList.setLine2(jsonObjectList.getString("line2"));
                                        tutorialList.setLine3(jsonObjectList.getString("line3"));
                                        TutorialLists.add(tutorialList);
                                    }
                                }
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

                TutorialSelectionActivity.ID = tutorialLists.get(1).getID();
                TutorialSelectionActivity.Name = tutorialLists.get(1).getName();
                AppConstant.songName=tutorialLists.get(1).getName();
                AppConstant.songId=tutorialLists.get(1).getID();
                tutorialSelectAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
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