package com.makemusiccount.android.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makemusiccount.android.R;
import com.makemusiccount.android.activity.DashboardActivity;
import com.makemusiccount.android.activity.SelectionActivity;
import com.makemusiccount.android.model.AvtarList;
import com.makemusiccount.android.model.DashboardList;
import com.makemusiccount.android.model.ThemeList;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Util;

import org.json.JSONObject;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.makemusiccount.android.activity.DashboardActivity.tabLayout;
import static com.makemusiccount.android.activity.DashboardActivity.viewPager;
import static com.makemusiccount.android.util.Util.convertDpToPixel;

public class ThemeAdapter1 extends RecyclerView.Adapter<ThemeAdapter1.ViewHolder> {

    public  Activity context;
    private List<ThemeList> listData;
    public String resMessage="",resCode="";
    ProgressDialog progressDialog;



    @SuppressLint("StaticFieldLeak")
    private class Setavtar extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;
        String ID="",NAME="",IMAGE="";
        Context context1;

        public Setavtar(String ID, String NAME, String IMAGE, Context context1) {
            this.NAME=NAME;
            this.ID=ID;
            this.IMAGE=IMAGE;
            this.context1=context1;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(context);
            progressDialog.setTitle("Loading..");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String strAPI = AppConstant.API_AVATAR_CHANGE + Util.getUserId(context) + "&avatarID=" +ID +"&avatarName="+NAME ;
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
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            if (resCode.equalsIgnoreCase("0")) {
                Util.setUserImage(context,IMAGE);
                ((Activity)context1).finish();
            }
        }
    }



int ll=0;
    public ThemeAdapter1(Activity context, List<ThemeList> bean, int ll) {
        this.listData = bean;
        this.context = context;
        this.ll=ll;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_avatar, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final ThemeList bean = listData.get(position);
        //holder.tvTitle.setText(bean.getName(), TextView.BufferType.SPANNABLE);
        if(Util.getUserTheme(context).equalsIgnoreCase(bean.getUsershopID()))
        {
            holder.aa.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.aa.setVisibility(View.GONE);
        }

         if(Util.getUserTheme(context).equalsIgnoreCase("")&& position==0)
            {
                holder.aa.setVisibility(View.VISIBLE);
            }

        if(!listData.get(position).getUsershopID().equalsIgnoreCase("9990"))
        {
            Glide.with(context)
                    .load(bean.getImage())
                    .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.ivImage);

        }
        else
        {

        }
        if(ll==99)
        {
            holder.llMain.setBackgroundColor(context.getResources().getColor(R.color.bootstrap_gray_light));
        }


        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!listData.get(position).getUsershopID().equalsIgnoreCase("9990"))
                {


                    openPopup(position);

                }
                else
                {
                    viewPager.setCurrentItem(3);
                    tabLayout.getTabAt(3).select();
                }

               // new Setavtar(listData.get(position).getAvatarID(),listData.get(position).getAvatarName(),listData.get(position).getImage(),context).execute();
            }
        });

    }
    AlertDialog dialog;
    private void openPopup(int pos) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") View alert_layout = inflater.inflate(R.layout.popup_subscribe_package12, null);

        alertDialogBuilder.setView(alert_layout);



        dialog = alertDialogBuilder.create();

        dialog.setCancelable(false);
        TextView tvMsg=alert_layout.findViewById(R.id.tvMsg);
        CircleImageView image=alert_layout.findViewById(R.id.image);
        Glide.with(context).load(listData.get(pos).getImage()).into(image);

        tvMsg.setText("ARE YOU SURE ?");

        alert_layout.findViewById(R.id.a1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Util.setUserTheme(context,listData.get(pos).getUsershopID());
                context.setTheme(Util.getTheme(context));
                notifyDataSetChanged();
                context.finish();
               context.startActivity(new Intent(context,DashboardActivity.class).putExtra("changeTheme","yes"));
                context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


                dialog.cancel();
            }
        });

        alert_layout.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyDataSetChanged();
                dialog.cancel();
            }
        });




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

    @Override
    public int getItemCount() {
        return listData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivImage;
        RelativeLayout llMain;
        ImageView aa;

        public ViewHolder(View v) {
            super(v);
            ivImage = v.findViewById(R.id.image);
            llMain = v.findViewById(R.id.llMain);
            aa = v.findViewById(R.id.aa);

        }
    }
}