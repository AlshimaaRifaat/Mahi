package com.mahitab.ecommerce.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.managers.GraphClientManager;
import com.mahitab.ecommerce.managers.interfaces.BaseCallback;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.ID;

import java.util.Timer;
import java.util.TimerTask;

public class PaymentWebViewActivity extends AppCompatActivity {
    private static final String TAG = "PaymentWebViewActivity";
    String webUrl;
    WebView mywebview;
    ID checkoutId;
    SharedPreferences sharedPreferences;
    String accessToken;
    boolean running;
    final int REQUEST_INTERVAL = 2000;
    Timer requestIntervalTimer;
    int previousOrderNumber = 0;
    boolean getPreviousOrderNumberOnce;
    String sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_web_view);

        mywebview = findViewById(R.id.webView);

        Intent intent = getIntent();
        webUrl = intent.getExtras().getString("web_url");
        checkoutId = (ID) getIntent().getSerializableExtra("checkout_id");
        mywebview.setWebViewClient(new MyBrowser());
        mywebview.getSettings().setLoadsImagesAutomatically(true);
        mywebview.getSettings().setJavaScriptEnabled(true);
        mywebview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mywebview.loadUrl(webUrl);
    }

    private void getSavedAccessToken() {
        sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        accessToken = sharedPreferences.getString("token", null);
        Log.d(TAG, "getSavedAccessToken: " + accessToken);
        queryMyOrders(accessToken);
    }

    private void queryMyOrders(String accessToken) {
        Storefront.QueryRootQuery query = Storefront.query(root -> root
                .customer(accessToken, customer -> customer
                        .orders(arg -> arg.first(100), connection -> connection
                                .edges(edge -> edge
                                        .node(node -> node
                                                .orderNumber()

                                        )
                                )
                        )
                )
        );
        getMyOrdersList(query, new BaseCallback() {
            @Override
            public void onResponse(int status) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (status == 200) {
                        }
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
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(PaymentWebViewActivity.this, "Order Completed", Toast.LENGTH_LONG).show();
                                    timerStop();
                                    //TODO Handle after order completed process
                                    //TODO You could simply finish this activty and intent to Home with an extra to empty cart
                                   Intent navigate = new Intent(PaymentWebViewActivity.this, HomeActivity.class);
                                    sign="mark";
                                    navigate.putExtra("from_payment",sign );
                                    startActivity(navigate);
                                }
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
                Log.d(TAG, "onFailure: " + error.getMessage().toString());

            }
        });
    }

    public void onPause() {
        super.onPause();
        if (running) {
            timerStop();
        }
    }

    public void onResume() {
        super.onResume();
        if (!running) {
            timerStart();
        }

    }

    public void timerStart() {
        running = true;
        if (requestIntervalTimer == null) {
            requestIntervalTimer = new Timer();
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
            getSavedAccessToken();
        }
    };


    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            Log.d(TAG, "onCreate: " + url);
            view.loadUrl(url);

            return true;
        }
    }
}