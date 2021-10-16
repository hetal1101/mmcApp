package com.makemusiccount.android.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.makemusiccount.android.R;
import com.makemusiccount.android.activity.DashboardActivity;
import com.makemusiccount.android.activity.NoNetworkActivity;
import com.makemusiccount.android.activity.SubCategoryActivity;
import com.makemusiccount.android.adapter.CategoryAdapter;
import com.makemusiccount.android.model.CategoryList;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Global;
import com.makemusiccount.android.util.Util;
import com.makemusiccount.pianoview.view.PianoView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.makemusiccount.android.util.AppConstant.NO_NETWORK_REQUEST_CODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubjectFragment extends Fragment {

    View view, view1;

    RelativeLayout rlPiano;

    PianoView pv;

    RecyclerView recyclerView;

    Activity context;

    Global global;

    LottieAnimationView lottieAnimationView;

    String UserId = "", resMessage = "", resCode = "";

    List<CategoryList> categoryLists = new ArrayList<>();

    CategoryAdapter categoryAdapter;

    public SubjectFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_subject, container, false);

        context = getActivity();

        global = new Global(context);

        initComp(view);

       /* view1.setVisibility(View.VISIBLE);
        rlPiano.setVisibility(View.VISIBLE);
        pv.setVisibility(View.VISIBLE);*/

        UserId = Util.getUserId(context); if(UserId==null) {UserId=""; }

        RecyclerView.LayoutManager mLayoutManagerBestProduct = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        //recyclerView.setLayoutManager(new GridLayoutManager(context, 2,GridLayoutManager.HORIZONTAL,false));
        recyclerView.setLayoutManager(mLayoutManagerBestProduct);
        recyclerView.setHasFixedSize(true);
        categoryAdapter = new CategoryAdapter(context, categoryLists);
        recyclerView.setAdapter(categoryAdapter);

        if (global.isNetworkAvailable()) {
            new GetCategory().execute();
        } else {
            retryInternet("category");
        }

        categoryAdapter.setOnItemClickListener((position, view, which) -> {
            if(categoryLists.get(position).getType().equalsIgnoreCase("subjects"))
            {
            DashboardActivity.CatId = categoryLists.get(position).getCatID();
            DashboardActivity.CatName = categoryLists.get(position).getName();
            DashboardActivity.CatProgess = categoryLists.get(position).getPercentage();
            DashboardActivity.CatShortDecsription = categoryLists.get(position).getShort_desc();
            DashboardActivity.CatbarColor = categoryLists.get(position).getBar_color();
            DashboardActivity.categoryListsNew=categoryLists;
            DashboardActivity.postion=position;
            Intent i = new Intent(context, SubCategoryActivity.class);
            startActivity(i);}
            else
            {
                DashboardActivity.CatId = categoryLists.get(position).getCatID();
                DashboardActivity.CatName = categoryLists.get(position).getName();
                DashboardActivity.CatProgess = categoryLists.get(position).getPercentage();
                DashboardActivity.CatShortDecsription = categoryLists.get(position).getShort_desc();
                DashboardActivity.CatbarColor = "#ffffff";
                Intent i = new Intent(context, SubCategoryActivity.class);
                startActivity(i);
            }
            /*if (which == 2) {
                MainActivity.CatId = categoryLists.get(position).getCatID();
                Fragment fragment = new SubCategoryFragment();
                llPianoView.setVisibility(View.GONE);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //fragmentTransaction.setCustomAnimations(R.anim.fragment_animation_fade_in, R.anim.fragment_animation_fade_out);
                fragmentTransaction.replace(R.id.frame_container, fragment);
                fragmentTransaction.commit();
                MainActivity.isHome = 2;
                MainActivity.isSubject = "Yes";
                MainActivity.tvCheck.setText("2");
            }

            if (which == 3) {
                MainActivity.CatId = categoryLists.get(position).getCatID();
                MainActivity.songType = "Premium";
                Fragment fragment = new SongListFragment();
                llPianoView.setVisibility(View.VISIBLE);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //fragmentTransaction.setCustomAnimations(R.anim.fragment_animation_fade_in, R.anim.fragment_animation_fade_out);
                fragmentTransaction.replace(R.id.frame_container, fragment);
                fragmentTransaction.commit();
                MainActivity.isHome = 1;
                MainActivity.tvCheck.setText("5");
            }*/
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
                if (extraValue.equalsIgnoreCase("category")) {
                    new GetCategory().execute();
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetCategory extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            lottieAnimationView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_CATEGORY + UserId
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
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("category_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        CategoryList categoryList = new CategoryList();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        categoryList.setCatID(jsonObjectList.getString("catID"));
                                        categoryList.setName(jsonObjectList.getString("name"));
                                        categoryList.setImage(jsonObjectList.getString("image"));
                                        categoryList.setSub_cats(jsonObjectList.getString("sub_cats"));
                                        categoryList.setType(jsonObjectList.getString("type"));
                                        categoryList.setPercentage(jsonObjectList.getString("percentage"));
                                        categoryList.setBar_color(jsonObjectList.getString("bar_color"));
                                        categoryList.setShort_desc(jsonObjectList.getString("short_desc"));
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
            lottieAnimationView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            if (resCode.equalsIgnoreCase("0")) {
                categoryAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    ImageView ivHelp;
    TextView tvHelpHint;

    @Override
    public void onResume() {
        super.onResume();
        /*ivHelp.setVisibility(View.GONE);
        tvHelpHint.setVisibility(View.GONE);*/
    }

    private void initComp(View view) {

        recyclerView = view.findViewById(R.id.recyclerView);
        lottieAnimationView = view.findViewById(R.id.lottieAnimationView);
       /* view1 = context.findViewById(R.id.view1);
        rlPiano = context.findViewById(R.id.rlPiano);
        pv = context.findViewById(R.id.pv);
        ivHelp = context.findViewById(R.id.ivHelp);
        tvHelpHint = context.findViewById(R.id.tvHelpHint);*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}