package com.mahitab.ecommerce.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.adapters.BannerAdapter;
import com.mahitab.ecommerce.adapters.ProductAdapter;
import com.mahitab.ecommerce.managers.DataManager;
import com.mahitab.ecommerce.managers.FirebaseManager;
import com.mahitab.ecommerce.models.BannerModel;
import com.mahitab.ecommerce.models.Collection;
import com.mahitab.ecommerce.models.CollectionModel;

import java.nio.charset.StandardCharsets;

import static com.mahitab.ecommerce.utils.CommonUtils.setArDefaultLocale;

public class CollectionProductsActivity extends AppCompatActivity implements ProductAdapter.ProductClickListener, BannerAdapter.BannerClickListener {

    private static final String TAG = "CollectionProductsActiv";

    private RecyclerView rvCollectionBanners;
    private BannerAdapter bannerAdapter;

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
            String collectionId = getIntent().getExtras().getString("collectionId");

            byte[] decodedBytes = android.util.Base64.decode(collectionId, android.util.Base64.DEFAULT);
            String decodeCollectionId = new String(decodedBytes).split("/")[4];

            DisplayMetrics displaymetrics = Resources.getSystem().getDisplayMetrics();

            rvCollectionBanners.setHasFixedSize(true);
            rvCollectionBanners.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            bannerAdapter = new BannerAdapter(displaymetrics.widthPixels);
            rvCollectionBanners.setAdapter(bannerAdapter);
            bannerAdapter.setBannerClickListener(this);

            FirebaseDatabase.getInstance().getReference()
                    .child("/HomePage/Collections").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot topSnapshot : dataSnapshot.child("Top").getChildren()) {
                        Collection collection = topSnapshot.getValue(Collection.class);
                        if (collection != null && collection.getId().equals(decodeCollectionId)) {
                            bannerAdapter.setBannerList(FirebaseManager.getBanners(topSnapshot));
                        }
                    }
                    for (DataSnapshot midSnapshot : dataSnapshot.child("Mid").getChildren()) {
                        Collection collection = midSnapshot.getValue(Collection.class);
                        if (collection != null && collection.getId().equals(decodeCollectionId)) {
                            bannerAdapter.setBannerList(FirebaseManager.getBanners(midSnapshot));
                        }
                    }
                    for (DataSnapshot bottomSnapshot : dataSnapshot.child("Bottom").getChildren()) {
                        Collection collection = bottomSnapshot.getValue(Collection.class);
                        if (collection != null && collection.getId().equals(decodeCollectionId)) {
                            bannerAdapter.setBannerList(FirebaseManager.getBanners(bottomSnapshot));
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "onCancelled: " + error.getMessage());
                }
            });

            CollectionModel collection = DataManager.getInstance().getCollectionByID(collectionId);
            if (collection != null) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(collection.getTitle());
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }

                rvCollectionProducts.setHasFixedSize(true);
                rvCollectionProducts.setLayoutManager(new GridLayoutManager(this, 2));
                ProductAdapter productAdapter = new ProductAdapter(this, collection.getPreviewProducts());
                rvCollectionProducts.setAdapter(productAdapter);
                productAdapter.setProductClickListener(this);
                rvCollectionProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (recyclerView.computeVerticalScrollOffset() == 0)
                            rvCollectionBanners.setVisibility(View.VISIBLE);
                        else rvCollectionBanners.setVisibility(View.GONE);
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

    @Override
    public void onProductClick(String productId) {
        Intent intent = new Intent(getApplicationContext(), ProductDetailsActivity.class);
        intent.putExtra("productId", productId);
        startActivity(intent);
        finish();
    }

    private void initView() {
        rvCollectionBanners = findViewById(R.id.rvCollectionBanners_CollectionProductsActivity);
        rvCollectionProducts = findViewById(R.id.rvCollectionProducts_CollectionProductsActivity);
    }

    @Override
    public void onBannerClick(BannerModel banner) {
        FirebaseManager.incrementBannerNoOfClicks(banner.getReference());
        String type;
        Intent intent;
        if (banner.getType().startsWith("p")) {
            type = "Product";
            String target = "gid://shopify/" + type + "/" + banner.getId();
            String targetId = Base64.encodeToString(target.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
            targetId = targetId.trim(); //remove spaces from end of string
            intent = new Intent(getApplicationContext(), ProductDetailsActivity.class);
            intent.putExtra("productId", targetId);
            startActivity(intent);
            finish();
        } else if (banner.getType().startsWith("c")) {
            type = "Collection";
            String target = "gid://shopify/" + type + "/" + banner.getId();
            String targetId = Base64.encodeToString(target.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
            targetId = targetId.trim(); //remove spaces from end of string
            intent = new Intent(getApplicationContext(), CollectionProductsActivity.class);
            intent.putExtra("collectionId", targetId);
            startActivity(intent);
            finish();
        }
    }
}