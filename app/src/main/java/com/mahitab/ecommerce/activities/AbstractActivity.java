package com.mahitab.ecommerce.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AbstractActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void showOnUiThread(String msg) {
        super.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AbstractActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onPause();
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(manager != null && manager.isAcceptingText()) {
            View view = getCurrentFocus();
            if(view != null) {
                IBinder binder = view.getWindowToken();
                manager.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_NOT_ALWAYS);
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(manager != null) {
            View view = getCurrentFocus();
            if(view != null) {
                IBinder binder = view.getWindowToken();
                manager.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(manager != null) {
            View view = getCurrentFocus();
            if(view != null) {
                IBinder binder = view.getWindowToken();
                manager.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}

