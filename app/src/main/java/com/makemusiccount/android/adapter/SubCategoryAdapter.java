package com.makemusiccount.android.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makemusiccount.android.R;
import com.makemusiccount.android.activity.DashboardActivity;
import com.makemusiccount.android.activity.SelectionActivity;
import com.makemusiccount.android.activity.SongDisplayActivity;
import com.makemusiccount.android.activity.SubCategoryActivity;
import com.makemusiccount.android.activity.SubscribePackageActivity;

import com.makemusiccount.android.model.CategoryList;
import com.makemusiccount.android.model.SongList;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Global;
import com.makemusiccount.android.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static com.makemusiccount.android.util.Util.convertDpToPixel;

/*
 * Created by Welcome on 25-01-2018.
 */

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.MyViewHolder> {

    private List<CategoryList> categoryLists;

    private Context context;
    List<SongList> songLists = new ArrayList<>();
    SongAdapter songAdapter;
    OnItemClickListener mOnItemClickListener;
    GridLayoutManager manager;
    MaterialShowcaseSequence sequence;
    Global global;
    String SubCatId = "";
    int selectedItem = 10000;
    int pagecode = 0;
    boolean IsLoading = false;
    String userId = "", resCode = "", resMessage = "",
            subscription_msg = "", subscription_img = "",
            badge_title = "", badge_msg = "", badge_img = "";

    public interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListenersxdxsdx) {
        this.mOnItemClickListener = mItemClickListenersxdxsdx;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvButton, tvTitle;
        /*ImageView ivImage;*/
        LinearLayout llMain;
        ImageView Icon, imgpluse, imgminu;
        LinearLayout llloadmore;
        MaterialRippleLayout a1;
        RecyclerView recyclerView;
        LottieAnimationView lottieAnimationView;

        MyViewHolder(View itemView) {
            super(itemView);
            tvButton = itemView.findViewById(R.id.tvButton);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            lottieAnimationView = itemView.findViewById(R.id.lottieAnimationView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            llloadmore = itemView.findViewById(R.id.llloadmore);
            a1 = itemView.findViewById(R.id.a1);
            imgpluse = itemView.findViewById(R.id.imgpluse);
            imgminu = itemView.findViewById(R.id.imgminu);
            /*ivImage = itemView.findViewById(R.id.ivImage);*/
            llMain = itemView.findViewById(R.id.llMain);
        }
    }

    public SubCategoryAdapter(Context context, List<CategoryList> categoryLists) {
        this.context = context;
        this.categoryLists = categoryLists;
        global = new Global(context);
        userId = Util.getUserId(context); if(userId==null) {userId=""; }
    }
    public SubCategoryAdapter(Context context, List<CategoryList> categoryLists,int selectedItem) {
        this.context = context;
        this.categoryLists = categoryLists;
        global = new Global(context);
        userId = Util.getUserId(context); if(userId==null) {userId=""; }
        if(userId==null)
        {
            userId="";
        }
        this.selectedItem= selectedItem;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sub_category_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int listPosition) {

        /*Glide.with(context)
                .load(categoryLists.get(listPosition).getImage())
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.app_logo)
                .into(holder.ivImage);*/

        Boolean a = isTablet(context);
        if (a) {
            manager = new GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false);
        } else {
            manager = new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false);
        }

        holder.recyclerView.setLayoutManager(manager);
        // holder.recyclerView.setLayoutManager(new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false));
        sequence = new MaterialShowcaseSequence((Activity) context, "complete1");
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(300);
        sequence.setConfig(config);
        songAdapter = new SongAdapter(context, songLists, sequence);
        holder.recyclerView.setAdapter(songAdapter);
        holder.recyclerView.setHasFixedSize(true);
        //holder.recyclerView.scrollToPosition(0);
        //holder.recyclerView.smoothScrollToPosition(0);

        holder.tvTitle.setText(categoryLists.get(listPosition).getName());
        holder.tvButton.setText(categoryLists.get(listPosition).getShort_desc());

        /*holder.tvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   mOnItemClickListener.onItemClick(listPosition, view, 2);
            }
        });*/
        if(resCode.equalsIgnoreCase("0"))
        {
            if(holder.recyclerView.getVisibility()==View.VISIBLE)
            {
                holder.llloadmore.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.llloadmore.setVisibility(View.GONE);
            }

        }
        else {
            holder.llloadmore.setVisibility(View.GONE);
        }
        holder.a1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IsLoading) {
                    holder.llloadmore.setVisibility(View.GONE);
                    pagecode++;
                    SubCatId = categoryLists.get(listPosition).getCatID();
                    holder.lottieAnimationView.setVisibility(View.VISIBLE);
                    if (global.isNetworkAvailable()) {
                        //new GetSongList().execute();
                        GetSongList task = new GetSongList(context, new MyInterface() {
                            @Override
                            public void myMethod(boolean result) {
                                if (result == true) {
                                    holder.lottieAnimationView.setVisibility(View.GONE);
                                } else {
                                    holder.lottieAnimationView.setVisibility(View.GONE);
                                }
                            }
                        });

                        task.execute();

                    } else {
                        Toast.makeText(context, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // mOnItemClickListener.onItemClick(listPosition, view, 2);
                if (holder.recyclerView.getVisibility() == View.GONE) {
                    ((Activity) context).findViewById(R.id.collapse_all).setVisibility(View.VISIBLE);
                    selectedItem = listPosition;
                    holder.recyclerView.setVisibility(View.VISIBLE);
                    holder.lottieAnimationView.setVisibility(View.VISIBLE);
                    pagecode = 0;
                    SubCatId = categoryLists.get(listPosition).getCatID();
                    IsLoading = false;
                    if (global.isNetworkAvailable()) {
                        //new GetSongList().execute();
                        GetSongList task = new GetSongList(context, new MyInterface() {
                            @Override
                            public void myMethod(boolean result) {
                                if (IsLoading == true) {
                                    holder.lottieAnimationView.setVisibility(View.GONE);
                                } else {
                                    holder.lottieAnimationView.setVisibility(View.GONE);
                                }
                            }
                        });

                        task.execute();

                    } else {
                        Toast.makeText(context, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                    }

                    //update_data(holder,"No");


                } else {

                    selectedItem = 10000;
                    //position = String.valueOf(listPosition);
                    notifyDataSetChanged();
                }
            }
        });

      /*  holder.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                int aa=recyclerView.getScrollY();
                int diff =  (recyclerView.getHeight() - recyclerView
                        .getScrollY());

                if (diff == 0) {
                    if (global.isNetworkAvailable()) {
                        //new GetSongList().execute();
                        GetSongList task = new GetSongList(context, new MyInterface() {
                            @Override
                            public void myMethod(boolean result) {
                                if (result == true) {
                                    holder.lottieAnimationView.setVisibility(View.GONE);
                                } else {
                                    holder.lottieAnimationView.setVisibility(View.GONE);
                                }
                            }
                        });

                        task.execute();

                    } else {
                        Toast.makeText(context, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                    }

                    // your pagination code
                }

             *//*   if (resCode.equalsIgnoreCase("0")) {
                    int totalItemCount = manager.getItemCount();
                    int lastVisible = manager.findLastCompletelyVisibleItemPosition();
                    boolean endHasBeenReached = lastVisible +1>= totalItemCount;
                    if (totalItemCount > 0 && endHasBeenReached) {
                        if (IsLoading) {
                            pagecode++;
                            holder.lottieAnimationView.setVisibility(View.VISIBLE);

                        }
                    }
                } else {
                    holder.lottieAnimationView.setVisibility(View.GONE);
                }*//*


            }
        });*/

        songAdapter.setOnItemClickListener((position, view, which) -> {
            if (which == 3) {
                //showPopup(position);


                    /*MainActivity.SongId = songLists.get(position).getID();
                    MainActivity.SongName = songLists.get(position).getName();
                    MainActivity.SongHintImage = songLists.get(position).getSong_hint_image();*/
                    SubCategoryActivity.SongId = songLists.get(position).getID();
                    SubCategoryActivity.SongName = songLists.get(position).getName();
                    SubCategoryActivity.SongHintImage = songLists.get(position).getSong_hint_image();

                    Intent i = new Intent(context, SongDisplayActivity.class);
                    i.putExtra("SongId", songLists.get(position).getID());
                    i.putExtra("SongName", songLists.get(position).getName());
                    i.putExtra("SongNameintroname", songLists.get(position).getSong_intro_name());
                    i.putExtra("SongNameintro", songLists.get(position).getSong_intro());
                    i.putExtra("songImage",songLists.get(position).getImage());
                    i.putExtra("SongHintImage", songLists.get(position).getSong_hint_image());
                    i.putExtra("url", songLists.get(position).getUrl()+"");
                    context.startActivity(i);


            }
            if(which ==5)
            {
             if(userId.equalsIgnoreCase(""))
             {
                 openPopup();
             }
             else {
                 if(subscription_msg.equalsIgnoreCase(""))
                 {
                     SubCategoryActivity.SongId = songLists.get(position).getID();
                     SubCategoryActivity.SongName = songLists.get(position).getName();
                     SubCategoryActivity.SongHintImage = songLists.get(position).getSong_hint_image();

                     Intent i = new Intent(context, SongDisplayActivity.class);
                     i.putExtra("SongId", songLists.get(position).getID());
                     i.putExtra("SongName", songLists.get(position).getName());
                     i.putExtra("SongNameintroname", songLists.get(position).getSong_intro_name());
                     i.putExtra("SongNameintro", songLists.get(position).getSong_intro());
                     i.putExtra("songImage",songLists.get(position).getImage());
                     i.putExtra("SongHintImage", songLists.get(position).getSong_hint_image());
                     i.putExtra("url", songLists.get(position).getUrl()+"");
                     context.startActivity(i);
                 }
                 else
                 {
                     openPopup();
                 }
             }


            }
        });

        if (selectedItem == listPosition) {

            holder.imgpluse.setVisibility(View.GONE);
            holder.imgminu.setVisibility(View.VISIBLE);
            holder.recyclerView.setVisibility(View.VISIBLE);
        } else {
            holder.llloadmore.setVisibility(View.GONE);
            holder.imgpluse.setVisibility(View.VISIBLE);
            holder.imgminu.setVisibility(View.GONE);
            holder.recyclerView.setVisibility(View.GONE);
        }
    }

    AlertDialog dialog;
    public void  change_ui()
    {
        selectedItem=10000;
        notifyDataSetChanged();
        ((Activity) context).findViewById(R.id.collapse_all).setVisibility(View.GONE);
    }
    private void openPopup() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") View alert_layout = inflater.inflate(R.layout.popup_subscribe_package, null);

        alertDialogBuilder.setView(alert_layout);

        dialog = alertDialogBuilder.create();

        dialog.setCancelable(false);

        TextView tvTitle = alert_layout.findViewById(R.id.tvTitle);
        TextView tvMsg = alert_layout.findViewById(R.id.tvMsg);
        TextView btnCancel = alert_layout.findViewById(R.id.btnCancel);
        TextView btnSubscribe = alert_layout.findViewById(R.id.btnSubscribe);
        ImageView ivIcon = alert_layout.findViewById(R.id.ivIcon);
        if(userId.equalsIgnoreCase(""))
        {
            tvMsg.setText("Please Sign in / Sign Up to Explore Premium Songs!");
            tvTitle.setText("Please Sign in / Sign Up to Explore Premium Songs!");
            btnSubscribe.setText("Sign in / Sign Up");
        }
        else {
            tvMsg.setText(subscription_msg);
            tvTitle.setText(subscription_msg);
            btnSubscribe.setText("Subscribe");
        }


        btnCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });

        btnSubscribe.setOnClickListener(view -> {

            dialog.dismiss();
            if(!userId.equalsIgnoreCase(""))
            {
                context.startActivity(new Intent(context, SubscribePackageActivity.class));
            }
            else {
                context.startActivity(new Intent(context, SelectionActivity.class));
            }

        });

        Glide.with(context)
                .load(subscription_img)
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

   /* private void update_data(MyViewHolder holder, String call) {
        if (call.equalsIgnoreCase("Yes")) {
            holder.lottieAnimationView.setVisibility(View.GONE);
        }

    }*/

    public interface MyInterface {
        public void myMethod(boolean result);
    }

    @SuppressLint("StaticFieldLeak")
    private class GetSongList extends AsyncTask<Void, Void, Boolean> {
        JSONObject jsonObjectList;
        private MyInterface mListener;

        public GetSongList(Context context, MyInterface mListener) {

            this.mListener = mListener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            resCode = "";
            resMessage = "";

            if (pagecode == 0) {
                songLists.clear();
            }
        }


        protected Boolean doInBackground(Void... params) {
            String strAPI="";
            Boolean a;
            if(DashboardActivity.CatId.equalsIgnoreCase("0"))
            {
                 strAPI = AppConstant.API_TUTORIALS + ""
                        + "&app_type=" + "Android" + "&catID=" + "0" + "&pagecode=" + pagecode;


                String strAPITrim = strAPI.replaceAll(" ", "%20");
                Log.d("strAPI", strAPITrim);
                a = true;
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
                                JSONArray jsonArray = jsonObjectList.getJSONArray("tutorial_list");
                                {
                                    if (jsonArray != null && jsonArray.length() != 0) {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            SongList tutorialList = new SongList();
                                            JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                            tutorialList.setID(jsonObjectList.getString("ID"));
                                            tutorialList.setName(jsonObjectList.getString("name"));
                                            tutorialList.setUrl(jsonObjectList.getString("video"));
                                            tutorialList.setImage(jsonObjectList.getString("image"));
                                            tutorialList.setStatus(jsonObjectList.getString("status"));
                                            tutorialList.setPlay_songs("No");
                                            tutorialList.setSong_file("");
                                            tutorialList.setSong_hint_image("");
                                            tutorialList.setArtist("");
                                            tutorialList.setPlay_autoplay("");
                                            tutorialList.setSong_level("");
                                            tutorialList.setSong_quiz("");
                                            tutorialList.setSong_intro_name(jsonObjectList.getString("name"));
                                            tutorialList.setSong_intro("");
                                            songLists.add(tutorialList);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }





            }
            else {
                 strAPI = AppConstant.API_SONG_LIST + userId
                        + "&catID=" + DashboardActivity.CatId
                        + "&subcatID=" + SubCatId
                        + "&pagecode=" + pagecode
                        + "&search_text=" + ""
                        + "&songType=" + "Premium"
                        + "&app_type=" + "Android";

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
             a = true;
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
                            subscription_msg = jsonObjectList.getString("subscription_msg");
                            subscription_img = jsonObjectList.getString("subscription_img");
                            badge_title = jsonObjectList.getString("badge_title");
                            badge_msg = jsonObjectList.getString("badge_msg");
                            badge_img = jsonObjectList.getString("badge_img");
                            JSONArray jsonArray = jsonObjectList.getJSONArray("song_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    List<SongList> FreesongLists = new ArrayList<>();
                                    List<SongList> PremiumsongLists = new ArrayList<>();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        SongList songList = new SongList();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        songList.setID(jsonObjectList.getString("ID"));
                                        songList.setName(jsonObjectList.getString("name"));
                                        songList.setImage(jsonObjectList.getString("image"));
                                        songList.setStatus(jsonObjectList.getString("status"));
                                        songList.setPlay_songs(jsonObjectList.getString("play_songs"));
                                        songList.setSong_file(jsonObjectList.getString("song_file"));
                                        songList.setSong_hint_image(jsonObjectList.getString("song_hint_image"));
                                        songList.setArtist(jsonObjectList.getString("artist"));
                                        songList.setPlay_autoplay(jsonObjectList.getString("play_autoplay"));
                                        songList.setSong_level(jsonObjectList.getString("song_level"));
                                        songList.setSong_quiz(jsonObjectList.getString("song_quiz"));
                                        songList.setSong_intro_name(jsonObjectList.getString("song_intro_name"));
                                        songList.setSong_intro(jsonObjectList.getString("song_intro"));
                                       // songLists.add(songList);
                                        if(songList.getSong_level().equalsIgnoreCase("free"))
                                        {
                                            FreesongLists.add(songList);
                                        }
                                        else
                                        {
                                            PremiumsongLists.add(songList);
                                        }
                                    }
                                    Collections.shuffle(PremiumsongLists);
                                    if(FreesongLists.size()>0)
                                    {
                                        songLists.addAll(0,FreesongLists);
                                    }
                                    if(PremiumsongLists.size()>0)
                                    {
                                    songLists.addAll(PremiumsongLists);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                a = false;
                e.printStackTrace();
            }
            }
            return a;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            if (resCode.equalsIgnoreCase("0")) {
                IsLoading = true;

                if (mListener != null)
                    mListener.myMethod(b);
                songAdapter.notifyDataSetChanged();
                notifyDataSetChanged();
            } else {
                IsLoading = false;
                if (mListener != null)
                    mListener.myMethod(b);
                //songAdapter.notifyDataSetChanged();
               notifyDataSetChanged();
                if (pagecode == 0) {
                    Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }


    @Override
    public int getItemCount() {
        return categoryLists.size();
    }
}
