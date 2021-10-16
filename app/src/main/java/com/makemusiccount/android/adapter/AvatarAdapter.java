package com.makemusiccount.android.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makemusiccount.android.R;
import com.makemusiccount.android.model.AvtarList;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Util;

import org.json.JSONObject;

import java.util.List;

public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.ViewHolder> {

    public  Activity context;
    private List<AvtarList> listData;
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




    public AvatarAdapter(Activity context, List<AvtarList> bean) {
        this.listData = bean;
        this.context = context;
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
        final AvtarList bean = listData.get(position);
        //holder.tvTitle.setText(bean.getName(), TextView.BufferType.SPANNABLE);
        if(Util.getUserImage(context).equalsIgnoreCase(bean.getImage()))
        {
            holder.aa.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.aa.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(bean.getImage())
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.ivImage);

        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Setavtar(listData.get(position).getAvatarID(),listData.get(position).getAvatarName(),listData.get(position).getImage(),context).execute();
            }
        });

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