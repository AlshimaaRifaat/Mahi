package com.mahitab.ecommerce.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.badge.BadgeDrawable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.activities.HomeActivity;
import com.mahitab.ecommerce.adapters.CartAdapter;
import com.mahitab.ecommerce.models.CartItemQuantity;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartFragment extends Fragment implements CartAdapter.CartProductClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private RecyclerView rvCartProducts;
    private List<CartItemQuantity> cartProducts;
    private CartAdapter cartAdapter;

    private LinearLayout llEmptyCart;
    private LinearLayout llContentCart;

    private TextView tvSubTotalPrice;

    private SharedPreferences defaultPreferences;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        defaultPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (defaultPreferences.getString("cartProducts", null) == null)
            cartProducts = new ArrayList<>();
        else
            cartProducts = new Gson().fromJson(defaultPreferences.getString("cartProducts", null), new TypeToken<List<CartItemQuantity>>() {
            }.getType());


        displaySuitableLayout();

        rvCartProducts.setHasFixedSize(true);
        rvCartProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartAdapter(cartProducts);
        rvCartProducts.setAdapter(cartAdapter);
        cartAdapter.setCartProductClickListener(this);

        calculateSubTotalUpdateUI();

        defaultPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        defaultPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (sharedPreferences.contains("cartProducts")) {
            defaultPreferences.edit().putString("cartProducts", new Gson().toJson(cartProducts)).apply();
        }
    }

    private void initView(View view) {
        llEmptyCart = view.findViewById(R.id.llEmptyCart_CartFragment);
        llContentCart = view.findViewById(R.id.llContentCart_CartFragment);
        rvCartProducts = view.findViewById(R.id.rvCartProducts_CartFragment);
        tvSubTotalPrice = view.findViewById(R.id.tvSubTotalPrice_CartFragment);
    }

    @Override
    public void onIncreaseProductQuantityClick(int position) {
        cartProducts.get(position).plusQuantity();
        cartAdapter.notifyDataSetChanged();
        defaultPreferences.edit().putString("cartProducts", new Gson().toJson(cartProducts)).apply();
        calculateSubTotalUpdateUI();
    }

    @Override
    public void onDecreaseProductQuantityClick(int position) {
        if (cartProducts.get(position).getQuantity() > 1)
            cartProducts.get(position).minQuantity();
        else
            cartProducts.remove(position);
        cartAdapter.notifyDataSetChanged();
        defaultPreferences.edit().putString("cartProducts", new Gson().toJson(cartProducts)).apply();
        calculateSubTotalUpdateUI();
    }

    @Override
    public void onDeleteProductClick(int position) {
        cartProducts.remove(position);
        cartAdapter.notifyDataSetChanged();
        defaultPreferences.edit().putString("cartProducts", new Gson().toJson(cartProducts)).apply();
        calculateSubTotalUpdateUI();
        BadgeDrawable cartBadge = ((HomeActivity) getActivity()).getBnvHomeNavigation().getOrCreateBadge(R.id.cart_navigation);
        if (cartProducts.size() >= 1) {
            cartBadge.setVisible(true);
            cartBadge.setNumber(cartProducts.size());
            cartBadge.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        } else cartBadge.setVisible(false);
    }

    private void calculateSubTotalUpdateUI() {
        double subTotal = 0;
        for (CartItemQuantity cartItem : cartProducts) {
            subTotal += cartItem.getQuantity() * cartItem.getProductPrice();
        }
        String subTotalPrice = NumberFormat.getInstance(new Locale("ar")).format(subTotal) + getString(R.string.egp);
        tvSubTotalPrice.setText(subTotalPrice);
    }

    private void displaySuitableLayout() {
        if (cartProducts.size() == 0) {
            llEmptyCart.setVisibility(View.VISIBLE);
            llContentCart.setVisibility(View.GONE);
        } else {
            llEmptyCart.setVisibility(View.GONE);
            llContentCart.setVisibility(View.VISIBLE);
        }
    }
}