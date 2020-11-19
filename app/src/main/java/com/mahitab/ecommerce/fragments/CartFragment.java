package com.mahitab.ecommerce.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.badge.BadgeDrawable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.activities.CartActivity;
import com.mahitab.ecommerce.activities.HomeActivity;
import com.mahitab.ecommerce.activities.ProductDetailsActivity;
import com.mahitab.ecommerce.adapters.CartAdapter;
import com.mahitab.ecommerce.managers.DataManager;
import com.mahitab.ecommerce.managers.DataManagerHelper;
import com.mahitab.ecommerce.managers.ShopifyManager;
import com.mahitab.ecommerce.models.AddressModel;
import com.mahitab.ecommerce.models.CartItemQuantity;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.QueryGraphCall;
import com.shopify.buy3.Storefront;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.mahitab.ecommerce.managers.ShopifyManager.LAUNCH_PAYMENT_ACTIVITY;

public class CartFragment extends Fragment implements CartAdapter.CartProductClickListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "CartFragment";

    private Toolbar toolbar;
    private RecyclerView rvCartProducts;
    public static List<CartItemQuantity> cartProducts;
    private CartAdapter cartAdapter;

    private LinearLayout llEmptyCart;
    private LinearLayout llContentCart;

    private TextView tvSubTotalPrice;

    private SharedPreferences defaultPreferences;
    private Button btnCheckout;

    private ProgressBar pbLoadingPayment;
    private ArrayList<AddressModel> customerAddresses;

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

        if (getActivity() != null && getActivity() instanceof HomeActivity) {
            ((HomeActivity) getActivity()).setSupportActionBar(toolbar);
            setHasOptionsMenu(true);
        } else if (getActivity() != null && getActivity() instanceof CartActivity) {
            toolbar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() != null && isResumed()) {
            toolbar.setTitle(getResources().getString(R.string.cart));
        }

        if (defaultPreferences.getString("cartProducts", null) == null)
            cartProducts = new ArrayList<>();
        else
            cartProducts = new Gson().fromJson(defaultPreferences.getString("cartProducts", null), new TypeToken<List<CartItemQuantity>>() {
            }.getType());


        displaySuitableLayout();

        rvCartProducts.setHasFixedSize(true);
        rvCartProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartAdapter(requireContext(), cartProducts);
        rvCartProducts.setAdapter(cartAdapter);
        cartAdapter.setCartProductClickListener(this);

        calculateSubTotalUpdateUI();

        defaultPreferences.registerOnSharedPreferenceChangeListener(this);

        String token = defaultPreferences.getString("token", null);
        if (token != null && isResumed()) {
            Log.e(TAG, "onResume: " + token);
            ShopifyManager.getCustomerAddresses(token).subscribe(new Observer<QueryGraphCall>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(QueryGraphCall queryGraphCall) {
                    queryGraphCall.enqueue(new GraphCall.Callback<Storefront.QueryRoot>() {
                        @Override
                        public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {
                            if (!response.hasErrors()) {
                                DataManagerHelper.getInstance().fetchAddresses().clear();
                                Storefront.MailingAddressConnection connection = response.data().getCustomer().getAddresses();
                                for (Storefront.MailingAddressEdge edge : connection.getEdges()) {
                                    AddressModel newAddressesModel = new AddressModel(edge);
                                    DataManagerHelper.getInstance().fetchAddresses().put(newAddressesModel.getmID().toString(), newAddressesModel);
                                }
                                customerAddresses = DataManager.getInstance().getAddresses();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull GraphError error) {

                        }
                    });
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "onError: " + e.getMessage());
                }

                @Override
                public void onComplete() {

                }
            });
        }

        btnCheckout.setOnClickListener(view1 -> {
            pbLoadingPayment.setVisibility(View.VISIBLE);
            btnCheckout.setEnabled(false);
            if (token == null) {
                ShopifyManager.checkoutAsGuest(requireActivity(), cartProducts);
            } else {
                if (customerAddresses != null && customerAddresses.size() == 1) {
                    ShopifyManager.getCurrentCustomer(token).subscribe(new Observer<QueryGraphCall>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(QueryGraphCall customerQueryGraphCall) {
                            customerQueryGraphCall.enqueue(new GraphCall.Callback<Storefront.QueryRoot>() {
                                @Override
                                public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {
                                    if (!response.hasErrors()) {
                                        ShopifyManager.checkoutAsCustomer(requireActivity(), cartProducts, response.data().getCustomer(), customerAddresses.get(0));
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull GraphError error) {
                                    Log.e(TAG, "onFailure: " + error.getMessage());
                                }
                            });
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "onError: " + e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
                }
            }
        });
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_PAYMENT_ACTIVITY && resultCode == Activity.RESULT_CANCELED) {
            pbLoadingPayment.setVisibility(View.GONE);
            btnCheckout.setEnabled(true);
        }
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
        changeBadge();
    }

    @Override
    public void onDeleteProductClick(int position) {
        cartProducts.remove(position);
        cartAdapter.notifyDataSetChanged();
        defaultPreferences.edit().putString("cartProducts", new Gson().toJson(cartProducts)).apply();
        calculateSubTotalUpdateUI();
        changeBadge();
    }

    @Override
    public void onProductClick(String productId) {
        Log.e(TAG, "onProductClick: " + productId);
        Intent intent = new Intent(getContext(), ProductDetailsActivity.class);
        intent.putExtra("productId", productId);
        startActivity(intent);
    }

    private void initView(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        llEmptyCart = view.findViewById(R.id.llEmptyCart_CartFragment);
        llContentCart = view.findViewById(R.id.llContentCart_CartFragment);
        rvCartProducts = view.findViewById(R.id.rvCartProducts_CartFragment);
        tvSubTotalPrice = view.findViewById(R.id.tvSubTotalPrice_CartFragment);
        btnCheckout = view.findViewById(R.id.btnCheckout_CartFragment);
        pbLoadingPayment = view.findViewById(R.id.pbLoadingPayment_CartFragment);
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

    private void changeBadge() {
        if (getActivity() instanceof HomeActivity) {
            BadgeDrawable cartBadge = ((HomeActivity) getActivity()).getBnvHomeNavigation().getOrCreateBadge(R.id.cart_navigation);
            if (cartProducts.size() >= 1) {
                cartBadge.setVisible(true);
                cartBadge.setNumber(cartProducts.size());
                cartBadge.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
            } else cartBadge.setVisible(false);
        }
    }
}