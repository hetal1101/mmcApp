package com.makemusiccount.android.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.makemusiccount.android.R;
import com.makemusiccount.android.adapter.AvatarAdapter1;
import com.makemusiccount.android.adapter.AvatarAdapter11;
import com.makemusiccount.android.adapter.KeyboardAdapter1;
import com.makemusiccount.android.adapter.KeyboardAdapter11;
import com.makemusiccount.android.adapter.ThemeAdapter1;
import com.makemusiccount.android.adapter.ThemeAdapter11;
import com.makemusiccount.android.model.AvtarList;
import com.makemusiccount.android.model.CategoryList;
import com.makemusiccount.android.model.ThemeList;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Shop extends Fragment {



    public Shop() {
        // Required empty public constructor
    }
    RecyclerView rvAvatar;
    AvatarAdapter11 avatarAdapter;
    TextView tvheading;

    List<ThemeList> themeLists = new ArrayList<>();
    List<ThemeList> avatarLists = new ArrayList<>();
    List<ThemeList> keyboardLists = new ArrayList<>();
    ProgressDialog progressDialog;

    RecyclerView rvTheme;
    ThemeAdapter11 themeAdapter1;
    TextView tvheading1,userCoin,tvheadingAvtar,tvheadingTheme,tvheadingKeyboard;


    RecyclerView rvKeyboard;
    KeyboardAdapter11 keyboardAdapter1;
    Activity context;
    LottieAnimationView lottieAnimationView;
    LinearLayout llData;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_shop, container, false);

        context = getActivity();

        rvAvatar=view.findViewById(R.id.rvAvatar);
        rvTheme=view.findViewById(R.id.rvTheme);
        rvKeyboard=view.findViewById(R.id.rvKeyboard);
        tvheading1=view.findViewById(R.id.tvheading1);
        userCoin=view.findViewById(R.id.userCoin);
        lottieAnimationView=view.findViewById(R.id.lottieAnimationView);
        tvheadingAvtar=view.findViewById(R.id.tvheadingAvtar);
        tvheadingTheme=view.findViewById(R.id.tvheadingTheme);
        tvheadingKeyboard=view.findViewById(R.id.tvheadingKeyboard);
        llData=view.findViewById(R.id.llData);





        tvheading=view.findViewById(R.id.tvheading);
        rvAvatar.setHasFixedSize(false);
        rvAvatar.setLayoutManager(new GridLayoutManager(context,2));
        avatarAdapter=new AvatarAdapter11(getActivity(),avatarLists,99);
        rvAvatar.setAdapter(avatarAdapter);

        avatarAdapter.setOnItemClickListener(new AvatarAdapter11.OnItemClickListener() {
            @Override
            public void onItemClick(int i) {
                if(i==99)
                {
                    new getShopItems(context).execute();
                }
            }
        });

        rvTheme.setLayoutManager(new GridLayoutManager(context,2));
        rvTheme.setHasFixedSize(false);
        themeAdapter1=new ThemeAdapter11(getActivity(),themeLists,99);
        rvTheme.setAdapter(themeAdapter1);

        rvKeyboard.setLayoutManager(new GridLayoutManager(context,2));
        rvKeyboard.setHasFixedSize(false);
        keyboardAdapter1=new KeyboardAdapter11(getActivity(),keyboardLists,99);
        rvKeyboard.setAdapter(keyboardAdapter1);

        keyboardAdapter1.setOnItemClickListener(new AvatarAdapter11.OnItemClickListener() {
            @Override
            public void onItemClick(int i) {
                if(i==99)
                {
                    new getShopItems(context).execute();
                }
            }
        });



            new getShopItems(context).execute();

        return view;
    }

    String UserId = "", resMessage = "", resCode = "",user_coin="",heading="",avatar_label="",theme_label="",app_key_label="";



    @SuppressLint("StaticFieldLeak")
    public class getShopItems extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;
        String ID="",NAME="",IMAGE="";
        Context context1;

        public getShopItems( Context context1) {

            this.context1=context1;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            String strAPI = AppConstant.API_SHOP_ITEMS + Util.getUserId(context) + "&app_type=Android" ;
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
                        user_coin = jsonObjectList.getString("user_coin");
                        heading = jsonObjectList.getString("heading");
                        avatar_label = jsonObjectList.getString("avatar_label");
                        theme_label = jsonObjectList.getString("theme_label");
                        app_key_label = jsonObjectList.getString("app_key_label");

                        JSONArray jsonArrayTheme=jsonObjectList.getJSONArray("app_theme_list");
                        if(jsonArrayTheme!=null&&jsonArrayTheme.length()>0)
                        {
                            themeLists.clear();
                            for(int i=0;i<jsonArrayTheme.length();i++)
                            {
                                ThemeList themeList=new ThemeList();
                                themeList.setCoin(jsonArrayTheme.getJSONObject(i).getString("coin"));
                                themeList.setDataMaster(jsonArrayTheme.getJSONObject(i).getInt("data_master"));
                                themeList.setImage(jsonArrayTheme.getJSONObject(i).getString("image"));
                                themeList.setName(jsonArrayTheme.getJSONObject(i).getString("name"));
                                themeList.setShopID(jsonArrayTheme.getJSONObject(i).getString("shopID"));
                                themeList.setSortId(jsonArrayTheme.getJSONObject(i).getString("sort_id"));
                                themeList.setUserPurchase(jsonArrayTheme.getJSONObject(i).getString("user_purchase"));
                                themeLists.add(themeList);
                            }
                        }

                        JSONArray jsonArrayAvatar=jsonObjectList.getJSONArray("avatar_list");
                        if(jsonArrayAvatar!=null&&jsonArrayAvatar.length()>0)
                        {
                            avatarLists.clear();
                            for(int i=0;i<jsonArrayAvatar.length();i++)
                            {
                                ThemeList themeList=new ThemeList();
                                themeList.setCoin(jsonArrayAvatar.getJSONObject(i).getString("coin"));
                                themeList.setDataMaster(jsonArrayAvatar.getJSONObject(i).getInt("data_master"));
                                themeList.setImage(jsonArrayAvatar.getJSONObject(i).getString("image"));
                                themeList.setName(jsonArrayAvatar.getJSONObject(i).getString("name"));
                                themeList.setShopID(jsonArrayAvatar.getJSONObject(i).getString("shopID"));
                                themeList.setSortId(jsonArrayAvatar.getJSONObject(i).getString("sort_id"));
                                themeList.setUserPurchase(jsonArrayAvatar.getJSONObject(i).getString("user_purchase"));
                                avatarLists.add(themeList);
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
                                themeList.setDataMaster(jsonArrayKeyboard.getJSONObject(i).getInt("data_master"));
                                themeList.setImage(jsonArrayKeyboard.getJSONObject(i).getString("image"));
                                themeList.setName(jsonArrayKeyboard.getJSONObject(i).getString("name"));
                                themeList.setShopID(jsonArrayKeyboard.getJSONObject(i).getString("shopID"));
                                themeList.setSortId(jsonArrayKeyboard.getJSONObject(i).getString("sort_id"));
                                themeList.setUserPurchase(jsonArrayKeyboard.getJSONObject(i).getString("user_purchase"));
                                keyboardLists.add(themeList);
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
        //    progressDialog.dismiss();
            lottieAnimationView.setVisibility(View.GONE);
            if (resCode.equalsIgnoreCase("0")) {
               themeAdapter1.notifyDataSetChanged();
               avatarAdapter.notifyDataSetChanged();
               keyboardAdapter1.notifyDataSetChanged();
                llData.setVisibility(View.VISIBLE);


                tvheading1.setText(heading);
                userCoin.setText(user_coin);
                tvheadingAvtar.setText(avatar_label);
                tvheadingTheme.setText(theme_label);
                tvheadingKeyboard.setText(app_key_label);
            }
        }
    }
}