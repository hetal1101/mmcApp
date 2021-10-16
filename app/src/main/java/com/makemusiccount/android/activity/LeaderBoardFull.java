package com.makemusiccount.android.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makemusiccount.android.R;
import com.makemusiccount.android.adapter.LeaderBoardAdapter;
import com.makemusiccount.android.model.LeaderList;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Global;
import com.makemusiccount.android.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.makemusiccount.android.util.AppConstant.NO_NETWORK_REQUEST_CODE;

public class LeaderBoardFull extends AppCompatActivity {
    boolean IsLoading = true;

    List<LeaderList> leaderLists = new ArrayList<>();
    Global global;
    String type="global";
    ProgressBar progress_bar;
    LeaderBoardAdapter leaderBoardAdapter;
    String number1, image1, name1, point1, number2, image2, name2, point2, number3, image3, name3, point3;
    ImageView ivCup3, ivCup2, ivCup1;
    GridLayoutManager mLayoutManager;

    String UserId = "", resMessage = "", resCode = "";

    CircleImageView ivUser3, ivUser2, ivUser1;
    int page = 0;
    TextView tvName3, tvScore3, tvName2, tvScore2, tvName1, tvScore1, tvDate, tvPage;

    RecyclerView recyclerView;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setTheme(Util.getTheme(this));             setContentView(      R.layout.activity_leader_board_full);
        recyclerView = findViewById(R.id.recyclerView);
        progressDialog = new ProgressDialog(LeaderBoardFull.this);
        progress_bar=findViewById(R.id.progress_bar);
        global = new Global(LeaderBoardFull.this);

        UserId = Util.getUserId(LeaderBoardFull.this);if(UserId==null){UserId="";}
        ivCup3 = findViewById(R.id.ivCup3);
        ivCup2 = findViewById(R.id.ivCup2);
        ivCup1 = findViewById(R.id.ivCup1);
        tvPage = findViewById(R.id.tvPage);
        type=getIntent().getStringExtra("tag");
        if(type!=null)
        {
            if(type.equalsIgnoreCase("1"))
            {
                type="global";
            }
            else
            {
                type="school";
            }
        }
        else
        {
            type="global";
        }
            findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        ivUser3 = findViewById(R.id.ivUser3);
        ivUser2 = findViewById(R.id.ivUser2);
        ivUser1 = findViewById(R.id.ivUser1);

        tvName3 = findViewById(R.id.tvName3);
        tvName2 = findViewById(R.id.tvName2);
        tvName1 = findViewById(R.id.tvName1);
        tvScore3 = findViewById(R.id.tvScore3);
        tvScore2 = findViewById(R.id.tvScore2);
        tvScore1 = findViewById(R.id.tvScore1);
        leaderBoardAdapter =new LeaderBoardAdapter(LeaderBoardFull.this,leaderLists);
        mLayoutManager = new GridLayoutManager(LeaderBoardFull.this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(leaderBoardAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                assert layoutManager != null;
                int totalItemCount = layoutManager.getItemCount();
                int lastVisible = layoutManager.findLastVisibleItemPosition();
                boolean endHasBeenReached = lastVisible + 2 >= totalItemCount;
                if (totalItemCount > 0 && endHasBeenReached) {
                    if (IsLoading) {
                        IsLoading = false;
                        page++;
                        new GetLeader().execute();
                    }
                }
            }
        });

        if (global.isNetworkAvailable()) {
            new GetLeader().execute();
        } else {
            retryInternet("leader");
        }


    }
    public void retryInternet(String extraValue) {
        Intent i = new Intent(LeaderBoardFull.this, NoNetworkActivity.class);
        i.putExtra("extraValue", extraValue);
        startActivityForResult(i, NO_NETWORK_REQUEST_CODE);
    }


    private class GetLeader extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (page == 0) {
                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                    progressDialog.setMessage("Loading...");
                    progressDialog.setCancelable(false);
                }
            } else {
                progress_bar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_LEADER_LIST_DETAIL + UserId
                    + "&app_type=" + "Android"
                    + "&leaderboard_type=" + type
                    + "&pagecode=" + page;

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPI);
                try {
                    restClient.Execute(RequestMethod.GET);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String Register = restClient.getResponse();
              //  Log.e("API", Register);

                if (Register != null && Register.length() != 0) {
                    jsonObjectList = new JSONObject(Register);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        if (resCode.equalsIgnoreCase("0")) {


                                JSONArray jsonArray = jsonObjectList.getJSONArray("leaderboard_data");
                                {
                                    if (jsonArray != null && jsonArray.length() != 0) {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            LeaderList leaderList = new LeaderList();
                                            JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                            leaderList.setSr(String.valueOf(i));
                                            leaderList.setName(jsonObjectList.getString("name"));
                                            leaderList.setPoint(jsonObjectList.getString("point"));
                                            leaderList.setImage(jsonObjectList.getString("image"));
                                            leaderList.setAccount_type(jsonObjectList.getString("account_type"));
                                            leaderList.setSelected(jsonObjectList.getString("selected"));
                                            leaderLists.add(leaderList);

                                    }
                                }
                            }
                        }
                        number1 = jsonObjectList.getString("number1");
                        number2 = jsonObjectList.getString("number2");
                        number3 = jsonObjectList.getString("number3");

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
            progress_bar.setVisibility(View.GONE);
            if (resCode.equalsIgnoreCase("0")) {

                leaderBoardAdapter.notifyDataSetChanged();
                IsLoading = true;
                if (page == 0) {
                    if(leaderLists.size()>0) {
                        Glide.with(LeaderBoardFull.this)
                                .load(number1)
                                .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(ivCup1);

                        Glide.with(LeaderBoardFull.this)
                                .load(leaderLists.get(0).getImage())
                                .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(ivUser1);

                        tvName1.setText(leaderLists.get(0).getName());

                        tvScore1.setText(leaderLists.get(0).getPoint());

                        Glide.with(LeaderBoardFull.this)
                                .load(number2)
                                .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(ivCup2);

                        Glide.with(LeaderBoardFull.this)
                                .load(leaderLists.get(1).getImage())
                                .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(ivUser2);

                        tvName2.setText(leaderLists.get(1).getName());

                        tvScore2.setText(leaderLists.get(1).getPoint());


                        Glide.with(LeaderBoardFull.this)
                                .load(number3)
                                .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(ivCup3);

                        Glide.with(LeaderBoardFull.this)
                                .load(leaderLists.get(2).getImage())
                                .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(ivUser3);

                        tvName3.setText(leaderLists.get(2).getName());

                        tvScore3.setText(leaderLists.get(2).getPoint());
                    }

                }


            } else {
                IsLoading = false;
                if (page == 0) {
                    Toast.makeText(LeaderBoardFull.this, resMessage, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        dismissProgressDialog();
    }
}
