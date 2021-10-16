package com.makemusiccount.android.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.makemusiccount.android.R;
import com.makemusiccount.android.adapter.AvatarAdapter;
import com.makemusiccount.android.model.AvtarList;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SelectAvtar extends AppCompatActivity {

    ProgressDialog progressDialog;
    List<AvtarList> badgesLists = new ArrayList<>();
    String resMessage = "", resCode = "",heading="";
    RecyclerView rvAvatar;
    TextView tvheading;

    AvatarAdapter badgesAdapter;
    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setTheme(Util.getTheme(this));             setContentView(      R.layout.activity_select_avtar);
        progressDialog=new ProgressDialog(this);
        rvAvatar=findViewById(R.id.rvAvatar);
        tvheading=findViewById(R.id.tvheading);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(SelectAvtar.this,5);
        rvAvatar.setLayoutManager(gridLayoutManager);
        badgesAdapter=new AvatarAdapter(SelectAvtar.this,badgesLists);
        rvAvatar.setAdapter(badgesAdapter);
        new GetBatchesList().execute();
    }


    @SuppressLint("StaticFieldLeak")
    private class GetBatchesList extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

                progressDialog.show();
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);

        }

        @Override
        protected String doInBackground(String... params) {
            String strAPI = AppConstant.API_AVATAR_LIST + (Util.getUserId(SelectAvtar.this)!=null?Util.getUserId(SelectAvtar.this):"") ;
            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.e("API", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
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
                        heading = jsonObjectList.getString("heading");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("avatar_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        AvtarList badgesList = new AvtarList();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        badgesList.setAvatarID(jsonObjectList.getString("avatarID"));
                                        badgesList.setAvatarName(jsonObjectList.getString("avatarName"));
                                        badgesList.setImage(jsonObjectList.getString("image"));
                                        badgesLists.add(badgesList);
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
                tvheading.setText(heading);
                badgesAdapter.notifyDataSetChanged();
            }
        }
    }

}
