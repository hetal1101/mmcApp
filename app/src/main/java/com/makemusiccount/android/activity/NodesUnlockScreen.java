package com.makemusiccount.android.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.makemusiccount.android.R;
import com.makemusiccount.android.adapter.NodesAdapter;
import com.makemusiccount.android.model.Nodes;
import com.makemusiccount.android.preference.AppPersistence;
import com.makemusiccount.android.preference.AppPreference;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Util;
import com.makemusiccount.pianoview.entity.Piano;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NodesUnlockScreen extends AppCompatActivity {


    RecyclerView rvnodes;
    NodesAdapter nodesAdapter;
    List<Nodes> nodesList=new ArrayList<>();
    ProgressDialog progressDialog;
    TextView line1,line2;

    LinearLayout tvSignUp;
    String userId = "", resCode = "", resMessage = "",
            title1 = "", title2 = "",
            badge_title = "", badge_msg = "", badge_img = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setTheme(Util.getTheme(this));             setContentView(      R.layout.activity_nodes_unlock_screen);

        rvnodes=findViewById(R.id.rvnodes);
        line1=findViewById(R.id.line1);
        line2=findViewById(R.id.line2);
        tvSignUp=findViewById(R.id.tvSignUp);
        progressDialog=new ProgressDialog(this);
        rvnodes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        nodesAdapter=new NodesAdapter(NodesUnlockScreen.this,nodesList);
        rvnodes.setAdapter(nodesAdapter);
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Util.getKeyTheme(NodesUnlockScreen.this);
                Intent intent = new Intent(NodesUnlockScreen.this, PianoActivity.class);
                intent.putExtra("screen", "equation");
                intent.putExtra("song_name", getIntent().getStringExtra("song_name"));
                intent.putExtra("song_id", getIntent().getStringExtra("song_id"));
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            }
        });

        new GetNodes().execute();




    }

    @SuppressLint("StaticFieldLeak")
    private class GetNodes extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            resCode = "";
            resMessage = "";

            nodesList.clear();

            if (!progressDialog.isShowing()) {
                progressDialog.show();
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
            }

        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_SONG_NODES + ""
                    + "&songsID=" + getIntent().getStringExtra("song_id");

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.Execute(RequestMethod.POST);
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
                        if (resCode.equalsIgnoreCase("0")) {
                            title1 = jsonObjectList.getString("title1");
                            title2 = jsonObjectList.getString("title2");
                            JSONArray jsonArray = jsonObjectList.getJSONArray("song_node");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        Nodes nodes=new Nodes();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        nodes.setValue(jsonObjectList.getString("value"));
                                        nodesList.add(nodes);
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
                line1.setText(title1);
                line2.setText(title2);
                nodesAdapter.notifyDataSetChanged();


            } else {


                Toast.makeText(NodesUnlockScreen.this, resMessage, Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
