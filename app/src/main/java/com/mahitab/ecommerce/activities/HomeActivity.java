package com.mahitab.ecommerce.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.fragments.AccountFragment;
import com.mahitab.ecommerce.fragments.CartFragment;
import com.mahitab.ecommerce.fragments.CategoriesFragment;
import com.mahitab.ecommerce.fragments.HomeFragment;
import com.mahitab.ecommerce.managers.DataManager;
import com.mahitab.ecommerce.managers.interfaces.BaseCallback;
import com.mahitab.ecommerce.models.BannerModel;
import com.mahitab.ecommerce.models.CartItemQuantity;
import com.mahitab.ecommerce.models.Collection;
import com.mahitab.ecommerce.models.CollectionModel;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.mahitab.ecommerce.utils.CommonUtils.setArDefaultLocale;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";
    private boolean doubleBackToExitPressedOnce = false;
    private BottomNavigationView bnvHomeNavigation;
    private SharedPreferences defaultPreferences;

    private LinearLayout llHome;
    private LinearLayout llSplash;

    private List<CollectionModel> topCollectionModels;
    private List<CollectionModel> midCollectionModels;
    private List<CollectionModel> bottomCollectionModels;

    private List<BannerModel> sliderBanners;

    private List<BannerModel> topBanners;
    private List<BannerModel> midBanners;
    private List<BannerModel> bottomBanners;

    private static HomePageLoadedListener homePageLoadedListener;

    private List<CartItemQuantity> cartProducts = null;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setArDefaultLocale(this);
        setContentView(R.layout.activity_home);

        initView();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        defaultPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        sliderBanners = new ArrayList<>();
        topCollectionModels = new ArrayList<>();
        midCollectionModels = new ArrayList<>();
        bottomCollectionModels = new ArrayList<>();
        topBanners = new ArrayList<>();
        midBanners = new ArrayList<>();
        bottomBanners = new ArrayList<>();

        cartProducts = new ArrayList<>();

        //loading the home default fragment
        changeFragment(new HomeFragment(), HomeFragment.class.getName(),
                bnvHomeNavigation.getMenu().getItem(0));

        bnvHomeNavigation.setOnNavigationItemSelectedListener(this);

        loadHomePage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setArDefaultLocale(this);
        if (defaultPreferences.getString("cartProducts", null) == null)
            cartProducts = new ArrayList<>();
        else
            cartProducts = new Gson().fromJson(defaultPreferences.getString("cartProducts", null), new TypeToken<List<CartItemQuantity>>() {
            }.getType());

        // change the number to see cartBadge in action
        int cartProductsCount = 0;
        if (cartProducts != null) {
            cartProductsCount = cartProducts.size();
        }

        BadgeDrawable cartBadge = bnvHomeNavigation.getOrCreateBadge(R.id.cart_navigation);
        if (cartProductsCount >= 1) {
            cartBadge.setVisible(true);
            cartBadge.setNumber(cartProductsCount);
            cartBadge.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        } else cartBadge.setVisible(false);

        overridePendingTransition(0, 0);
    }


    @Override
    protected void onPause() {
        super.onPause();
        Intent intent = getIntent();
        String sign = intent.getStringExtra("from_payment");
        Log.d(TAG, "getSignAfterPayment1: " + sign);
        cartProducts = new Gson().fromJson(defaultPreferences.getString("cartProducts", null), new TypeToken<List<CartItemQuantity>>() {
        }.getType());
        Log.d(TAG, "onPause: " + cartProducts);

        if (sign != null) {
            cartProducts.clear();
            defaultPreferences.edit().putString("cartProducts", new Gson().toJson(cartProducts)).apply();
        }
    }

    @Override
    public void onBackPressed() {
        if (bnvHomeNavigation.getSelectedItemId() != R.id.home_navigation)
            changeFragment(new HomeFragment(), HomeFragment.class
                    .getName(), bnvHomeNavigation.getMenu().getItem(0));
        else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
            }
            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.back_again), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.home_navigation)
            changeFragment(new HomeFragment(), HomeFragment.class.getName(), item);
        else if (item.getItemId() == R.id.categories_navigation)
            changeFragment(new CategoriesFragment(), CategoriesFragment.class.getName(), item);
        else if (item.getItemId() == R.id.cart_navigation)
            changeFragment(new CartFragment(), CartFragment.class.getName(), item);
        else if (item.getItemId() == R.id.account_navigation)
            changeFragment(new AccountFragment(), AccountFragment.class.getName(), item);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initView() {
        llHome = findViewById(R.id.llHome_HomeActivity);
        llSplash = findViewById(R.id.llSplash_HomeActivity);
        bnvHomeNavigation = findViewById(R.id.bnvHomeNavigation_HomeActivity);
    }

    private void changeFragment(Fragment fragment, String tagFragmentName, MenuItem item) {
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        Fragment currentFragment = mFragmentManager.getPrimaryNavigationFragment();
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }

        Fragment fragmentTemp = mFragmentManager.findFragmentByTag(tagFragmentName);
        if (fragmentTemp == null) {
            fragmentTemp = fragment;
            fragmentTransaction.add(R.id.llContainer_HomeActivity, fragmentTemp, tagFragmentName);
        } else {
            fragmentTransaction.show(fragmentTemp);
        }

        item.setChecked(true);

        fragmentTransaction.setPrimaryNavigationFragment(fragmentTemp);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commitNowAllowingStateLoss();
    }

    public BottomNavigationView getBnvHomeNavigation() {
        return bnvHomeNavigation;
    }

    private void loadHomePage() {
        List<Collection> topCollections = new ArrayList<>();
        List<Collection> midCollections = new ArrayList<>();
        List<Collection> bottomCollections = new ArrayList<>();
        databaseReference.child("HomePage").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot topSnapshot : dataSnapshot.child("Collections/Top").getChildren()) {
                    if (topSnapshot.child("id").getValue() != null &&
                            topSnapshot.child("id").getValue() instanceof String &&
                            topSnapshot.child("image").getValue() instanceof String) {
                        Collection topCollection = topSnapshot.getValue(Collection.class);
                        if (topCollection != null) {
                            String target = "gid://shopify/Collection/" + topCollection.getId();
                            String targetId = Base64.encodeToString(target.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
                            targetId = targetId.trim(); //remove spaces from end of string
                            topCollection.setEncodedId(targetId);
                            topCollections.add(topCollection);
                            Log.e(TAG, "onDataChange: top "+topCollection.getImage());
                        }
                    }
                }

                for (DataSnapshot midSnapshot : dataSnapshot.child("Collections/Mid").getChildren()) {
                    if (midSnapshot.child("id").getValue() != null &&
                            midSnapshot.child("id").getValue() instanceof String &&
                            midSnapshot.child("image").getValue() instanceof String) {
                        Collection midCollection = midSnapshot.getValue(Collection.class);
                        if (midCollection != null) {
                            String target = "gid://shopify/Collection/" + midCollection.getId();
                            String targetId = Base64.encodeToString(target.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
                            targetId = targetId.trim(); //remove spaces from end of string
                            midCollection.setEncodedId(targetId);
                            midCollections.add(midCollection);
                        }
                    }
                }

                for (DataSnapshot bottomSnapshot : dataSnapshot.child("Collections/Bottom").getChildren()) {
                    if (bottomSnapshot.child("id").getValue() != null &&
                            bottomSnapshot.child("id").getValue() instanceof String &&
                            bottomSnapshot.child("image").getValue() instanceof String) {
                        Collection bottomCollection = bottomSnapshot.getValue(Collection.class);
                        if (bottomCollection != null) {
                            String target = "gid://shopify/Collection/" + bottomCollection.getId();
                            String targetId = Base64.encodeToString(target.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
                            targetId = targetId.trim(); //remove spaces from end of string
                            bottomCollection.setEncodedId(targetId);
                            bottomCollections.add(bottomCollection);
                        }
                    }
                }

                for (DataSnapshot sliderSnapshot : dataSnapshot.child("Slider").getChildren()) {
                    if (sliderSnapshot.child("id").getValue() instanceof String
                            && sliderSnapshot.child("type").getValue() instanceof String
                            && sliderSnapshot.child("image").getValue() instanceof String
                            && sliderSnapshot.child("numberOfClicks").getValue() instanceof Long) { // validate data types before add in model

                        BannerModel sliderBanner = sliderSnapshot.getValue(BannerModel.class);
                        if (sliderBanner != null) {
                            sliderBanner.setReference(sliderSnapshot.getRef());
                            sliderBanners.add(sliderBanner);
                        }
                    }
                }

                for (DataSnapshot topBannersSnapshot : dataSnapshot.child("Banners/Top").getChildren()) {
                    if (topBannersSnapshot.child("id").getValue() instanceof String
                            && topBannersSnapshot.child("type").getValue() instanceof String
                            && topBannersSnapshot.child("image").getValue() instanceof String
                            && topBannersSnapshot.child("numberOfClicks").getValue() instanceof Long) {// validate data types before add in model

                        BannerModel banner = topBannersSnapshot.getValue(BannerModel.class);
                        if (banner != null) {
                            banner.setReference(topBannersSnapshot.getRef());
                            topBanners.add(banner);
                        }
                    }
                }

                for (DataSnapshot midBannersSnapshot : dataSnapshot.child("Banners/Mid").getChildren()) {
                    if (midBannersSnapshot.child("id").getValue() instanceof String
                            && midBannersSnapshot.child("type").getValue() instanceof String
                            && midBannersSnapshot.child("image").getValue() instanceof String
                            && midBannersSnapshot.child("numberOfClicks").getValue() instanceof Long) {// validate data types before add in model

                        BannerModel banner = midBannersSnapshot.getValue(BannerModel.class);
                        if (banner != null) {
                            banner.setReference(midBannersSnapshot.getRef());
                            midBanners.add(banner);
                        }
                    }
                }

                for (DataSnapshot midBannersSnapshot : dataSnapshot.child("Banners/Bottom").getChildren()) {
                    if (midBannersSnapshot.child("id").getValue() instanceof String
                            && midBannersSnapshot.child("type").getValue() instanceof String
                            && midBannersSnapshot.child("image").getValue() instanceof String
                            && midBannersSnapshot.child("numberOfClicks").getValue() instanceof Long) {// validate data types before add in model

                        BannerModel banner = midBannersSnapshot.getValue(BannerModel.class);
                        if (banner != null) {
                            banner.setReference(midBannersSnapshot.getRef());
                            bottomBanners.add(banner);
                        }
                    }
                }

                DataManager.getInstance().homeCollections(new BaseCallback() {
                    @Override
                    public void onResponse(int status) {
                        if (status == 200) {
                            runOnUiThread(() -> {
                                for (int i = 0; i < topCollections.size(); i++) {
                                    CollectionModel topCollectionModel = DataManager.getInstance().getCollectionByID(topCollections.get(i).getEncodedId());
                                    if (topCollectionModel != null) {
                                        topCollectionModel.setImage(topCollections.get(i).getImage());
                                        topCollectionModels.add(topCollectionModel);
                                    } else Log.e(TAG, "onResponse: top collection null");
                                }
                                for (int i = 0; i < midCollections.size(); i++) {
                                    CollectionModel midCollectionModel = DataManager.getInstance().getCollectionByID(midCollections.get(i).getEncodedId());
                                    if (midCollectionModel != null) {
                                        midCollectionModel.setImage(midCollections.get(i).getImage());
                                        Log.e(TAG, "onResponse: mid collection " + midCollectionModel.getTitle() + " " + topCollections.get(i).getImage());
                                        midCollectionModels.add(midCollectionModel);
                                    } else Log.e(TAG, "onResponse: mid collection null");
                                }
                                for (int i = 0; i < bottomCollections.size(); i++) {
                                    CollectionModel bottomCollectionModel = DataManager.getInstance().getCollectionByID(bottomCollections.get(i).getEncodedId());
                                    if (bottomCollectionModel != null) {
                                        bottomCollectionModel.setImage(bottomCollections.get(i).getImage());
                                        Log.e(TAG, "onResponse:bottom collection " + bottomCollectionModel.getTitle() + " " + topCollections.get(i).getImage());
                                        bottomCollectionModels.add(bottomCollectionModel);
                                    } else
                                        Log.e(TAG, "onResponse:bottom collection null");
                                }
                                if (homePageLoadedListener != null) {
                                    homePageLoadedListener.onSliderLoaded(sliderBanners);
                                    homePageLoadedListener.onTopCollectionLoaded(topCollectionModels);
                                    homePageLoadedListener.onMidCollectionLoaded(midCollectionModels);
                                    homePageLoadedListener.onBottomCollectionLoaded(bottomCollectionModels);
                                    homePageLoadedListener.onTopBannersLoaded(topBanners);
                                    homePageLoadedListener.onMidBannersLoaded(midBanners);
                                    homePageLoadedListener.onBottomBannersLoaded(bottomBanners);
                                }

                                llSplash.animate()
                                        .translationY(llSplash.getHeight())
                                        .alpha(0.0f)
                                        .setDuration(300)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                super.onAnimationEnd(animation);
                                                llSplash.setVisibility(View.GONE);
                                                llHome.setVisibility(View.VISIBLE);
                                            }
                                        });
                            });
                        } else {
                            this.onFailure("An unknown error has occurred");
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        Log.e(TAG, "onFailure: " + message);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error.getMessage());
            }
        });

    }

    public interface HomePageLoadedListener {

        void onSliderLoaded(List<BannerModel> sliderList);

        void onTopCollectionLoaded(List<CollectionModel> collections);

        void onMidCollectionLoaded(List<CollectionModel> collections);

        void onBottomCollectionLoaded(List<CollectionModel> collections);

        void onTopBannersLoaded(List<BannerModel> banners);

        void onMidBannersLoaded(List<BannerModel> banners);

        void onBottomBannersLoaded(List<BannerModel> banners);

    }

    public static void setHomePageLoadedListener(HomePageLoadedListener homePageLoadedListener) {
        HomeActivity.homePageLoadedListener = homePageLoadedListener;
    }
}