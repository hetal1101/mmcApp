package com.makemusiccount.android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.makemusiccount.android.R;
import com.makemusiccount.android.adapter.RecordAllSongAdapter;
import com.makemusiccount.android.model.AllSong;
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

public class RecordSongListActivity extends AppCompatActivity {

    RecyclerView rvSongList;

    Activity context;

    ProgressDialog progressDialog;

    List<AllSong> allSongs = new ArrayList<>();

    TextView tvClear;

    RecordAllSongAdapter recordAllSongAdapter;

    Global global;

    String UserId = "", resMessage = "", resCode = "";

    int ThisVisibleItemCount = 0;

    int visibleItemCount, totalItemCount, pastVisibleItems;

    boolean IsLoading = true;

    int page = 0;

    LinearLayoutManager mLayoutManager;

    ProgressBar progress_bar;

    EditText searchresult;

    String Search = "";

    ImageView imgNext;

    LinearLayout llNoData;

    TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            context = this;
        setTheme(Util.getTheme(context));             setContentView(      R.layout.activity_record_song_list);

        context = this;

        UserId = Util.getUserId(context); if(UserId==null) {UserId=""; }

        global = new Global(context);

        initToolbar();

        initComp();

        mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rvSongList.setLayoutManager(mLayoutManager);
        rvSongList.setHasFixedSize(true);
        recordAllSongAdapter = new RecordAllSongAdapter(context, allSongs);
        rvSongList.setAdapter(recordAllSongAdapter);

        if (global.isNetworkAvailable()) {
            new GetSongList().execute();
        } else {
            global.retryInternet("song_list");
        }

        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Search = searchresult.getText().toString();
                if (!Search.equalsIgnoreCase("")) {
                    page = 0;
                    tvClear.setVisibility(View.VISIBLE);
                    IsLoading = true;
                    if (global.isNetworkAvailable()) {
                        new GetSongList().execute();
                    } else {
                        global.retryInternet("song_list");
                    }
                } else {
                    Toast.makeText(context, "Please Enter Search Keyword", Toast.LENGTH_SHORT).show();
                }

                //Toast.makeText(context, Search, Toast.LENGTH_SHORT).show();
            }
        });

        rvSongList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                ThisVisibleItemCount = mLayoutManager.findFirstCompletelyVisibleItemPosition();
                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisibleItems = mLayoutManager.findFirstCompletelyVisibleItemPosition();
                if (IsLoading) {
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount &&
                            recyclerView.getChildAt(recyclerView.getChildCount() - 1).getBottom() <= recyclerView.getHeight()) {
                        page++;
                        if (global.isNetworkAvailable()) {
                            new GetSongList().execute();
                        } else {
                            global.retryInternet("song_list");
                        }
                    }
                }
            }
        });

        recordAllSongAdapter.setOnItemClickListener(new RecordAllSongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, int i) {
                Intent intent = new Intent(context, RecordPianoActivity.class);
                intent.putExtra("id", allSongs.get(position).getID());
                intent.putExtra("name", allSongs.get(position).getName());
                intent.putExtra("category", allSongs.get(position).getCategory());
                intent.putExtra("status", allSongs.get(position).getStatus());
                intent.putExtra("file", allSongs.get(position).getSong_file());
                intent.putExtra("image", allSongs.get(position).getImage());
                startActivity(intent);
            }
        });

        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvClear.setVisibility(View.GONE);
                llNoData.setVisibility(View.GONE);
                rvSongList.setVisibility(View.VISIBLE);
                searchresult.setText("");
                Search = "";
                page = 0;
                IsLoading = true;
                if (global.isNetworkAvailable()) {
                    new GetSongList().execute();
                } else {
                    global.retryInternet("song_list");
                }
            }
        });

        Util.hideKeyboard(RecordSongListActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NO_NETWORK_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String extraValue = data.getStringExtra("extraValue");
                if (extraValue.equalsIgnoreCase("song_list")) {
                    new GetSongList().execute();
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetSongList extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (page == 0) {
                allSongs.clear();
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

            String strAPI = AppConstant.API_SONG_ALL + UserId + "&pagecode=" + page + "&search=" + Search;

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String API = restClient.getResponse();
                Log.e("API", API);

                if (API != null && API.length() != 0) {
                    jsonObjectList = new JSONObject(API);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("song_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        AllSong allSong = new AllSong();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        allSong.setID(jsonObjectList.getString("ID"));
                                        allSong.setName(jsonObjectList.getString("name"));
                                        allSong.setSong_file(jsonObjectList.getString("song_file"));
                                        allSong.setCategory(jsonObjectList.getString("category"));
                                        allSong.setStatus(jsonObjectList.getString("recording"));
                                        allSong.setImage(jsonObjectList.getString("image"));
                                        allSongs.add(allSong);
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
            Util.hideKeyboard(RecordSongListActivity.this);
            progress_bar.setVisibility(View.GONE);
            if (resCode.equalsIgnoreCase("0")) {
                llNoData.setVisibility(View.GONE);
                rvSongList.setVisibility(View.VISIBLE);
                recordAllSongAdapter.notifyDataSetChanged();
            } else {
                if (page == 0) {
                    llNoData.setVisibility(View.VISIBLE);
                    rvSongList.setVisibility(View.GONE);
                    tvMessage.setText(resMessage);
                }
                IsLoading = false;
            }
        }
    }

    private void initComp() {
        progress_bar = findViewById(R.id.progress_bar);
        progressDialog = new ProgressDialog(context);
        rvSongList = findViewById(R.id.rvSongList);
        searchresult = findViewById(R.id.searchresult);
        imgNext = findViewById(R.id.imgNext);
        tvClear = findViewById(R.id.tvClear);
        llNoData = findViewById(R.id.llNoData);
        tvMessage = findViewById(R.id.tvMessage);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setContentInsetStartWithNavigation(0);
        ImageView ivHelp = findViewById(R.id.ivHelp);
        ivHelp.setVisibility(View.GONE);
        TextView tvHelpHint = findViewById(R.id.tvHelpHint);
        tvHelpHint.setVisibility(View.GONE);
        ImageView ivDashboard = findViewById(R.id.ivDashboard);
        ivDashboard.setOnClickListener(view -> {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finish();
        });
        ImageView ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(view -> {
            if (Util.getUserId(context) == null) {
                Util.loginDialog(context, "You need to be signed in to this action.");
            } else {
                Intent intent = new Intent(context, NotificationActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
