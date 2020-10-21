package com.mahitab.ecommerce.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
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
import com.mahitab.ecommerce.models.CartItemQuantity;

import java.util.ArrayList;
import java.util.List;

import static com.mahitab.ecommerce.utils.CommonUtils.setArDefaultLocale;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private boolean doubleBackToExitPressedOnce = false;
    private BottomNavigationView bnvHomeNavigation;
    private SharedPreferences defaultPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setArDefaultLocale(this);
        setContentView(R.layout.activity_main);

        bnvHomeNavigation = findViewById(R.id.bnvHomeNavigation_HomeActivity);

        defaultPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        //loading the home default fragment
        changeFragment(new HomeFragment(), HomeFragment.class.getName(),
                bnvHomeNavigation.getMenu().getItem(0));

        bnvHomeNavigation.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        List<CartItemQuantity> cartProducts;
        if (defaultPreferences.getString("cartProducts", null) == null)
            cartProducts = new ArrayList<>();
        else
            cartProducts = new Gson().fromJson(defaultPreferences.getString("cartProducts", null), new TypeToken<List<CartItemQuantity>>() {
            }.getType());

        // change the number to see cartBadge in action
        int cartProductsCount = cartProducts.size();

        BadgeDrawable cartBadge = bnvHomeNavigation.getOrCreateBadge(R.id.cart_navigation);
        if (cartProductsCount >= 1) {
            cartBadge.setVisible(true);
            cartBadge.setNumber(cartProductsCount);
            cartBadge.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        } else cartBadge.setVisible(false);

        overridePendingTransition(0, 0); // remove activity default transition
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
}