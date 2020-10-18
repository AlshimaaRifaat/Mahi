package com.mahitab.ecommerce.activities;

import android.os.Bundle;

import com.asksira.loopingviewpager.LoopingViewPager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.adapters.ProductImageSliderAdapter;
import com.mahitab.ecommerce.managers.DataManager;
import com.mahitab.ecommerce.models.ProductModel;
import com.rd.PageIndicatorView;

import java.util.Arrays;

import static com.mahitab.ecommerce.utils.CommonUtils.setArDefaultLocale;

public class ProductDetailsActivity extends AppCompatActivity {
    private LoopingViewPager viewPager;
    private PageIndicatorView indicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setArDefaultLocale(this);
        setContentView(R.layout.activity_product_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();

        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_product_details));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().getExtras() != null) {
            ProductModel product  = getIntent().getExtras().getParcelable("product");
            if (product != null){
                if (product.getImages() != null) {
                    ProductImageSliderAdapter sliderAdapter = new ProductImageSliderAdapter(this, Arrays.asList(product.getImages()), true);
                    viewPager.setAdapter(sliderAdapter);
                    //Tell the IndicatorView that how many indicators should it display:
                    indicatorView.setCount(viewPager.getIndicatorCount());
                }

                //Set IndicatorPageChangeListener on LoopingViewPager.
                //When the methods are called, update the Indicator accordingly.
                viewPager.setIndicatorPageChangeListener(new LoopingViewPager.IndicatorPageChangeListener() {
                    @Override
                    public void onIndicatorProgress(int selectingPosition, float progress) {
                    }

                    @Override
                    public void onIndicatorPageChange(int newIndicatorPosition) {
                        indicatorView.setSelection(newIndicatorPosition);
                    }
                });
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0); // remove activity default transition
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        viewPager = findViewById(R.id.viewPager);
        indicatorView = findViewById(R.id.pageIndicatorView);
    }
}