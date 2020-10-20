package com.mahitab.ecommerce.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.adapters.CartAdapter;
import com.mahitab.ecommerce.models.CartItemQuantity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private RecyclerView rvCartProducts;
    private List<CartItemQuantity> cartProducts;
    private CartAdapter cartAdapter;
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

        rvCartProducts.setHasFixedSize(true);
        rvCartProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartAdapter();
        rvCartProducts.setAdapter(cartAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        Type type = new TypeToken<List<CartItemQuantity>>() {
        }.getType();

        if (defaultPreferences.getString("cartProducts", null) == null)
            cartProducts = new ArrayList<>();
        else
            cartProducts = new Gson().fromJson(defaultPreferences.getString("cartProducts", null), type);

        cartAdapter.setCartItemQuantities(cartProducts);
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
            Type type = new TypeToken<List<CartItemQuantity>>() {
            }.getType();
            cartProducts = new Gson().fromJson(sharedPreferences.getString("cartProducts", null), type);
            cartAdapter.notifyDataSetChanged();
        }
    }

    private void initView(View view) {
        rvCartProducts = view.findViewById(R.id.rvCartProducts_CartFragment);
    }

}