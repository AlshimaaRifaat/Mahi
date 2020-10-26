package com.mahitab.ecommerce.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.adapters.ProductAdapter;
import com.mahitab.ecommerce.managers.DataManager;
import com.mahitab.ecommerce.models.CollectionModel;

import static com.mahitab.ecommerce.utils.CommonUtils.setArDefaultLocale;

public class CollectionProductsActivity extends AppCompatActivity implements ProductAdapter.ProductClickListener {

    private static final String TAG = "CollectionProductsActiv";

    private RecyclerView rvCollectionProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setArDefaultLocale(this);
        setContentView(R.layout.activity_collection_products);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();

        if (getIntent().getExtras() != null) {
            String collectionId=getIntent().getExtras().getString("collectionId");
            CollectionModel collection = DataManager.getInstance().getCollectionByID(collectionId);
            if (collection != null) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(collection.getTitle());
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }

                rvCollectionProducts.setHasFixedSize(true);
                rvCollectionProducts.setLayoutManager(new GridLayoutManager(this, 2));
                ProductAdapter productAdapter = new ProductAdapter(collection.getPreviewProducts());
                rvCollectionProducts.setAdapter(productAdapter);
                productAdapter.setProductClickListener(this);
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

    @Override
    public void onProductClick(String productId) {
        Intent intent = new Intent(getApplicationContext(), ProductDetailsActivity.class);
        intent.putExtra("productId", productId);
        startActivity(intent);
    }

    private void initView() {
        rvCollectionProducts = findViewById(R.id.rvCollectionProducts_CollectionProductsActivity);
    }

}