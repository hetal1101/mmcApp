package com.makemusiccount.android.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.makemusiccount.android.R;
import com.makemusiccount.android.activity.DashboardActivity;
import com.makemusiccount.android.activity.NoNetworkActivity;
import com.makemusiccount.android.activity.SubCategoryActivity;
import com.makemusiccount.android.adapter.SubCategoryAdapter;
import com.makemusiccount.android.model.CategoryList;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Global;
import com.makemusiccount.android.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.makemusiccount.android.util.AppConstant.NO_NETWORK_REQUEST_CODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubCategoryFragment extends Fragment {

    View view;

    RecyclerView recyclerView;

    Activity context;

    Global global;


    ProgressDialog progressDialog;

    String UserId = "", resMessage = "", resCode = "";

    List<CategoryList> categoryLists = new ArrayList<>();

    SubCategoryAdapter subCategoryAdapter;

    TextView tvBack, tvName,tvshortdec;

    ProgressBar activeProgress;

    LinearLayout llNext;

    public SubCategoryFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        context.setTheme(Util.getTheme(context));
        view = inflater.inflate(R.layout.fragment_sub_category, container, false);



        global = new Global(context);

        initComp(view);

        UserId = Util.getUserId(context); if(UserId==null) {UserId=""; }
        if( DashboardActivity.CatId.equalsIgnoreCase("0"))
        {
            llNext.setVisibility(View.GONE);
        }
        else
        {

            if(!(DashboardActivity.postion+1<DashboardActivity.categoryListsNew.size()))
            {
                llNext.setVisibility(View.GONE);
            }
            else
            {
                llNext.setVisibility(View.VISIBLE);
            }
        }

        RecyclerView.LayoutManager mLayoutManagerBestProduct = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManagerBestProduct);
        recyclerView.setHasFixedSize(true);
        subCategoryAdapter = new SubCategoryAdapter(context, categoryLists);
        recyclerView.setAdapter(subCategoryAdapter);
        view.findViewById(R.id.collapse_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subCategoryAdapter.change_ui();
               /* subCategoryAdapter = new SubCategoryAdapter(context, categoryLists,10000);
                subCategoryAdapter.notifyDataSetChanged();*/
            }
        });
        if(!DashboardActivity.CatId.equalsIgnoreCase("0"))
        {
            if (global.isNetworkAvailable()) {
                new GetSubCategory().execute();
            } else {
                retryInternet("song_sub_cat");
            }

        }
        else
        {
            if (global.isNetworkAvailable()) {
                new GetSubCategory().execute();
            } else {
                retryInternet("song_sub_cat");
            }

        }

        subCategoryAdapter.setOnItemClickListener(new SubCategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, int which) {
                /*if (which == 2) {
                    MainActivity.SubCatId = categoryLists.get(position).getCatID();
                    MainActivity.songType = "Premium";
                    Fragment fragment = new SongListFragment();
                    llPianoView.setVisibility(View.VISIBLE);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    //fragmentTransaction.setCustomAnimations(R.anim.fragment_animation_fade_in, R.anim.fragment_animation_fade_out);
                    fragmentTransaction.replace(R.id.frame_container, fragment);
                    fragmentTransaction.commit();
                    MainActivity.isHome = 5;
                    MainActivity.tvCheck.setText("5");
                }*/
            }
        });

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.finish();
            }
        });

        tvName.setText(DashboardActivity.CatName);
        tvshortdec=view.findViewById(R.id.tvshortdec);
        tvshortdec.setText(DashboardActivity.CatShortDecsription);
        activeProgress.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_br));
        activeProgress.setProgressTintList(ColorStateList.valueOf(Color.parseColor(DashboardActivity.CatbarColor)));
        activeProgress.setProgress(Integer.parseInt(DashboardActivity.CatProgess));


        llNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=0;


                List<CategoryList> categoryLists2=DashboardActivity.categoryListsNew;
                if(DashboardActivity.postion+1<DashboardActivity.categoryListsNew.size()) {
                    position = DashboardActivity.postion + 1;
                    DashboardActivity.CatId = categoryLists2.get(position).getCatID();
                    DashboardActivity.CatName = categoryLists2.get(position).getName();
                    DashboardActivity.CatProgess = categoryLists2.get(position).getPercentage();
                    DashboardActivity.CatShortDecsription = categoryLists2.get(position).getShort_desc();
                    DashboardActivity.CatbarColor = categoryLists2.get(position).getBar_color();
                    DashboardActivity.postion = position;
                    context.finish();
                    Intent i = new Intent(context, SubCategoryActivity.class);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(context, "No Next Subject Available.", Toast.LENGTH_SHORT).show();
                }


            }
        });

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
                if (extraValue.equalsIgnoreCase("song_sub_cat")) {
                    new GetSubCategory().execute();
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetSubCategory extends AsyncTask<String, Void, String> {
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
            String strAPI="";
            if(!DashboardActivity.CatId.equalsIgnoreCase("0"))
            {
             strAPI = AppConstant.API_SUB_CATEGORY + UserId
                    + "&catID=" + DashboardActivity.CatId
                    + "&app_type=" + "Android";}

            else
            {
                 strAPI = AppConstant.API_SUB_TUTORIALS + UserId
                        + "&catID=" + DashboardActivity.CatId
                        + "&app_type=" + "Android";
            }
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
                            JSONArray jsonArray = jsonObjectList.getJSONArray("sub_category_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        CategoryList categoryList = new CategoryList();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        categoryList.setCatID(jsonObjectList.getString("subcatID"));
                                        categoryList.setShort_desc(jsonObjectList.getString("short_desc"));
                                        categoryList.setName(jsonObjectList.getString("name"));
                                        categoryList.setImage(jsonObjectList.getString("image"));
                                        categoryList.setSub_cats(jsonObjectList.getString("sub_cats"));
                                        categoryLists.add(categoryList);
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
                subCategoryAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

  /*  ImageView ivHelp;
    TextView tvHelpHint;*/

    @Override
    public void onResume() {
        super.onResume();
        /*ivHelp.setVisibility(View.GONE);
        tvHelpHint.setVisibility(View.GONE);*/
    }


    private void initComp(View view) {
        tvBack = view.findViewById(R.id.tvBack);
        tvName = view.findViewById(R.id.tvName);
        activeProgress = view.findViewById(R.id.activeProgress);
        llNext = view.findViewById(R.id.llNext);
        progressDialog = new ProgressDialog(context);
        recyclerView = view.findViewById(R.id.recyclerView);
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
