package com.mahitab.ecommerce.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.models.MyOrdersModel;

import static com.mahitab.ecommerce.utils.CommonUtils.setArDefaultLocale;

public class MyOrderDetailsActivity extends AppCompatActivity {
    private static final String TAG ="MyOrderDetailsActivity";
    MyOrdersModel myOrdersModel;
    WebView mywebview;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_details);
        mywebview = findViewById(R.id.webView);

        myOrdersModel = (MyOrdersModel) getIntent().getSerializableExtra("myOrdersModel");
        Log.d(TAG, "onCreate: " + myOrdersModel.getStatutsUrl());
        mywebview.setWebViewClient(new MyBrowser());
        mywebview.getSettings().setLoadsImagesAutomatically(true);
        mywebview.getSettings().setJavaScriptEnabled(true);
        mywebview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mywebview.loadUrl(myOrdersModel.getStatutsUrl());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setArDefaultLocale(this);
        overridePendingTransition(0, 0); // remove activity default transition
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            Log.d(TAG, "MyBrowser: " + url);
            view.loadUrl(url);

            return true;
        }
    }
}