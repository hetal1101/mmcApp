package com.makemusiccount.android.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makemusiccount.android.R;
import com.makemusiccount.android.util.Global;
import com.makemusiccount.android.util.Util;

public class CleverWebViewActivity extends AppCompatActivity {

    RelativeLayout relativeMain;
    ProgressBar Pbar;
    TextView txtview;
    WebView webView;
    String status = "";
    String Url = "https://clever.com/oauth/authorize?response_type=code&redirect_uri=https%3A%2F%2Fwww.makemusiccount.online%2Fweb%2Fcleverlogin.php&client_id=86c05b1fd266b0114114&scope=read%3Auser_id+read%3Ateachers+read%3Astudents";

    Global global;
    Context context;
    int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            context = this;
        setTheme(Util.getTheme(context));             setContentView(R.layout.activity_clever_webview);

        context = this;
        global = new Global(context);
        initToolbar();

        txtview = findViewById(R.id.tV1);
        Pbar = findViewById(R.id.pB1);
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait..");

        webView = findViewById(R.id.webViewNew);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 && Pbar.getVisibility() == ProgressBar.GONE) {
                    Pbar.setVisibility(ProgressBar.VISIBLE);
                    txtview.setVisibility(View.VISIBLE);

                }
                Pbar.setProgress(progress);
                if (progress == 100) {

                    Pbar.setVisibility(ProgressBar.GONE);
                    txtview.setVisibility(View.GONE);
                }
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onLoadResource(WebView view, String url) {
            }

            public void onPageFinished(WebView view, String url) {
                try {

                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    //https://www.makemusiccount.online/web/index.php?view=clever&loginStatus=success&cleverID=151651615651

                    if (url.contains("loginStatus=success")) {

                        webView.setVisibility(View.GONE);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Uri uri = Uri.parse(url);
                                String cleverID = uri.getQueryParameter("cleverID");

                                Bundle b = new Bundle();
                                status = "captured";
                                b.putString("status", "success");
                                b.putString("cleverID", cleverID);
                                Intent i = getIntent();
                                i.putExtras(b);
                                setResult(Activity.RESULT_OK, i);
                                finish();
                            }
                        }, SPLASH_TIME_OUT);


                    } else if (url.contains("loginStatus=failed")) {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Uri uri = Uri.parse(url);
                                String cleverID = uri.getQueryParameter("cleverID");

                                Bundle b = new Bundle();
                                status = "failed";
                                b.putString("status", "failed");
                                b.putString("cleverID", cleverID);
                                Intent i = getIntent();
                                i.putExtras(b);
                                setResult(Activity.RESULT_OK, i);
                                finish();
                            }
                        }, SPLASH_TIME_OUT);
                    }

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

        });

        webView.loadUrl(Url);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (webView.canGoBack() && status.equals("")) {
            webView.goBack();
        } else {
            finish();
            /*AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Cancel Transaction");
            builder.setMessage("Pressing back would cancel your current transaction. Proceed to cancel?");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.setCanceledOnTouchOutside(false);
            alert.show();*/
        }
    }
}
