package com.makemusiccount.android.activity;

import android.app.Activity;
import android.content.Intent;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makemusiccount.android.R;
import com.makemusiccount.android.util.Global;
import com.makemusiccount.android.util.Util;

public class NoNetworkActivity extends AppCompatActivity {

    TextView tvOrderOnPhone, tvRefresh;

    Activity context;

    Global global;

    String extraValue = "";

    RelativeLayout rlMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            context = this;
        setTheme(Util.getTheme(context));             setContentView(      R.layout.activity_no_network);

        context = this;

        global = new Global(context);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            extraValue = bundle.getString("extraValue", "");
        }

        initComp();

        tvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (global.isNetworkAvailable()) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("extraValue", extraValue);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else {
                    ShowSnakBar("No Network Available", rlMain, context);
                }
            }
        });

        tvOrderOnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.callIntent(context, "9510069163");
            }
        });

    }

    public void ShowSnakBar(String s, View linearLayout, Activity login) {
        Snackbar snackbar = Snackbar.make(linearLayout, s, Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();
        TextView tv = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(login, R.color.red));
        sbView.setBackgroundColor(ContextCompat.getColor(login, R.color.white));
        snackbar.show();
    }

    private void initComp() {
        tvOrderOnPhone = findViewById(R.id.tvOrderOnPhone);
        tvRefresh = findViewById(R.id.tvRefresh);
        rlMain = findViewById(R.id.rlMain);
    }

    @Override
    public void onBackPressed() {

    }
}
