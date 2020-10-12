package com.mahitab.ecommerce.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.fragments.AccountFragment;
import com.mahitab.ecommerce.fragments.CartFragment;
import com.mahitab.ecommerce.fragments.CategoriesFragment;
import com.mahitab.ecommerce.fragments.HomeFragment;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private boolean doubleBackToExitPressedOnce = false;
    private BottomNavigationView bnvHomeNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bnvHomeNavigation = findViewById(R.id.bnvHomeNavigation_HomeActivity);

        //loading the home default fragment
        changeFragment(new HomeFragment(), HomeFragment.class.getName(),
                bnvHomeNavigation.getMenu().getItem(0));

        bnvHomeNavigation.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_navigation:
                changeFragment(new HomeFragment(), HomeFragment.class.getName(), item);
                break;
            case R.id.categories_navigation:
                changeFragment(new CategoriesFragment(), CategoriesFragment.class.getName(), item);
                break;
            case R.id.cart_navigation:
                changeFragment(new CartFragment(), CartFragment.class.getName(), item);
                break;
            case R.id.account_navigation:
                changeFragment(new AccountFragment(), AccountFragment.class.getName(), item);
                break;
        }
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

}