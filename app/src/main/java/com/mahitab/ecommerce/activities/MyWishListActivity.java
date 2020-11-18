package com.mahitab.ecommerce.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.adapters.ProductAdapter;
import com.mahitab.ecommerce.managers.DataManager;
import com.mahitab.ecommerce.models.ProductModel;

import java.util.ArrayList;
import java.util.List;

import static com.mahitab.ecommerce.utils.CommonUtils.setArDefaultLocale;

public class MyWishListActivity extends AppCompatActivity implements ProductAdapter.ProductClickListener {

    private RecyclerView rvWishedProducts;
    private ArrayList<ProductModel> wishedProducts;
    private ProductAdapter productAdapter;

    private SharedPreferences defaultPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setArDefaultLocale(this);
        setContentView(R.layout.activity_my_wish_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.my_wishlist));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initView();

        defaultPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        wishedProducts = new ArrayList<>();

        rvWishedProducts.setHasFixedSize(true);
        rvWishedProducts.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new ProductAdapter(wishedProducts);
        rvWishedProducts.setAdapter(productAdapter);
        productAdapter.setProductClickListener(this);

    }

    private void initView() {
        rvWishedProducts = findViewById(R.id.rvWishedProducts_MyWishListActivity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0); // remove activity default transition

        List<String> wishListItems;
        if (defaultPreferences.getString("wishListProducts", null) == null)
            wishListItems = new ArrayList<>();
        else
            wishListItems = new Gson().fromJson(defaultPreferences.getString("wishListProducts", null), new TypeToken<List<String>>() {
            }.getType());

        getWishedProducts(wishListItems);
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

    private void getWishedProducts(List<String> productsIds) {
        if (productsIds.size() > 0) {
            for (String productId : productsIds) {
                ProductModel product = DataManager.getInstance().getProductByID(productId);
                wishedProducts.add(product);
            }
        } else wishedProducts.clear();
        productAdapter.notifyDataSetChanged();
    }
}