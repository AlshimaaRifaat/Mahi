package com.mahitab.ecommerce.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

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
import com.mahitab.ecommerce.managers.interfaces.BaseCallback;
import com.mahitab.ecommerce.models.BannerModel;

import com.mahitab.ecommerce.models.Collection;
import com.mahitab.ecommerce.models.CollectionModel;
import com.mahitab.ecommerce.models.ProductModel;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static com.mahitab.ecommerce.utils.CommonUtils.setArDefaultLocale;

public class CollectionProductsActivity extends AppCompatActivity implements ProductAdapter.ProductClickListener, BannerAdapter.BannerClickListener {

    private static final String TAG = "CollectionProductsActiv";

    private RecyclerView rvCollectionBanners;
    private BannerAdapter bannerAdapter;

    private ProgressBar pbLoadingCollectionProducts;
    private RecyclerView rvCollectionProducts;
    private ArrayList<ProductModel> collectionProducts;
    private ProductAdapter productAdapter;
    private Collection collection;

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
            Log.d(TAG, "collectionId: " + decodeCollectionId);
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
                        collection = topSnapshot.getValue(Collection.class);
                        if (collection != null && collection.getId().equals(decodeCollectionId)) {
                            rvCollectionBanners.setVisibility(View.VISIBLE);
                            bannerAdapter.setBannerList(FirebaseManager.getBanners(topSnapshot));
                        }
                    }
                    for (DataSnapshot midSnapshot : dataSnapshot.child("Mid").getChildren()) {
                        collection = midSnapshot.getValue(Collection.class);
                        if (collection != null && collection.getId().equals(decodeCollectionId)) {
                            rvCollectionBanners.setVisibility(View.VISIBLE);
                            bannerAdapter.setBannerList(FirebaseManager.getBanners(midSnapshot));
                        }
                    }
                    for (DataSnapshot bottomSnapshot : dataSnapshot.child("Bottom").getChildren()) {
                        collection = bottomSnapshot.getValue(Collection.class);
                        if (collection != null && collection.getId().equals(decodeCollectionId)) {
                            rvCollectionBanners.setVisibility(View.VISIBLE);
                            bannerAdapter.setBannerList(FirebaseManager.getBanners(bottomSnapshot));
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "onCancelled: " + error.getMessage());
                }
            });

            CollectionModel collectionModel = DataManager.getInstance().getCollectionByID(collectionId);
            collectionProducts = collectionModel.getPreviewProducts();
            Log.e(TAG, "run: " + collectionProducts.size());
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(collectionModel.getTitle());
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            if (collectionProducts.size() == 4) {
                getAllProducts(collectionId);
            } else pbLoadingCollectionProducts.setVisibility(View.GONE);

            rvCollectionProducts.setHasFixedSize(true);
            rvCollectionProducts.setLayoutManager(new GridLayoutManager(CollectionProductsActivity.this, 2));
            collectionProducts = collectionModel.getPreviewProducts();
            productAdapter = new ProductAdapter(CollectionProductsActivity.this, collectionProducts);
            rvCollectionProducts.setAdapter(productAdapter);
            productAdapter.setProductClickListener(CollectionProductsActivity.this);
            rvCollectionProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (collection.getBanners() != null && collection.getBanners().size() > 0) {
                        if (recyclerView.computeVerticalScrollOffset() == 0)
                            rvCollectionBanners.setVisibility(View.VISIBLE);
                        else rvCollectionBanners.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setArDefaultLocale(this);
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
        pbLoadingCollectionProducts = findViewById(R.id.pbLoadingCollectionProducts_CollectionProductsActivity);
    }

    @Override
    public void onBannerClick(BannerModel banner) {
        FirebaseManager.incrementBannerNoOfClicks(banner.getReference());
        if (!banner.getId().isEmpty()) {
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

    private void getAllProducts(String collectionId) {
        DataManager.getInstance().collectionsAllProducts(new BaseCallback() {
            @Override
            public void onResponse(int status) {
                if (status == 200) {
                    runOnUiThread(() -> {
                        pbLoadingCollectionProducts.setVisibility(View.GONE);
                        collectionProducts.clear();
                        collectionProducts = DataManager.getInstance().getCollectionByID(collectionId).getPreviewProducts();
                        productAdapter.setProductList(collectionProducts);
                    });
                }
            }

            @Override
            public void onFailure(String message) {
                getAllProducts(collectionId);
                Log.e(TAG, "onFailure: " + message);
            }
        });
    }
}