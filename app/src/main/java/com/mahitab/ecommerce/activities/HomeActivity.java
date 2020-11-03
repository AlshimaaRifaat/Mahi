package com.mahitab.ecommerce.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.fragments.AccountFragment;
import com.mahitab.ecommerce.fragments.CartFragment;
import com.mahitab.ecommerce.fragments.CategoriesFragment;
import com.mahitab.ecommerce.fragments.HomeFragment;
import com.mahitab.ecommerce.managers.DataManager;
import com.mahitab.ecommerce.managers.interfaces.BaseCallback;
import com.mahitab.ecommerce.models.CartItemQuantity;
import com.mahitab.ecommerce.models.CollectionModel;

import java.util.ArrayList;
import java.util.List;

import static com.mahitab.ecommerce.utils.CommonUtils.setArDefaultLocale;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG ="HomeActivity" ;
    private boolean doubleBackToExitPressedOnce = false;
    private BottomNavigationView bnvHomeNavigation;
    private SharedPreferences defaultPreferences;

    private LinearLayout llHome;
    private LinearLayout llSplash;

    private List<CollectionModel> collections;
    private static CollectionLoadListener collectionLoadListener;
 String sign;
    List<CartItemQuantity> cartProducts=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setArDefaultLocale(this);
        setContentView(R.layout.activity_home);

        initView();
cartProducts=new ArrayList<>();
        defaultPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        //loading the home default fragment
        changeFragment(new HomeFragment(), HomeFragment.class.getName(),
                bnvHomeNavigation.getMenu().getItem(0));

        bnvHomeNavigation.setOnNavigationItemSelectedListener(this);

        DataManager.getInstance().setClientManager(this); // init shopify sdk

        geCollectionsWithSomeProducts();
    }

    @Override
    protected void onResume() {
        super.onResume();


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
        sign = intent.getStringExtra("from_payment");
        Log.d(TAG, "getSignAfterPayment1: " + sign);
        cartProducts = new Gson().fromJson(defaultPreferences.getString("cartProducts", null), new TypeToken<List<CartItemQuantity>>() {
        }.getType());
        Log.d(TAG, "onPause: "+cartProducts);

        if (sign != null) {
            cartProducts.clear();
            defaultPreferences.edit().putString("cartProducts", new Gson().toJson(cartProducts)).apply();
        }
    }

    @Override
    public void onBackPressed() {
        if (bnvHomeNavigation.getSelectedItemId() != R.id.home_navigation)
            changeFragment(new HomeFragment(), HomeFragment.class
                    .getSimpleName(), bnvHomeNavigation.getMenu().getItem(0));
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

    private void geCollectionsWithSomeProducts() {
        DataManager.getInstance().collections(new BaseCallback() {
            @Override
            public void onResponse(int status) {
                if (status == 200) {
                    runOnUiThread(() -> {
                        collections = DataManager.getInstance().getCollections();
                        collections.removeIf(collection -> (collection.getPreviewProducts().size() == 0)); //remove collection if has no products
                        if (collectionLoadListener != null)
                            collectionLoadListener.onCollectionLoaded(collections);
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

            }
        });
    }

    public interface CollectionLoadListener {
        void onCollectionLoaded(List<CollectionModel> collections);
    }

    public static void setCollectionLoadListener(CollectionLoadListener collectionLoadListener) {
        HomeActivity.collectionLoadListener = collectionLoadListener;
    }
}