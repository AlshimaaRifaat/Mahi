package com.mahitab.ecommerce.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asksira.loopingviewpager.LoopingViewPager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.adapters.ProductAdapter;
import com.mahitab.ecommerce.adapters.ProductImageSliderAdapter;
import com.mahitab.ecommerce.managers.DataManager;
import com.mahitab.ecommerce.models.CartItemQuantity;
import com.mahitab.ecommerce.models.CollectionModel;
import com.mahitab.ecommerce.models.ProductModel;
import com.rd.PageIndicatorView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import static com.mahitab.ecommerce.utils.CommonUtils.setArDefaultLocale;

public class ProductDetailsActivity extends AppCompatActivity implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private LoopingViewPager viewPager;
    private PageIndicatorView indicatorView;

    private TextView tvTitle;
    private TextView tvPrice;
    private TextView tvDescription;
    private ImageView ivDescription;

    private List<CartItemQuantity> cartProducts;
    private LinearLayout llCartQuantityControl;
    private Button btnAddToCart;

    private TextView tvQuantityType;
    private ImageView ivIncreaseQuantity;
    private TextView tvCartQuantity;
    private ImageView ivDecreaseQuantity;
    private ImageView ivBuyByPhone;
    private LinearLayout llBuy;

    private String currentProductId;

    private ProductModel product;
    private RecyclerView rvRelatedProducts;
    private SharedPreferences defaultPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setArDefaultLocale(this);
        setContentView(R.layout.activity_product_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();

        defaultPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        if (defaultPreferences.getString("cartProducts", null) == null)
            cartProducts = new ArrayList<>();
        else
            cartProducts = new Gson().fromJson(defaultPreferences.getString("cartProducts", null), new TypeToken<List<CartItemQuantity>>() {
            }.getType());

        if (getIntent().getExtras() != null) {
            currentProductId = getIntent().getExtras().getString("productId");
            boolean isAddedBefore = cartProducts.stream()
                    .anyMatch(cartItemQuantity -> cartItemQuantity.getProductID().equals(currentProductId));

            displayQuantityControls(isAddedBefore);

            product = DataManager.getInstance().getProductByID(currentProductId);

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
        btnAddToCart.setOnClickListener(this);
        ivBuyByPhone.setOnClickListener(this);
        ivIncreaseQuantity.setOnClickListener(this);
        ivDecreaseQuantity.setOnClickListener(this);
        llBuy.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0); // remove activity default transition
        defaultPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        defaultPreferences.unregisterOnSharedPreferenceChangeListener(this);
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
        } else if (v.getId() == R.id.btnAddToCart_ProductDetailsActivity) {
            cartProducts.add(new CartItemQuantity(1, product.getID().toString()));
            defaultPreferences.edit().putString("cartProducts", new Gson().toJson(cartProducts)).apply();
            displayQuantityControls(true);
            tvCartQuantity.setText(String.valueOf(1));
        } else if (v.getId() == R.id.ivBuyByPhone_ProductDetailsActivity) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:+20 111 111 4512"));
            startActivity(intent);
        } else if (v.getId() == R.id.ivIncreaseQuantity_ProductDetailsActivity) {
            int currentCartProductIndex = IntStream.range(0, cartProducts.size())
                    .filter(i -> cartProducts.get(i).getProductID().equals(currentProductId))
                    .findFirst().orElseGet(null);
            cartProducts.get(currentCartProductIndex).plusQuantity();

            updateQuantitySharedPreferencesUI(currentCartProductIndex);

        } else if (v.getId() == R.id.ivDecreaseQuantity_ProductDetailsActivity) {
            int currentCartProductIndex = IntStream.range(0, cartProducts.size())
                    .filter(i -> cartProducts.get(i).getProductID().equals(currentProductId))
                    .findFirst().orElseGet(null);
            if (cartProducts.get(currentCartProductIndex).getQuantity() > 1) {
                cartProducts.get(currentCartProductIndex).minQuantity();
                updateQuantitySharedPreferencesUI(currentCartProductIndex);
            } else {
                cartProducts.remove(currentCartProductIndex);
                defaultPreferences.edit().putString("cartProducts", new Gson().toJson(cartProducts)).apply(); //update shared pref list after remove
                llCartQuantityControl.setVisibility(View.GONE);
                btnAddToCart.setVisibility(View.VISIBLE);
            }
        } else if (v.getId() == R.id.llBuy_ProductDetailsActivity)
            startActivity(new Intent(getApplicationContext(), CartActivity.class));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (sharedPreferences.contains("cartProducts")) {
            defaultPreferences.edit().putString("cartProducts", new Gson().toJson(cartProducts)).apply();
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
        llCartQuantityControl = findViewById(R.id.llCartQuantityControl_ProductDetailsActivity);
        btnAddToCart = findViewById(R.id.btnAddToCart_ProductDetailsActivity);
        ivBuyByPhone = findViewById(R.id.ivBuyByPhone_ProductDetailsActivity);
        tvQuantityType = findViewById(R.id.tvQuantityType_ProductDetailsActivity);
        tvCartQuantity = findViewById(R.id.tvCartQuantity_ProductDetailsActivity);
        ivIncreaseQuantity = findViewById(R.id.ivIncreaseQuantity_ProductDetailsActivity);
        ivDecreaseQuantity = findViewById(R.id.ivDecreaseQuantity_ProductDetailsActivity);
        llBuy = findViewById(R.id.llBuy_ProductDetailsActivity);
    }

    private void displayQuantityControls(boolean isAddedToCart) {
        if (isAddedToCart) {
            int currentCartProductIndex = IntStream.range(0, cartProducts.size())
                    .filter(i -> cartProducts.get(i).getProductID().equals(currentProductId))
                    .findFirst().orElseGet(null);
            tvCartQuantity.setText(String.valueOf(cartProducts.get(currentCartProductIndex).getQuantity())); //update cart quantity in ui
            llCartQuantityControl.setVisibility(View.VISIBLE);
            btnAddToCart.setVisibility(View.GONE);
        } else {
            llCartQuantityControl.setVisibility(View.GONE);
            btnAddToCart.setVisibility(View.VISIBLE);
        }
    }

    private void updateQuantitySharedPreferencesUI(int cartProductIndex) {
        defaultPreferences.edit().putString("cartProducts", new Gson().toJson(cartProducts)).apply(); // update list in shared pref
        tvCartQuantity.setText(String.valueOf(cartProducts.get(cartProductIndex).getQuantity())); //update cart quantity in ui
    }
}