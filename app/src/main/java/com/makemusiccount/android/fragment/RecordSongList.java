package com.makemusiccount.android.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.makemusiccount.android.R;
import com.makemusiccount.android.activity.RecordPianoActivity;
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


public class RecordSongList extends Fragment {
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

    public RecordSongList() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_record_song_list, container, false);
        //
        context = getActivity();

        UserId = Util.getUserId(context); if(UserId==null) {UserId=""; }

        global = new Global(context);



        initComp(view);

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

        Util.hideKeyboard(context);




        //
        return view;
    }
    private void initComp(View view) {
        progress_bar = view.findViewById(R.id.progress_bar);
        progressDialog = new ProgressDialog(context);
        rvSongList = view.findViewById(R.id.rvSongList);
        searchresult = view.findViewById(R.id.searchresult);
        imgNext = view.findViewById(R.id.imgNext);
        tvClear =view.findViewById(R.id.tvClear);
        llNoData = view.findViewById(R.id.llNoData);
        tvMessage = view.findViewById(R.id.tvMessage);
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
            Util.hideKeyboard(context);
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
    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}