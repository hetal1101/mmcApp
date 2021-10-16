package com.makemusiccount.android.activity;

import android.graphics.Bitmap;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.makemusiccount.android.R;
import com.makemusiccount.android.util.Util;

public class WebViewActivity extends AppCompatActivity {

    WebView webBlog;
    String webURL = "";
    LottieAnimationView lottieAnimationView;
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(Util.getTheme(this));             setContentView(      R.layout.activity_web_view);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            webURL = b.getString("webURL");
            Log.e("wenncnn", webURL);
        }

        webBlog = findViewById(R.id.webBlog);
        lottieAnimationView = findViewById(R.id.lottieAnimationView);
        ivBack = findViewById(R.id.ivBack);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        WebSettings webSettings = webBlog.getSettings();
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);

        }
        webBlog.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        webBlog.loadUrl(webURL);

        webBlog.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }

        });
        webBlog.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                try {
                    if (webBlog.getVisibility() == View.VISIBLE)
                        webBlog.setVisibility(View.INVISIBLE);
                    if (lottieAnimationView.getVisibility() == View.INVISIBLE)
                        lottieAnimationView.setVisibility(View.VISIBLE);
                } catch (Exception ex) {

                }

            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Toast.makeText(WebViewActivity.this, "Please retry", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                try {
                    if (webBlog.getVisibility() == View.INVISIBLE)
                        webBlog.setVisibility(View.VISIBLE);
                    if (lottieAnimationView.getVisibility() == View.VISIBLE)
                        lottieAnimationView.setVisibility(View.INVISIBLE);
                } catch (Exception ex) {

                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (webBlog.canGoBack()) {
            webBlog.goBack();
        } else {
            if (webBlog != null) {
                webBlog.freeMemory();
                webBlog.clearHistory();
                webBlog = null;
            }
            super.onBackPressed();
        }
    }
}