package com.mahitab.ecommerce.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asksira.loopingviewpager.LoopingViewPager;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.adapters.ProductAdapter;
import com.mahitab.ecommerce.adapters.ProductImageSliderAdapter;
import com.mahitab.ecommerce.managers.DataManager;
import com.mahitab.ecommerce.models.CollectionModel;
import com.mahitab.ecommerce.models.ProductModel;
import com.rd.PageIndicatorView;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

import static com.mahitab.ecommerce.utils.CommonUtils.setArDefaultLocale;

public class ProductDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private LoopingViewPager viewPager;
    private PageIndicatorView indicatorView;

    private TextView tvTitle;
    private TextView tvPrice;
    private TextView tvDescription;
    private ImageView ivDescription;

    private ProductModel product;
    private RecyclerView rvRelatedProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setArDefaultLocale(this);
        setContentView(R.layout.activity_product_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();

        if (getIntent().getExtras() != null) {
            String productId = getIntent().getExtras().getString("productId");
            product = DataManager.getInstance().getProductByID(productId);

            if (product != null) {
                tvTitle.setText(product.getTitle());
                String price = NumberFormat.getInstance(new Locale("ar")).format(product.getVariants().get(0).getPrice()) + getString(R.string.egp);
                tvPrice.setText(price);

                tvDescription.setText(HtmlCompat.fromHtml(product.getDescription(), HtmlCompat.FROM_HTML_MODE_LEGACY));

                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(product.getTitle());
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }

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

                CollectionModel collection = DataManager.getInstance().getCollectionByID(product.getCollectionID());
                if (collection != null) {
                    rvRelatedProducts.setHasFixedSize(true);
                    rvRelatedProducts.setLayoutManager(new GridLayoutManager(this, 3));
                    ProductAdapter productAdapter = new ProductAdapter(collection.getPreviewProducts());
                    rvRelatedProducts.setAdapter(productAdapter);
                }
            }
        }

        ivDescription.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0); // remove activity default transition
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivDescription_ProductDetailsActivity) {
            Intent intent = new Intent(getApplicationContext(), DescriptionActivity.class);
            intent.putExtra("description", product.getDescription());
            startActivity(intent);
        }
    }

    private void initView() {
        viewPager = findViewById(R.id.viewPager);
        indicatorView = findViewById(R.id.pageIndicatorView);
        tvTitle = findViewById(R.id.tvTitle_ProductDetailsActivity);
        tvPrice = findViewById(R.id.tvPrice_ProductDetailsActivity);
        tvDescription = findViewById(R.id.tvDescription_ProductDetailsActivity);
        ivDescription = findViewById(R.id.ivDescription_ProductDetailsActivity);
        rvRelatedProducts = findViewById(R.id.rvRelatedProducts_ProductDetailsActivity);
    }
}