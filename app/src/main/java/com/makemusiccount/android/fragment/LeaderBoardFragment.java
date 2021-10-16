package com.makemusiccount.android.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makemusiccount.android.R;
import com.makemusiccount.android.activity.LeaderBoardFull;
import com.makemusiccount.android.activity.LevelActivity;
import com.makemusiccount.android.activity.NoNetworkActivity;
import com.makemusiccount.android.activity.SelectAvtar;

import com.makemusiccount.android.activity.SelectionActivity;
import com.makemusiccount.android.activity.SettingsActivity;
import com.makemusiccount.android.adapter.AvatarAdapter;
import com.makemusiccount.android.adapter.AvatarAdapter1;
import com.makemusiccount.android.adapter.BadgesAdapter;
import com.makemusiccount.android.adapter.KeyboardAdapter1;
import com.makemusiccount.android.adapter.LeaderBoardAdapter;
import com.makemusiccount.android.adapter.LeaderBoardAdapter1;
import com.makemusiccount.android.adapter.ThemeAdapter1;
import com.makemusiccount.android.model.AvtarList;
import com.makemusiccount.android.model.BadgesList;
import com.makemusiccount.android.model.LeaderList;
import com.makemusiccount.android.model.LeaderList1;
import com.makemusiccount.android.model.ThemeList;
import com.makemusiccount.android.preference.AppPersistence;
import com.makemusiccount.android.preference.AppPreference;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.ui.MyBoldTextView;
import com.makemusiccount.android.util.AndroidMultiPartEntity;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Global;
import com.makemusiccount.android.util.Util;
import com.makemusiccount.pianoview.view.PianoView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.makemusiccount.android.activity.DashboardActivity.tabLayout;
import static com.makemusiccount.android.activity.DashboardActivity.viewPager;
import static com.makemusiccount.android.activity.LevelActivity.progress_wave_count;
import static com.makemusiccount.android.activity.LevelActivity.total_wave_count;
import static com.makemusiccount.android.util.AppConstant.NO_NETWORK_REQUEST_CODE;
import static com.makemusiccount.android.util.Util.convertDpToPixel;

/**
 * A simple {@link Fragment} subclass.
 */
public class LeaderBoardFragment extends Fragment {

    View view, view1;

    RelativeLayout rlPiano;
    TextView tvback1;

    RecyclerView recyclerView;

    RecyclerView recyclerView1;
    LinearLayout leader_school;
    TextView tvUpdate;
    String userID = "",Name="",Email="", name = "", email = "", phone = "", image = "", currentPass = "", newPass = "", reTypePass = "";

    List<LeaderList> leaderLists = new ArrayList<>();
    ProgressDialog loading;
    List<LeaderList1> leaderLists1 = new ArrayList<>();

    LeaderBoardAdapter leaderBoardAdapter;

    LeaderBoardAdapter1 leaderBoardAdapter1;

    Activity context;

    Global global;

    ProgressDialog progressDialog;

    ProgressBar progress_bar;
    RecyclerView rvBadges;
    List<BadgesList> badgesLists = new ArrayList<>();
    BadgesAdapter badgesAdapter;

    PianoView pv;

    String UserId = "", resMessage = "", resCode = "";

    ImageView ivCup3, ivCup2, ivCup1;

    CircleImageView ivUser3, ivUser2, ivUser1;

    TextView tvName3, tvScore3, tvName2, tvScore2, tvName1, tvScore1, tvDate, tvPage,tvSetting,tvEditProfile;

    MyBoldTextView tvName;
    TextView seefull1,seefull;



    ImageView ivCup33, ivCup22, ivCup11 ;
    public static ImageView ivAvatar;

    CircleImageView ivUser33, ivUser22, ivUser11;

    TextView tvName33, tvScore33, tvName22, tvScore22, tvName11, tvScore11,tvSelectAvatar;

    GridLayoutManager mLayoutManager;
    TextView bheading,gheading,sheading;

    int visibleItemCount, totalItemCount, pastVisibleItems;

    boolean IsLoading = true;
    EditText etName,etEmail;

    int page = 0;

    String number1, image1, name1, point1, number2, image2, name2, point2, number3, image3, name3, point3;
    String number11, image11, name11, point11, number22, image22, name22, point22, number33, image33, name33, point33;

    String upLoadServerUri = "https://www.makemusiccount.online/mmc/index.php?view=change_info";
    @SuppressLint("StaticFieldLeak")
    private class UploadFileToServer extends AsyncTask<Void, Integer, String>
    {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(upLoadServerUri);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        num -> {
                        });


                entity.addPart("userphoto", new StringBody(""));
                entity.addPart("username", new StringBody(etName.getText().toString()));
                entity.addPart("useremail", new StringBody(etEmail.getText().toString()));
                entity.addPart("userphone", new StringBody(""));
                entity.addPart("userpass", new StringBody(""));
                entity.addPart("userID", new StringBody(Util.getUserId(context)));
                httppost.setEntity(entity);

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: " + statusCode;
                }
                Log.e("response", responseString);
            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String temp) {

            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(temp);
                String resMessage = jsonObject.getString("message");
                String resCode = jsonObject.getString("msgcode");
                if (resCode.equalsIgnoreCase("0")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("detail");
                    JSONObject jsonObjectList = jsonArray.getJSONObject(0);
                    if (jsonObjectList != null && jsonObjectList.length() != 0) {
                        userID = jsonObjectList.getString("userID");
                        name = jsonObjectList.getString("name");
                        email = jsonObjectList.getString("email");
                        phone = jsonObjectList.getString("phone");

                    }
                    AppPreference.setPreference(context, AppPersistence.keys.USER_NAME, name);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_EMAIL, email);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_NUMBER, phone);
                    Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
                    tvName.setText(name);
                    view.findViewById(R.id.llEdit).setVisibility(View.GONE);
                    view.findViewById(R.id.llLeaderboard).setVisibility(View.VISIBLE);
                    tvEditProfile.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                Log.e("AA", e.getMessage());
            }
        }
    }

    public LeaderBoardFragment() {
        // Required empty public constructor
    }
    AlertDialog dialog;
    @SuppressLint("SetTextI18n")
    private void openPopup(int position) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") View alert_layout = inflater.inflate(R.layout.popup_badges, null);

        alertDialogBuilder.setView(alert_layout);

        dialog = alertDialogBuilder.create();

        TextView tvTitle = alert_layout.findViewById(R.id.tvTitle);
        TextView tvMsg = alert_layout.findViewById(R.id.tvMsg);
        TextView btnOk = alert_layout.findViewById(R.id.btnOk);
        TextView tvTerms = alert_layout.findViewById(R.id.tvTerms);
        ImageView ivIcon = alert_layout.findViewById(R.id.ivIcon);

        tvMsg.setText(badgesLists.get(position).getMessage());
        tvTitle.setText(badgesLists.get(position).getName());
        tvTerms.setText(badgesLists.get(position).getTerms());

        if (badgesLists.get(position).getMessage().isEmpty()) {
            tvMsg.setVisibility(View.GONE);
        }

        btnOk.setOnClickListener(v -> dialog.dismiss());
        Glide.with(context)
                .load(badgesLists.get(position).getImage())
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



    RecyclerView rvAvatar;
    AvatarAdapter1 avatarAdapter;
    TextView tvheading;
    List<ThemeList> avtarLists = new ArrayList<>();

    RecyclerView rvTheme;
    ThemeAdapter1 themeAdapter1;
    TextView tvheading1;
    List<ThemeList> themeLists = new ArrayList<>();

    RecyclerView rvKeyboard;
    KeyboardAdapter1 keyboardAdapter1;

    List<ThemeList> keyboardLists = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().setTheme(Util.getTheme(getActivity()));
        view = inflater.inflate(R.layout.fragment_leader_board, container, false);

        context = getActivity();

        rvAvatar=view.findViewById(R.id.rvAvatar);
        rvTheme=view.findViewById(R.id.rvTheme);
        rvKeyboard=view.findViewById(R.id.rvKeyboard);
        avtarLists.clear();

        view.findViewById(R.id.a2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(3);
                tabLayout.getTabAt(3).select();
            }
        });
        view.findViewById(R.id.viewProgress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(context, LevelActivity.class));
            }
        });
        //Util.setUserTheme(context,"https://cdn.shopify.com/s/files/1/0100/0836/3108/products/Solid-125-BrightPink_360x.jpg?v=1545434833");
       /*  avtarLists.add(new AvtarList("1","Xyz","https://www.w3schools.com/howto/img_avatar.png"));
        avtarLists.add(new AvtarList("2","Xyz","https://www.w3schools.com/w3images/avatar6.png"));
        avtarLists.add(new AvtarList("0","Xyz",""));

        avtarLists1.clear();*/
       /* avtarLists1.add(new AvtarList("1","Xyz","https://cdn.shopify.com/s/files/1/0119/0402/products/Oxford_Blue_a6769006-43f4-4f6b-a61a-aa9a1614c673_540x.jpg?v=1571262476"));
        avtarLists1.add(new AvtarList("2","Xyz","https://www.makemusiccount.online/uploads/themeColor/black.png"));
        avtarLists1.add(new AvtarList("3","Xyz","https://www.makemusiccount.online/uploads/themeColor/blue.png"));
   //     avtarLists1.add(new AvtarList("4","Xyz","https://www.treehugger.com/thmb/55jUaIf2YCQGU5_tdwi9S7VHsSE=/653x436/filters:no_upscale():max_bytes(150000):strip_icc()/__opt__aboutcom__coeus__resources__content_migration__mnn__images__2018__06__BlockOfCyan-f3880e94bcbc4f21b1cfc48d7902675b.jpg"));
        avtarLists1.add(new AvtarList("0","Xyz",""));

        avtarLists2.clear();*/
    /* //   Util.setUSER_KEYBOARD(context,"https://www.makemusiccount.online/uploads/keyboard/2.jpg");

        avtarLists2.add(new AvtarList("1","Xyz","https://www.makemusiccount.online/uploads/keyboard/2.jpg"));
        avtarLists2.add(new AvtarList("2","Xyz","https://www.makemusiccount.online/uploads/themeKey/pink.jpg"));
        avtarLists2.add(new AvtarList("3","Xyz","https://www.makemusiccount.online/uploads/themeKey/blue.jpg"));
        avtarLists2.add(new AvtarList("0","Xyz",""));
*/
        tvheading=view.findViewById(R.id.tvheading);
        rvAvatar.setLayoutManager(new LinearLayoutManager(context,RecyclerView.HORIZONTAL,false));
        avatarAdapter=new AvatarAdapter1(getActivity(),avtarLists,0);
        rvAvatar.setAdapter(avatarAdapter);

        rvTheme.setLayoutManager(new LinearLayoutManager(context,RecyclerView.HORIZONTAL,false));
        themeAdapter1=new ThemeAdapter1(getActivity(),themeLists,0);
        rvTheme.setAdapter(themeAdapter1);

        rvKeyboard.setLayoutManager(new LinearLayoutManager(context,RecyclerView.HORIZONTAL,false));
        keyboardAdapter1=new KeyboardAdapter1(getActivity(),keyboardLists,0);
        rvKeyboard.setAdapter(keyboardAdapter1);

        global = new Global(context);
        seefull1 = view.findViewById(R.id.seefull1);
        tvSetting = view.findViewById(R.id.tvSetting);
        bheading = view.findViewById(R.id.badgeheading);
        gheading = view.findViewById(R.id.globalheading);
        sheading = view.findViewById(R.id.schoolheading);
        seefull = view.findViewById(R.id.seefull);
        rvBadges = view.findViewById(R.id.rvBadges);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);
        rvBadges.setLayoutManager(mLayoutManager);
        rvBadges.setHasFixedSize(true);
        badgesAdapter = new BadgesAdapter(context, badgesLists);
        rvBadges.setAdapter(badgesAdapter);

        badgesAdapter.setOnItemClickListener((position, view, i) -> openPopup(position));


        initComp(view);
        tvback1=view.findViewById(R.id.tvback1);
        tvback1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvEditProfile.setVisibility(View.VISIBLE);
                view1.findViewById(R.id.llEdit).setVisibility(View.GONE);
                view1.findViewById(R.id.llLeaderboard).setVisibility(View.VISIBLE);
            }
        });
        tvSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });
        view.findViewById(R.id.tvLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage("Are you sure you want to sign out?");
                alertDialogBuilder.setPositiveButton("Yes", (arg0, arg1) -> {
                    arg0.dismiss();
                    Util.Logout(context);
                    Intent intent = new Intent(context, SelectionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });

                alertDialogBuilder.setNegativeButton("No", (arg0, which) -> arg0.dismiss());
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();



               /* Util.Logout(context);
                Intent intent = new Intent(context, SelectionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);*/
            }
        });
        tvUpdate=view.findViewById(R.id.tvUpdate);
        tvUpdate.setOnClickListener(view -> {
            Name = etName.getText().toString();
            Email = etEmail.getText().toString();
            Email = Email.replaceAll(" ", "");
            if (Name.equals("")) {
                Toast.makeText(context, "Please enter name", Toast.LENGTH_SHORT).show();
            } else if (Email.equals("")) {
                Toast.makeText(context, "Please enter email / username", Toast.LENGTH_SHORT).show();
            }/* else if (MobileNo.equals("")) {
                Toast.makeText(context, "Please Enter Mobile No!!!", Toast.LENGTH_SHORT).show();
            } */ else {
              //  loading = ProgressDialog.show(context, "", "Please wait...", false, false);
                new Thread(() -> {
                    Util.hideKeyboard(context);
                    UploadFileToServer task = new UploadFileToServer();
                    task.execute();
                }).start();
            }
        });
        etName.setText(Util.getUserName(context)+"");
        etEmail.setText(Util.getEmail(context)+"");
        tvEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvEditProfile.setVisibility(View.GONE);
                view1.findViewById(R.id.llEdit).setVisibility(View.VISIBLE);
                view1.findViewById(R.id.llLeaderboard).setVisibility(View.GONE);
            }
        });





//        view1.setVisibility(View.GONE);
     //   rlPiano.setVisibility(View.GONE);
   //     pv.setVisibility(View.GONE);

        UserId = Util.getUserId(context); if(UserId==null) {UserId=""; }
        tvName.setText(Util.getUserName(getContext()));

       

        Glide.with(context)
                .load(  Util.getUserImage(getContext()))
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.app_logo)
                .into(ivAvatar);


        mLayoutManager = new GridLayoutManager(context, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        leaderBoardAdapter = new LeaderBoardAdapter(context, leaderLists);
        recyclerView.setAdapter(leaderBoardAdapter);
        view.findViewById(R.id.seefull).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, LeaderBoardFull.class).putExtra("tag","1"));
            }

        });
        view.findViewById(R.id.seefull1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, LeaderBoardFull.class).putExtra("tag","2"));
            }

        });
        tvSelectAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, SelectAvtar.class));
            }
        });



        progressDialog = new ProgressDialog(context);
        mLayoutManager = new GridLayoutManager(context, 1);
        recyclerView1.setLayoutManager(mLayoutManager);
        recyclerView1.setHasFixedSize(true);
        leaderBoardAdapter1 = new LeaderBoardAdapter1(context, leaderLists1);
        recyclerView1.setAdapter(leaderBoardAdapter1);

        Util.setDate(tvDate);

        tvPage.setText("Leaderboard");

        /*recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onScrolled(@NonNull RecyclerView rvBookingList, int dx, int dy) {
                super.onScrolled(rvBookingList, dx, dy);

                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisibleItems = mLayoutManager.findFirstCompletelyVisibleItemPosition();

                if (IsLoading) {
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount && rvBookingList.getChildAt(rvBookingList.getChildCount() - 1).getBottom() <= rvBookingList.getHeight()) {
                        page++;
                        new GetLeader().execute();
                    }
                }
            }
        });*/

        if (global.isNetworkAvailable()) {
            new GetLeader().execute();
            new GetUserDATA().execute();
        } else {
            retryInternet("leader");
        }

        return view;
    }

    public void retryInternet(String extraValue) {
        Intent i = new Intent(context, NoNetworkActivity.class);
        i.putExtra("extraValue", extraValue);
        startActivityForResult(i, NO_NETWORK_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NO_NETWORK_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String extraValue = data.getStringExtra("extraValue");
                if (extraValue.equalsIgnoreCase("leader")) {
                    new GetLeader().execute();
                }
            }
        }
    }

    String levelTitel="";
    @SuppressLint("StaticFieldLeak")
    private class GetUserDATA extends AsyncTask<String, Void, String> {
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

            String strAPI = AppConstant.API_PROFILE_DATA + Util.getUserId(context)
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
                        progress_wave_count = Integer.parseInt(jsonObjectList.getString("user_level"));
                        total_wave_count = Integer.parseInt(jsonObjectList.getString("user_level_total"));
                        LevelActivity.user_level_title =jsonObjectList.getString("user_level_title");
                        levelTitel=jsonObjectList.getString("user_level_title");
                        LevelActivity.usercoin=jsonObjectList.getString("user_coin");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArrayTheme=jsonObjectList.getJSONArray("app_theme_list");
                            if(jsonArrayTheme!=null&&jsonArrayTheme.length()>0)
                            {
                                themeLists.clear();
                                for(int i=0;i<jsonArrayTheme.length();i++)
                                {
                                    ThemeList themeList=new ThemeList();
                                    themeList.setCoin(jsonArrayTheme.getJSONObject(i).getString("coin"));
                                    themeList.setImage(jsonArrayTheme.getJSONObject(i).getString("image"));
                                    themeList.setName(jsonArrayTheme.getJSONObject(i).getString("name"));
                                    themeList.setUsershopID(jsonArrayTheme.getJSONObject(i).getString("usershopID"));
                                    themeList.setUserPurchase(jsonArrayTheme.getJSONObject(i).getString("user_purchase"));
                                    themeLists.add(themeList);
                                }
                            }

                            JSONArray jsonArrayAvatar=jsonObjectList.getJSONArray("avatar_list");
                            if(jsonArrayAvatar!=null&&jsonArrayAvatar.length()>0)
                            {
                                avtarLists.clear();
                                for(int i=0;i<jsonArrayAvatar.length();i++)
                                {
                                    ThemeList themeList=new ThemeList();
                                    themeList.setCoin(jsonArrayAvatar.getJSONObject(i).getString("coin"));
                                    themeList.setImage(jsonArrayAvatar.getJSONObject(i).getString("image"));
                                    themeList.setName(jsonArrayAvatar.getJSONObject(i).getString("name"));
                                    themeList.setUsershopID(jsonArrayAvatar.getJSONObject(i).getString("usershopID"));
                                    themeList.setUserPurchase(jsonArrayAvatar.getJSONObject(i).getString("user_purchase"));
                                    avtarLists.add(themeList);
                                }
                            }

                            JSONArray jsonArrayKeyboard=jsonObjectList.getJSONArray("app_key_theme_list");
                            if(jsonArrayKeyboard!=null&&jsonArrayKeyboard.length()>0)
                            {
                                keyboardLists.clear();
                                for(int i=0;i<jsonArrayKeyboard.length();i++)
                                {
                                    ThemeList themeList=new ThemeList();
                                    themeList.setCoin(jsonArrayKeyboard.getJSONObject(i).getString("coin"));
                                    themeList.setImage(jsonArrayKeyboard.getJSONObject(i).getString("image"));
                                    themeList.setName(jsonArrayKeyboard.getJSONObject(i).getString("name"));
                                    themeList.setUsershopID(jsonArrayKeyboard.getJSONObject(i).getString("usershopID"));
                                    themeList.setUserPurchase(jsonArrayKeyboard.getJSONObject(i).getString("user_purchase"));
                                    keyboardLists.add(themeList);
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
                avtarLists.add(new ThemeList("0","9990","0","0","","0","0",0));
                keyboardLists.add(new ThemeList("0","9990","0","0","","0","0",0));
                themeLists.add(new ThemeList("0","9990","0","0","","0","0",0));
                tvLevel.setText(levelTitel);
                int progress=progress_wave_count*100/total_wave_count;
                activeProgress.setProgress(progress);
                themeAdapter1.notifyDataSetChanged();
                avatarAdapter.notifyDataSetChanged();
                keyboardAdapter1.notifyDataSetChanged();
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
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

            String strAPI = AppConstant.API_LEADER_LIST + UserId
                    + "&app_type=" + "Android"
                    + "&pagecode=" + page;

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

                            if(!jsonObjectList.getString("leaderboard_heading").equalsIgnoreCase(""))
                            {

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
                            if(!jsonObjectList.getString("school_leaderboard_heading").equalsIgnoreCase(""))
                            {
                                JSONArray jsonArray1 = jsonObjectList.getJSONArray("school_leaderboard_data");
                            {
                                if (jsonArray1 != null && jsonArray1.length() != 0) {
                                    for (int i = 0; i < jsonArray1.length(); i++) {
                                        LeaderList1 leaderList = new LeaderList1();
                                        JSONObject jsonObjectList = jsonArray1.getJSONObject(i);
                                        leaderList.setSr(String.valueOf(i));
                                        leaderList.setName(jsonObjectList.getString("name"));
                                        leaderList.setPoint(jsonObjectList.getString("point"));
                                        leaderList.setImage(jsonObjectList.getString("image"));
                                        leaderList.setAccount_type(jsonObjectList.getString("account_type"));
                                        leaderList.setSelected(jsonObjectList.getString("selected"));
                                        leaderLists1.add(leaderList);
                                    }
                                }
                            }
                            }
                            JSONArray jsonArray = jsonObjectList.getJSONArray("batches_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        BadgesList badgesList = new BadgesList();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        badgesList.setID(jsonObjectList.getString("ID"));
                                        badgesList.setName(jsonObjectList.getString("name"));
                                        badgesList.setImage(jsonObjectList.getString("image"));
                                        badgesList.setStatus(jsonObjectList.getString("status"));
                                        badgesList.setTerms(jsonObjectList.getString("terms"));
                                        badgesList.setMessage(jsonObjectList.getString("message"));
                                        badgesLists.add(badgesList);
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
                badgesAdapter.notifyDataSetChanged();
                leaderBoardAdapter1.notifyDataSetChanged();

                try {
                    if(jsonObjectList.getString("leaderboard_heading").equalsIgnoreCase(""))
                    {
                        gheading.setVisibility(View.GONE);
                    }
                    else
                    {
                        gheading.setVisibility(View.VISIBLE);
                        gheading.setText(jsonObjectList.getString("leaderboard_heading")+"");
                    }

                    if(jsonObjectList.getString("school_leaderboard_heading").equalsIgnoreCase(""))
                    {
                        sheading.setVisibility(View.GONE);
                    }
                    else
                    {
                        sheading.setVisibility(View.VISIBLE);
                        sheading.setText(jsonObjectList.getString("school_leaderboard_heading")+"");
                    }

                    if(jsonObjectList.getString("batche_heading").equalsIgnoreCase(""))
                    {
                        bheading.setVisibility(View.GONE);
                    }
                    else
                    {
                        bheading.setVisibility(View.VISIBLE);
                        bheading.setText(jsonObjectList.getString("batche_heading")+"");
                    }

                    if (page == 0) {
                        if(leaderLists.size()>0) {
                            Glide.with(context)
                                    .load(number1)
                                    .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(ivCup1);

                            Glide.with(context)
                                    .load(leaderLists.get(0).getImage())
                                    .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(ivUser1);

                            tvName1.setText(leaderLists.get(0).getName());

                            tvScore1.setText(leaderLists.get(0).getPoint());

                            Glide.with(context)
                                    .load(number2)
                                    .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(ivCup2);

                            Glide.with(context)
                                    .load(leaderLists.get(1).getImage())
                                    .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(ivUser2);

                            tvName2.setText(leaderLists.get(1).getName());

                            tvScore2.setText(leaderLists.get(1).getPoint());


                            Glide.with(context)
                                    .load(number3)
                                    .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(ivCup3);

                            Glide.with(context)
                                    .load(leaderLists.get(2).getImage())
                                    .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(ivUser3);

                            tvName3.setText(leaderLists.get(2).getName());

                            tvScore3.setText(leaderLists.get(2).getPoint());
                        }
                        if(leaderLists1.size()>0) {
                            Glide.with(context)
                                    .load(number1)
                                    .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(ivCup11);

                            Glide.with(context)
                                    .load(leaderLists1.get(0).getImage())
                                    .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(ivUser11);

                            tvName1.setText(leaderLists1.get(0).getName());

                            tvScore1.setText(leaderLists1.get(0).getPoint());

                            Glide.with(context)
                                    .load(number2)
                                    .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(ivCup22);

                            Glide.with(context)
                                    .load(leaderLists1.get(1).getImage())
                                    .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(ivUser22);

                            tvName2.setText(leaderLists1.get(1).getName());

                            tvScore2.setText(leaderLists1.get(1).getPoint());


                            Glide.with(context)
                                    .load(number3)
                                    .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(ivCup33);

                            Glide.with(context)
                                    .load(leaderLists1.get(2).getImage())
                                    .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(ivUser33);

                            tvName3.setText(leaderLists1.get(2).getName());

                            tvScore3.setText(leaderLists1.get(2).getPoint());
                        }
                        else
                        {
                            leader_school.setVisibility(View.GONE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(leaderLists1.size()>=10)
                {
                    seefull1.setVisibility(View.VISIBLE);
                }
                else
                {
                    seefull1.setVisibility(View.GONE);
                }
                if(leaderLists.size()>=10)
                {
                    seefull.setVisibility(View.VISIBLE);
                }
                else
                {
                    seefull.setVisibility(View.GONE);
                }
            } else {
                if (page == 0) {
                    Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Util.getUserImage(getActivity())!=null)
        {
            ivAvatar=view.findViewById(R.id.ivAvatar);
            Glide.with(context)
                    .load(  Util.getUserImage(getContext()))
                    .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.app_logo)
                    .into(ivAvatar);
        }
    /*    new GetLeader().execute();
        new GetUserDATA().execute();*/
//        ivHelp.setVisibility(View.GONE);
   //     tvHelpHint.setVisibility(View.GONE);
    }

    ImageView ivHelp;
    TextView tvHelpHint,tvLevel;
    ProgressBar activeProgress;

    private void initComp(View view) {
        progressDialog = new ProgressDialog(context);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView1 = view.findViewById(R.id.recyclerView1);
        tvName = view.findViewById(R.id.tvName);
        etName = view.findViewById(R.id.etName);
        etEmail= view.findViewById(R.id.etEmail);
        tvEditProfile = view.findViewById(R.id.tvEditProfile);
        activeProgress = view.findViewById(R.id.activeProgress);
        tvLevel = view.findViewById(R.id.tvLevel);
        ivAvatar = view.findViewById(R.id.ivAvatar);
        view1 = view;
        rlPiano = context.findViewById(R.id.rlPiano);
        tvSelectAvatar = view.findViewById(R.id.tvSelectAvatar);

        pv = context.findViewById(R.id.pv);

        ivHelp = context.findViewById(R.id.ivHelp);
        tvHelpHint = context.findViewById(R.id.tvHelpHint);

        ivCup3 = view.findViewById(R.id.ivCup3);
        ivCup2 = view.findViewById(R.id.ivCup2);
        ivCup1 = view.findViewById(R.id.ivCup1);
        tvPage = view.findViewById(R.id.tvPage);
        leader_school = view.findViewById(R.id.leader_school);

        ivUser3 = view.findViewById(R.id.ivUser3);
        ivUser2 = view.findViewById(R.id.ivUser2);
        ivUser1 = view.findViewById(R.id.ivUser1);

        tvName3 = view.findViewById(R.id.tvName3);
        tvName2 = view.findViewById(R.id.tvName2);
        tvName1 = view.findViewById(R.id.tvName1);
        tvScore3 = view.findViewById(R.id.tvScore3);
        tvScore2 = view.findViewById(R.id.tvScore2);
        tvScore1 = view.findViewById(R.id.tvScore1);

        ivCup33 = view.findViewById(R.id.ivCup33);
        ivCup22 = view.findViewById(R.id.ivCup22);
        ivCup11 = view.findViewById(R.id.ivCup11);

        ivUser33 = view.findViewById(R.id.ivUser33);
        ivUser22 = view.findViewById(R.id.ivUser22);
        ivUser11 = view.findViewById(R.id.ivUser11);

        tvName33 = view.findViewById(R.id.tvName33);
        tvName22 = view.findViewById(R.id.tvName22);
        tvName11 = view.findViewById(R.id.tvName11);
        tvScore33 = view.findViewById(R.id.tvScore33);
        tvScore22 = view.findViewById(R.id.tvScore22);
        tvScore11 = view.findViewById(R.id.tvScore11);

        tvDate = view.findViewById(R.id.tvDate);
        progress_bar = view.findViewById(R.id.progress_bar);
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