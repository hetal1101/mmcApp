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
import com.makemusiccount.android.activity.MainActivity;
import com.makemusiccount.android.activity.SubscribePackageActivity;
import com.makemusiccount.android.fragment.HomeFragment;
import com.makemusiccount.android.fragment.Shop;
import com.makemusiccount.android.model.AvtarList;
import com.makemusiccount.android.model.ThemeList;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Util;

import org.json.JSONObject;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.makemusiccount.android.activity.DashboardActivity.tabLayout;
import static com.makemusiccount.android.activity.DashboardActivity.viewPager;
import static com.makemusiccount.android.activity.MainActivity.llPianoView;
import static com.makemusiccount.android.fragment.LeaderBoardFragment.ivAvatar;
import static com.makemusiccount.android.util.Util.convertDpToPixel;

public class AvatarAdapter11 extends RecyclerView.Adapter<AvatarAdapter11.ViewHolder> {

    public  Activity context;
    private List<ThemeList> listData;
    public String resMessage="",resCode="";
    ProgressDialog progressDialog;
    int ll=0;

    OnItemClickListener mOnItemClickListener;
    public interface OnItemClickListener {
        void onItemClick( int i);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListenersxdxsdx) {
        this.mOnItemClickListener = mItemClickListenersxdxsdx;
    }

    @SuppressLint("StaticFieldLeak")
    public class Setavtar extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;
        String ID="",NAME="",IMAGE="";
        Context context1;

        public Setavtar(String ID,String IMAGE) {

            this.ID=ID;
            this.IMAGE=IMAGE;
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
            String strAPI = AppConstant.API_SHOP_PURCHASE + Util.getUserId(context) + "&shopID=" +ID +"&shop_type="+"avatar"+"&app_type="+"Android" ;
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
                dialog.cancel();
                Util.setUserImage(context,IMAGE);
                mOnItemClickListener.onItemClick(99);
            }
            else if(resCode.equalsIgnoreCase("1")){
                errorText.setText(resMessage+"");
                errorText.setVisibility(View.VISIBLE);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class Setavtar1 extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;
        String ID="",NAME="",IMAGE="";
        Context context1;

        public Setavtar1(String ID, String NAME, String IMAGE, Context context1) {
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

                notifyDataSetChanged();
            }
        }
    }

    AlertDialog dialog;
    TextView errorText;

    private void openPopup(int pos) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") View alert_layout = inflater.inflate(R.layout.popup_subscribe_package1, null);

        alertDialogBuilder.setView(alert_layout);



        dialog = alertDialogBuilder.create();

        dialog.setCancelable(false);
        TextView tvMsg=alert_layout.findViewById(R.id.tvMsg);
        ImageView image=alert_layout.findViewById(R.id.image);
        TextView coin=alert_layout.findViewById(R.id.coin);
         errorText=alert_layout.findViewById(R.id.errorText);

        Glide.with(context).load(listData.get(pos).getImage()).into(image);

        tvMsg.setText("BUY THIS AVATAR ?");
        coin.setText(""+listData.get(pos).getCoin());

        alert_layout.findViewById(R.id.a1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               new Setavtar(listData.get(pos).getShopID(),listData.get(pos).getImage()).execute();
            }
        });

        alert_layout.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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


    public AvatarAdapter11(Activity context, List<ThemeList> bean, int ll) {
        this.listData = bean;
        this.context = context;
        this.ll=ll;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_avatar1, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final ThemeList bean = listData.get(position);
        //holder.tvTitle.setText(bean.getName(), TextView.BufferType.SPANNABLE);
        Glide.with(context).load(bean.getImage()).into(holder.ivImage);

            holder.aa.setVisibility(View.GONE);
        if(Util.getUserImage(context).equalsIgnoreCase(bean.getImage()))
        {
            holder.aa.setVisibility(View.VISIBLE);
        }
        else
        {

            holder.aa.setVisibility(View.GONE);
        }


            if(bean.getUserPurchase().equalsIgnoreCase("No"))
            {
                holder.rlPre.setVisibility(View.VISIBLE);
            }
            else {
                holder.rlPre.setVisibility(View.GONE);
            }
       // }


        if (ll==99)
        {
            holder.llMain.setBackgroundColor(context.getResources().getColor(R.color.bootstrap_gray_light));
        }


        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(holder.rlPre.getVisibility()==View.VISIBLE)
                {
                    openPopup(position);
                }
                else
                {
                    Util.setUserImage(context,listData.get(position).getImage());
                    Glide.with(context)
                            .load(  Util.getUserImage(context))
                            .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.app_logo)
                            .into(ivAvatar);
                    notifyDataSetChanged();

                    new Setavtar1(listData.get(position).getShopID(),listData.get(position).getName(),listData.get(position).getImage(),context);

                }

               // new Setavtar(listData.get(position).getAvatarID(),listData.get(position).getAvatarName(),listData.get(position).getImage(),context).execute();
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

        CircleImageView ivImage;
        RelativeLayout llMain,rlPre;
        ImageView aa;

        public ViewHolder(View v) {
            super(v);
            ivImage = v.findViewById(R.id.image);
            llMain = v.findViewById(R.id.llMain);
            aa = v.findViewById(R.id.aa);
            rlPre = v.findViewById(R.id.rlPre);

        }
    }
}