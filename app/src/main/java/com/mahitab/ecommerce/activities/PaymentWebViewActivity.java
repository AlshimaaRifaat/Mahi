package com.mahitab.ecommerce.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.managers.GraphClientManager;
import com.mahitab.ecommerce.managers.interfaces.BaseCallback;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.Storefront;

import java.util.Timer;
import java.util.TimerTask;

import static com.mahitab.ecommerce.utils.CommonUtils.setArDefaultLocale;

public class PaymentWebViewActivity extends AppCompatActivity {
    private static final String TAG = "PaymentWebViewActivity";
    private SharedPreferences sharedPreferences;
    private boolean running;
    private Timer requestIntervalTimer;
    private int previousOrderNumber = 0;
    private boolean getPreviousOrderNumberOnce;
    private String sign;

    private boolean loadingFinished = true;
    private boolean redirect = false;

    private WebView wvCheckOut;
    private ProgressBar pbLoadingPayment;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_web_view);

        initView();
        sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        if (getIntent().getExtras() != null) {
            String webUrl = getIntent().getExtras().getString("web_url");
            wvCheckOut.setWebViewClient(new MyBrowser());
            wvCheckOut.getSettings().setAllowFileAccess(false); // handel WebView failing only the very first time crash
            wvCheckOut.getSettings().setLoadsImagesAutomatically(true);
            wvCheckOut.getSettings().setJavaScriptEnabled(true);
            wvCheckOut.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            wvCheckOut.loadUrl(webUrl);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    private void getSavedAccessToken() {
        String accessToken = sharedPreferences.getString("token", null);
        Log.d(TAG, "getSavedAccessToken: " + accessToken);
        queryMyOrders(accessToken);
    }

    private void queryMyOrders(String accessToken) {
        Storefront.QueryRootQuery query = Storefront.query(root -> root
                .customer(accessToken, customer -> customer
                        .orders(arg -> arg.first(100), connection -> connection
                                .edges(edge -> edge
                                        .node(Storefront.OrderQuery::orderNumber
                                        )
                                )
                        )
                )
        );
        getMyOrdersList(query, new BaseCallback() {
            @Override
            public void onResponse(int status) {
                runOnUiThread(() -> {
                    if (status == 200) {
                        Log.e(TAG, "onResponse: getMyOrdersList");
                    }
                });

            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    private void getMyOrdersList(Storefront.QueryRootQuery query, BaseCallback callback) {
        GraphClientManager.mClient.queryGraph(query).enqueue(new GraphCall.Callback<Storefront.QueryRoot>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {
                if (!response.hasErrors()) {

                    Storefront.OrderConnection connection = response.data().getCustomer().getOrders();
                    if (!getPreviousOrderNumberOnce) {
                        getPreviousOrderNumberOnce = true;
                        previousOrderNumber = connection.getEdges().get(connection.getEdges().size() - 1).getNode().getOrderNumber();
                    }

                    if (previousOrderNumber != 0) {
                        if (previousOrderNumber != connection.getEdges().get(connection.getEdges().size() - 1).getNode().getOrderNumber()) {
                            runOnUiThread(() -> {
                                Toast.makeText(PaymentWebViewActivity.this, "Order Completed", Toast.LENGTH_LONG).show();
                                timerStop();
                                //TODO Handle after order completed process
                                //TODO You could simply finish this activty and intent to Home with an extra to empty cart
                                Intent navigate = new Intent(PaymentWebViewActivity.this, HomeActivity.class);
                                sign = "mark";
                                navigate.putExtra("from_payment", sign);
                                startActivity(navigate);
                            });
                        }
                    }

                    callback.onResponse(BaseCallback.RESULT_OK);
                    return;
                }
                callback.onFailure(response.errors().get(0).message());
            }


            @Override
            public void onFailure(@NonNull GraphError error) {
                Log.d(TAG, "onFailure: " + error.getMessage());

            }
        });
    }

    private void initView() {
        wvCheckOut= findViewById(R.id.wvCheckOut_PaymentWebViewActivity);
        pbLoadingPayment = findViewById(R.id.pbLoadingPayment_PaymentWebViewActivity);
    }

    public void onPause() {
        super.onPause();
        if (running) {
            timerStop();
        }
    }

    public void onResume() {
        super.onResume();
        setArDefaultLocale(this);
        overridePendingTransition(0, 0); // remove activity default transition
        if (!running) {
            timerStart();
        }

    }

    public void timerStart() {
        running = true;
        if (requestIntervalTimer == null) {
            requestIntervalTimer = new Timer();
            int REQUEST_INTERVAL = 2000;
            requestIntervalTimer.schedule(new TimerTask() {
                public void run() {
                    runOnUiThread(TimerRunnable);
                }
            }, 0, REQUEST_INTERVAL);
        }
    }

    void timerStop() {
        running = false;
        try {
            if (requestIntervalTimer != null) {
                requestIntervalTimer.cancel();
                requestIntervalTimer.purge();
                requestIntervalTimer = null;
            }
        } catch (NullPointerException e) {
            running = true;
        }
    }

    Runnable TimerRunnable = new Runnable() {
        public void run() {
            if (sharedPreferences.getString("email", null) != null)
                getSavedAccessToken();
        }
    };


    private class MyBrowser extends WebViewClient {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (!loadingFinished) {
                redirect = true;
            }

            loadingFinished = false;
            Log.d(TAG, "onCreate: " + url);
            view.loadUrl(url);
            return true;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (!loadingFinished) {
                redirect = true;
            }

            loadingFinished = false;
            Log.d(TAG, "onCreate: " + request.getUrl());
            view.loadUrl(String.valueOf(request.getUrl()));
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap facIcon) {
            loadingFinished = false;
            //SHOW LOADING IF IT ISNT ALREADY VISIBLE
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if(!redirect){
                loadingFinished = true;
            }

            if(loadingFinished && !redirect){
                //HIDE LOADING IT HAS FINISHED
                pbLoadingPayment.setVisibility(View.GONE);
            } else{
                redirect = false;
            }

        }
    }
}