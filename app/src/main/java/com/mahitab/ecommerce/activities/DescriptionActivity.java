package com.mahitab.ecommerce.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mahitab.ecommerce.R;

import static com.mahitab.ecommerce.utils.CommonUtils.setArDefaultLocale;

public class DescriptionActivity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setArDefaultLocale(this);
        setContentView(R.layout.activity_description);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        WebView wvDescription=findViewById(R.id.wvDescription_DescriptionActivity);
        if (getIntent().getExtras()!=null){
            String description=getIntent().getExtras().getString("description");
            wvDescription.getSettings().setJavaScriptEnabled(true);
            wvDescription.loadData("<html dir=\"rtl\" lang=\"\"><body>" + description + "</body></html>", "text/html; charset=utf-8", "UTF-8");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setArDefaultLocale(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.description));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        overridePendingTransition(0, 0); // remove activity default transition
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}