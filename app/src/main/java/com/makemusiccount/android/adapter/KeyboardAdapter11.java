package com.makemusiccount.android.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import com.makemusiccount.android.model.AvtarList;
import com.makemusiccount.android.model.ThemeList;
import com.makemusiccount.android.preference.AppPersistence;
import com.makemusiccount.android.preference.AppPreference;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Util;
import com.makemusiccount.pianoview.entity.Piano;

import org.json.JSONObject;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.makemusiccount.android.activity.DashboardActivity.tabLayout;
import static com.makemusiccount.android.activity.DashboardActivity.viewPager;
import static com.makemusiccount.android.util.Util.convertDpToPixel;

public class KeyboardAdapter11 extends RecyclerView.Adapter<KeyboardAdapter11.ViewHolder> {

    public  Activity context;
    private List<ThemeList> listData;
    public String resMessage="",resCode="";
    ProgressDialog progressDialog;


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

        tvMsg.setText("BUY THIS KEYBOARD ?");
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

  AvatarAdapter11.OnItemClickListener mOnItemClickListener;
    public interface OnItemClickListener {
        void onItemClick( int i);
    }

    public void setOnItemClickListener(final AvatarAdapter11.OnItemClickListener mItemClickListenersxdxsdx) {
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
            String strAPI = AppConstant.API_SHOP_PURCHASE + Util.getUserId(context) + "&shopID=" +ID +"&shop_type="+"app_key_theme"+"&app_type="+"Android" ;
            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.e("API", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.Execute(RequestMethod.GET);
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
                Util.setUSER_KEYBOARD(context,ID);
                AppPreference.setPreference(context, AppPersistence.keys.selectedTheme, ""+ID);
                Piano.autoplayviewkey=Integer.parseInt(ID);
                mOnItemClickListener.onItemClick(99);
            }
            else if(resCode.equalsIgnoreCase("1")){
                errorText.setText(resMessage+"");
                errorText.setVisibility(View.VISIBLE);
            }
        }
    }


int ll=0;

    public KeyboardAdapter11(Activity context, List<ThemeList> bean, int ll) {
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
        holder.aa.setVisibility(View.GONE);
        String a1a1=Util.getUSER_KEYBOARDTheme(context);
        if(Util.getUSER_KEYBOARDTheme(context).equalsIgnoreCase(bean.getShopID()))
        {
            holder.aa.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.aa.setVisibility(View.GONE);
        }
        Glide.with(context).load(bean.getImage()).into(holder.ivImage);


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
                    openPopup1(position,listData.get(position).getShopID());
                }

                // new Setavtar(listData.get(position).getAvatarID(),listData.get(position).getAvatarName(),listData.get(position).getImage(),context).execute();
            }
        });
    }

    private void openPopup1(int pos,String shopID) {

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

                //Util.setUSER_KEYBOARD(context,shopID);
                AppPreference.setPreference(context, AppPersistence.keys.selectedTheme, ""+shopID);
                AppPreference.setPreference(context, AppPersistence.keys.USER_KEYBOARD, ""+shopID);
                Piano.autoplayviewkey=Integer.parseInt(shopID);
                notifyDataSetChanged();
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
        RelativeLayout llMain,rlPre;
        ImageView aa;

        public ViewHolder(View v) {
            super(v);
            ivImage = v.findViewById(R.id.image);
            llMain = v.findViewById(R.id.llMain);
            rlPre = v.findViewById(R.id.rlPre);
            aa = v.findViewById(R.id.aa);

        }
    }
}