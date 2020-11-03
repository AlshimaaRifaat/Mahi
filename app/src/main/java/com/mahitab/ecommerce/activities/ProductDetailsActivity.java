package com.mahitab.ecommerce.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asksira.loopingviewpager.LoopingViewPager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.adapters.ProductAdapter;
import com.mahitab.ecommerce.adapters.ProductImageSliderAdapter;
import com.mahitab.ecommerce.adapters.ReviewAdapter;
import com.mahitab.ecommerce.managers.DataManager;
import com.mahitab.ecommerce.models.CartItemQuantity;
import com.mahitab.ecommerce.models.CollectionModel;
import com.mahitab.ecommerce.models.ProductModel;
import com.mahitab.ecommerce.models.ProductReviewModel;
import com.rd.PageIndicatorView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import static com.mahitab.ecommerce.utils.CommonUtils.setArDefaultLocale;

public class ProductDetailsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "ProductDetailsActivity";
    // badge text view
    TextView badgeCounter;
    // change the number to see badge in action
    int cartProductsCount = 0;

    private LoopingViewPager viewPager;
    private PageIndicatorView indicatorView;

    private TextView tvTitle;
    private TextView tvPrice;
    private TextView tvDescription;
    private ImageView ivDescription;

    private ImageView ivWishProduct;
    private List<String> wishListProducts;
    private boolean isWishedBefore;

    private List<CartItemQuantity> cartProducts;
    private LinearLayout llCartQuantityControl;
    private Button btnAddToCart;

    private TextView tvQuantityType;
    private ImageView ivIncreaseQuantity;
    private TextView tvCartQuantity;
    private ImageView ivDecreaseQuantity;
    private ImageView ivBuyByPhone;
    private Button btnBuy;
    private CardView cvAddReview;

    private MenuItem cartMenuItem;

    private RatingBar rbAverageRating;
    private CardView cvReviews;
    private RecyclerView rvProductReviews;
    private final List<ProductReviewModel> productReviews = new ArrayList<>();
    private ReviewAdapter reviewAdapter;

    private DatabaseReference productReviewsReference;

    private String currentProductId;

    private ProductModel product;
    private RecyclerView rvRelatedProducts;
    private SharedPreferences defaultPreferences;
    private TextView tvSKU;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setArDefaultLocale(this);
        setContentView(R.layout.activity_product_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();

        defaultPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        productReviewsReference = FirebaseDatabase.getInstance().getReference("ProductReviews");

        if (defaultPreferences.getString("cartProducts", null) == null)
            cartProducts = new ArrayList<>();
        else
            cartProducts = new Gson().fromJson(defaultPreferences.getString("cartProducts", null), new TypeToken<List<CartItemQuantity>>() {
            }.getType());

        if (defaultPreferences.getString("wishListProducts", null) == null)
            wishListProducts = new ArrayList<>();
        else
            wishListProducts = new Gson().fromJson(defaultPreferences.getString("wishListProducts", null), new TypeToken<List<String>>() {
            }.getType());

        cartProductsCount = cartProducts.size();

        if (getIntent().getExtras() != null) {
            currentProductId = getIntent().getExtras().getString("productId");
            boolean isAddedBefore = cartProducts.stream()
                    .anyMatch(cartItemQuantity -> cartItemQuantity.getId().toString().equals(currentProductId));

            displayQuantityControls(isAddedBefore);

            isWishedBefore = wishListProducts.stream()
                    .anyMatch(wishedProduct -> wishedProduct.equals(currentProductId));

            displayIsWishedProduct(isWishedBefore);

            product = DataManager.getInstance().getProductByID(currentProductId);

            if (product != null) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(product.getTitle());
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }

                tvTitle.setText(product.getTitle());
                tvSKU.setText(product.getSKU());
                String price = NumberFormat.getInstance(new Locale("ar")).format(product.getVariants().get(0).getPrice()) + getString(R.string.egp);
                tvPrice.setText(price);

                tvDescription.setText(HtmlCompat.fromHtml(product.getDescription(), HtmlCompat.FROM_HTML_MODE_LEGACY));

                rvProductReviews.setHasFixedSize(true);
                rvProductReviews.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                reviewAdapter = new ReviewAdapter(productReviews);
                rvProductReviews.setAdapter(reviewAdapter);


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
                ProductAdapter productAdapter = new ProductAdapter(this,collection.getPreviewProducts());
                rvRelatedProducts.setAdapter(productAdapter);
            }
        }

        cvAddReview.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ReviewActivity.class);
            intent.putExtra("productId", product.getID().toString());
            startActivity(intent);
        });

        ivDescription.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), DescriptionActivity.class);
            intent.putExtra("description", product.getDescription());
            startActivity(intent);
        });

        btnAddToCart.setOnClickListener(v -> {
            cartProducts.add(new CartItemQuantity(1, product.getID().toString(), product.getVariants().get(0).getPrice().doubleValue(),product.getVariants().get(0).getID()));
            Log.e("Tango", product.getID().toString());
            defaultPreferences.edit().putString("cartProducts", new Gson().toJson(cartProducts)).apply();
            tvCartQuantity.setText(String.valueOf(1));
            cartProductsCount = cartProducts.size();
            updateCartBadge(cartProductsCount); //update badge counter
            displayQuantityControls(true);
        });

        ivBuyByPhone.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:+20 111 111 4512"));
            startActivity(intent);
        });

        ivIncreaseQuantity.setOnClickListener(v -> {
            int currentCartProductIndex = IntStream.range(0, cartProducts.size())
                    .filter(i -> cartProducts.get(i).getProductID().toString().equals(currentProductId))
                    .findFirst().orElse(-1);
            cartProducts.get(currentCartProductIndex).plusQuantity();
            updateQuantitySharedPreferencesUI(currentCartProductIndex);
        });

        ivDecreaseQuantity.setOnClickListener(v -> {
            int currentCartProductIndex = IntStream.range(0, cartProducts.size())
                    .filter(i -> cartProducts.get(i).getProductID().toString().equals(currentProductId))
                    .findFirst().orElse(-1);
            if (cartProducts.get(currentCartProductIndex).getQuantity() > 1) {
                cartProducts.get(currentCartProductIndex).minQuantity();
                updateQuantitySharedPreferencesUI(currentCartProductIndex);
            } else {
                cartProducts.remove(currentCartProductIndex);
                defaultPreferences.edit().putString("cartProducts", new Gson().toJson(cartProducts)).apply(); //update shared pref list after remove
                llCartQuantityControl.setVisibility(View.GONE);
                btnAddToCart.setVisibility(View.VISIBLE);
                cartProductsCount = cartProducts.size();
                updateCartBadge(cartProductsCount);
            }
        });

        btnBuy.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), CartActivity.class)));

        ivWishProduct.setOnClickListener(v -> {
            if (isWishedBefore) {
                isWishedBefore = false;
                wishListProducts.remove(currentProductId);
            } else {
                isWishedBefore = true;
                wishListProducts.add(currentProductId);
            }
            defaultPreferences.edit().putString("wishListProducts", new Gson().toJson(wishListProducts)).apply();
            displayIsWishedProduct(isWishedBefore);
        });
    }
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0); // remove activity default transition

        if (llCartQuantityControl.getVisibility() == View.VISIBLE) {
            //start update quantity in ui in changed in cart fragment and back to details
            cartProducts = new Gson().fromJson(defaultPreferences.getString("cartProducts", null), new TypeToken<List<CartItemQuantity>>() {
            }.getType());
            int currentCartProductIndex = IntStream.range(0, cartProducts.size())
                    .filter(i -> cartProducts.get(i).getProductID().toString().equals(currentProductId))
                    .findFirst().orElse(-1);
            if (currentCartProductIndex != -1)
                updateQuantitySharedPreferencesUI(currentCartProductIndex);
            else
                displayQuantityControls(false);
            //end update quantity in ui
        }

        if (product != null)
            getProductReviews(product.getID().toString());

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
        cartMenuItem = menu.findItem(R.id.action_open_cart);
        updateCartBadge(cartProductsCount);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        else if (item.getItemId() == R.id.action_open_cart)
            startActivity(new Intent(getApplicationContext(), CartActivity.class));
        return super.onOptionsItemSelected(item);
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
        btnBuy = findViewById(R.id.btnBuy_ProductDetailsActivity);
        cvReviews = findViewById(R.id.cvReviews_ProductDetailsActivity);
        rbAverageRating = findViewById(R.id.rbAverageRating_ProductDetailsActivity);
        rvProductReviews = findViewById(R.id.rvProductReviews_ProductDetailsActivity);
        cvAddReview = findViewById(R.id.cvAddReview_ProductDetailsActivity);
        ivWishProduct = findViewById(R.id.ivWishProduct_ProductDetailsActivity);
        tvSKU=findViewById(R.id.tvSKU_ProductDetailsActivity);
    }

    private void displayQuantityControls(boolean isAddedToCart) {
        if (isAddedToCart) {
            int currentCartProductIndex = IntStream.range(0, cartProducts.size())
                    .filter(i -> cartProducts.get(i).getProductID().toString().equals(currentProductId))
                    .findFirst().orElse(-1);
            tvCartQuantity.setText(String.valueOf(cartProducts.get(currentCartProductIndex).getQuantity())); //update cart quantity in ui
            llCartQuantityControl.setVisibility(View.VISIBLE);
            btnAddToCart.setVisibility(View.GONE);
        } else {
            llCartQuantityControl.setVisibility(View.GONE);
            btnAddToCart.setVisibility(View.VISIBLE);
        }
    }

    private void displayIsWishedProduct(boolean wished) {
        if (wished)
            ivWishProduct.setImageResource(R.drawable.ic_favorite_yellow_24dp);
        else ivWishProduct.setImageResource(R.drawable.ic_favorite_border_yellow_24dp);
    }

    private void updateQuantitySharedPreferencesUI(int cartProductIndex) {
        defaultPreferences.edit().putString("cartProducts", new Gson().toJson(cartProducts)).apply(); // update list in shared pref
        tvCartQuantity.setText(String.valueOf(cartProducts.get(cartProductIndex).getQuantity())); //update cart quantity in ui
    }

    private void updateCartBadge(int cartProductsCount) {
        if (cartProductsCount == 0)
            cartMenuItem.setActionView(null);
        else {
            // if notification than set the badge icon layout
            cartMenuItem.setActionView(R.layout.cart_badge_layout);
            // get the text view of the action view for the nav item
            badgeCounter = cartMenuItem.getActionView().findViewById(R.id.badge_counter);
            // set the pending notifications value
            badgeCounter.setText(String.valueOf(cartProductsCount));
            cartMenuItem.getActionView().setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), CartActivity.class))); // handel custom view click
        }
    }

    private void getProductReviews(String productId) {
        productReviewsReference.child(productId)
                .orderByChild("accepted")
                .equalTo(true)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            productReviews.clear();
                            cvReviews.setVisibility(View.VISIBLE);
                            rvProductReviews.setVisibility(View.VISIBLE);
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                ProductReviewModel productReview = snapshot.getValue(ProductReviewModel.class);
                                productReviews.add(productReview);
                                reviewAdapter.notifyDataSetChanged();
                            }
                            rbAverageRating.setRating(calculateAverageRatings(productReviews));
                        } else {
                            cvReviews.setVisibility(View.GONE);
                            rvProductReviews.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "onCancelled: " + error.getMessage());
                    }
                });
    }

    private int calculateAverageRatings(List<ProductReviewModel> reviews) {
        int customer5Stars = 0;
        int customer4Stars = 0;
        int customer3Stars = 0;
        int customer2Stars = 0;
        int customer1Stars = 0;
        for (ProductReviewModel review : reviews) {
            if (review.getRating() == 5)
                customer5Stars += 1;
            else if (review.getRating() == 4)
                customer4Stars += 1;
            else if (review.getRating() == 3)
                customer3Stars += 1;
            else if (review.getRating() == 2)
                customer2Stars += 1;
            else if (review.getRating() == 1)
                customer1Stars += 1;
        }
        return (customer1Stars + (2 * customer2Stars) + (3 * customer3Stars)+ (4 * customer4Stars) + (5 * customer5Stars)) / (customer1Stars + customer2Stars + customer3Stars + customer4Stars + customer5Stars);
    }
}