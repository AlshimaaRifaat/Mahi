package com.mahitab.ecommerce.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.adapters.ProductAdapter;
import com.mahitab.ecommerce.models.CollectionModel;

import static com.mahitab.ecommerce.utils.CommonUtils.setArDefaultLocale;

public class CollectionProductsActivity extends AppCompatActivity {

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
            CollectionModel collection  = getIntent().getExtras().getParcelable("collection");
            if (collection != null) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(collection.getTitle());
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                    rvCollectionProducts.setHasFixedSize(true);
                    rvCollectionProducts.setLayoutManager(new GridLayoutManager(this,2));
                    ProductAdapter productAdapter = new ProductAdapter(collection.getPreviewProducts());
                    rvCollectionProducts.setAdapter(productAdapter);
                }
            }
        }
    }

    private void initView() {
        rvCollectionProducts=findViewById(R.id.rvCollectionProducts_CollectionProductsActivity);
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
}