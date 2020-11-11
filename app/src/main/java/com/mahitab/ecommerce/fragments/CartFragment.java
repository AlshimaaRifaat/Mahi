package com.mahitab.ecommerce.fragments;

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
import com.mahitab.ecommerce.activities.PaymentWebViewActivity;
import com.mahitab.ecommerce.activities.ProductDetailsActivity;
import com.mahitab.ecommerce.adapters.CartAdapter;
import com.mahitab.ecommerce.managers.GraphClientManager;
import com.mahitab.ecommerce.models.CartItemQuantity;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.ID;
import com.shopify.graphql.support.Input;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private Button checkoutButton;


    String accessToken;
    String firstName = " ";
    String lastName = "";
    String phone = "";
    String city = "";
    String country = "";
    String zip = "";
    String province = "";
    String address1 = "";
    String address2 = "";
    String email = "";
    String strEmail, strPassword;

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

        if (defaultPreferences.getString("email", null) != null)
            createAccessToken();

        checkoutButton.setOnClickListener(view1 -> {
            ArrayList<Storefront.CheckoutLineItemInput> inputArrayList = new ArrayList<>();
            for (int i = 0; i < cartProducts.size(); i++) {
                inputArrayList.add(new Storefront.CheckoutLineItemInput(cartProducts.get(i).getQuantity(), cartProducts.get(i).getVariantId()));
            }
            Storefront.CheckoutCreateInput input = new Storefront.CheckoutCreateInput()
                    .setLineItemsInput(Input.value(inputArrayList));
            createCashOnDeliveryCheckOut(input);
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

    private void initView(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        llEmptyCart = view.findViewById(R.id.llEmptyCart_CartFragment);
        llContentCart = view.findViewById(R.id.llContentCart_CartFragment);
        rvCartProducts = view.findViewById(R.id.rvCartProducts_CartFragment);
        tvSubTotalPrice = view.findViewById(R.id.tvSubTotalPrice_CartFragment);
        checkoutButton = view.findViewById(R.id.checkoutButton);
    }

    private void createAccessToken() {
        getSavedEmailAndPassword();
        Log.d(TAG, "createAccessToken :e " + strEmail);
        Log.d(TAG, "createAccessToken:p " + strPassword);
        Storefront.CustomerAccessTokenCreateInput tokenCreateInput = new Storefront.CustomerAccessTokenCreateInput(strEmail, strPassword);

        Storefront.MutationQuery mutationQuery = Storefront.mutation(mutation -> mutation
                .customerAccessTokenCreate(tokenCreateInput, query -> query
                        .customerAccessToken(customerAccessToken -> customerAccessToken
                                .accessToken()
                                .expiresAt()

                        )
                        .userErrors(userError -> userError
                                .field()
                                .message()
                        )
                )
        );
        getAccessTokenFromAPI(mutationQuery);
    }

    private void getSavedEmailAndPassword() {
        strEmail = defaultPreferences.getString("email", null);
        strPassword = defaultPreferences.getString("password", null);
    }

    private void getAccessTokenFromAPI(Storefront.MutationQuery mutationQuery) {
        GraphClientManager.mClient.mutateGraph(mutationQuery).enqueue(new GraphCall.Callback<Storefront.Mutation>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.Mutation> response) {

                if (!response.data().getCustomerAccessTokenCreate().getUserErrors().isEmpty()) {
                    for (Storefront.UserError error : response.data().getCustomerAccessTokenCreate().getUserErrors()) {
                        Log.e("TAG", "error is" + error.getMessage());
                    }
                } else {
                    accessToken = response.data().getCustomerAccessTokenCreate().getCustomerAccessToken().getAccessToken();
                    Log.e("TAG", "login" + response.data().getCustomerAccessTokenCreate().getCustomerAccessToken().getAccessToken());
                    // queryUserDetails(accessToken);
                    fetchCustomerQuery(accessToken);
                }
            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                Log.d("TAG", "Create customer Account API FAIL:" + error.getMessage());
            }
        });
    }

    private void fetchCustomerQuery(String accessToken) {
        Storefront.QueryRootQuery queryRootQuery = Storefront.query(rootQuery -> rootQuery
                .customer(
                        accessToken,
                        userQuery -> userQuery
                                .id()
                                .firstName()
                                .lastName()
                                .email()
                                .acceptsMarketing()
                                .displayName()
                                .phone()
                                .defaultAddress(
                                        address -> address
                                                .firstName()
                                                .lastName()
                                                .address1()
                                                .address2()
                                                .phone()
                                                .company()
                                                .city()
                                                .country()
                                                .province()
                                                .zip()
                                )
                                .addresses(
                                        args -> args
                                                .first(25),
                                        address -> address
                                                .edges(
                                                        edge -> edge
                                                                .node(
                                                                        node -> node
                                                                                .firstName()
                                                                                .lastName()
                                                                                .address1()
                                                                                .address2()
                                                                                .phone()
                                                                                .company()
                                                                                .city()
                                                                                .country()
                                                                                .province()
                                                                                .zip()
                                                                )
                                                )
                                )
                )
        );

        getCustomerInformation(queryRootQuery);
    }

    private void getCustomerInformation(Storefront.QueryRootQuery queryRootQuery) {
        GraphClientManager.mClient.queryGraph(queryRootQuery).enqueue(new GraphCall.Callback<Storefront.QueryRoot>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {

                if (response.data().getCustomer().getFirstName() != null) {
                    firstName = response.data().getCustomer().getFirstName();
                    Log.e("data", "user..." + response.data().getCustomer().getFirstName());
                }
                if (response.data().getCustomer().getLastName() != null) {
                    lastName = response.data().getCustomer().getLastName();
                    Log.e("data", "user..." + response.data().getCustomer().getLastName());
                }
                if (response.data().getCustomer().getEmail() != null) {
                    email = response.data().getCustomer().getEmail();
                    Log.e("data", "user..." + response.data().getCustomer().getEmail());
                }


                if (response.data().getCustomer().getDefaultAddress().getPhone() != null) {
                    phone = response.data().getCustomer().getDefaultAddress().getPhone();
                }

                if (response.data().getCustomer().getDefaultAddress().getCity() != null) {
                    city = response.data().getCustomer().getDefaultAddress().getCity();
                }
                if (response.data().getCustomer().getDefaultAddress().getCountry() != null) {
                    country = response.data().getCustomer().getDefaultAddress().getCountry();
                }
                if (response.data().getCustomer().getDefaultAddress().getZip() != null) {
                    zip = response.data().getCustomer().getDefaultAddress().getZip();
                }
                if (response.data().getCustomer().getDefaultAddress().getProvince() != null) {
                    province = response.data().getCustomer().getDefaultAddress().getProvince();
                }
                if (response.data().getCustomer().getDefaultAddress().getAddress1() != null) {
                    address1 = response.data().getCustomer().getDefaultAddress().getAddress1();
                }
                if (response.data().getCustomer().getDefaultAddress().getAddress2() != null) {
                    address2 = response.data().getCustomer().getDefaultAddress().getAddress2();
                }


            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                Log.e("TAG", "Failed to execute query", error);
            }
        });
    }

    private void createCashOnDeliveryCheckOut(Storefront.CheckoutCreateInput input) {
        Storefront.MutationQuery query = Storefront.mutation(mutationQuery -> mutationQuery
                .checkoutCreate(input, createPayloadQuery -> createPayloadQuery
                        .checkout(checkoutQuery -> checkoutQuery
                                .webUrl()
                        )
                        .userErrors(userErrorQuery -> userErrorQuery
                                .field()
                                .message()
                        ))
        );

        getCashPaymentStatus(query);
    }

    private void getCashPaymentStatus(Storefront.MutationQuery query) {

        GraphClientManager.mClient.mutateGraph(query).enqueue(new GraphCall.Callback<Storefront.Mutation>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.Mutation> response) {
                if (!response.data().getCheckoutCreate().getUserErrors().isEmpty()) {
                    // handle user friendly errors
                } else {
                    ID checkoutId = response.data().getCheckoutCreate().getCheckout().getId();
                    Log.d(TAG, "ch id: " + checkoutId.toString());

                    if (checkoutId.toString() != null) {
                        queryUpdateEmail(checkoutId);
                    }


                }

            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                // handle errors
                // Log.d(TAG, "onFailure: " + error.getMessage().toString());
            }
        });
    }

    private void queryUpdateEmail(ID checkoutId) {
        Storefront.MutationQuery mutationQuery = Storefront.mutation(mutation -> mutation
                .checkoutEmailUpdate(checkoutId,
                        email,
                        result -> result
                                .checkout(
                                        checkout -> checkout
                                                .webUrl()
                                                .email()
                                                .shippingAddress(
                                                        address -> address
                                                                .firstName()
                                                                .lastName()
                                                                .phone()
                                                                .company()
                                                                .address1()
                                                                .address2()
                                                                .city()
                                                                .province()
                                                                .country()
                                                                .zip()
                                                )
                                                .createdAt()
                                )
                                .userErrors(
                                        error -> error
                                                .field()
                                                .message()
                                )
                )
        );
        updateEmail(mutationQuery);
    }

    private void updateEmail(Storefront.MutationQuery mutationQuery) {
        GraphClientManager.mClient.mutateGraph(mutationQuery).enqueue(new GraphCall.Callback<Storefront.Mutation>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.Mutation> response) {

                String strCheckoutId = response.data().getCheckoutEmailUpdate().getCheckout().getId().toString();
                Log.d(TAG, "ch id email: " + strCheckoutId);

                if (strCheckoutId != null) {

                    ID checkoutId = new ID(strCheckoutId);
                    Log.d(TAG, "itt: " + checkoutId);
                    queryUpdateAddress(checkoutId);
                }


            }

            @Override
            public void onFailure(@NonNull GraphError error) {

            }
        });
    }

    private void queryUpdateAddress(ID checkoutId) {
        Log.d(TAG, "firstName: " + firstName);
        Storefront.MailingAddressInput inputAddress = new Storefront.MailingAddressInput()
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPhone(phone)
                .setCity(city)
                .setCountry(country)
                .setZip(zip)
                .setProvince(province)
                .setAddress1(address1)
                .setAddress2(address2);
        Storefront.MutationQuery mutationQuery = Storefront.mutation(mutation -> mutation
                .checkoutShippingAddressUpdate(
                        inputAddress,
                        checkoutId,
                        result -> result
                                .checkout(
                                        checkout -> checkout
                                                .email()
                                                .webUrl()
                                                .shippingAddress(
                                                        address -> address
                                                                .firstName()
                                                                .lastName()
                                                                .phone()
                                                                .company()
                                                                .address1()
                                                                .address2()
                                                                .city()
                                                                .province()
                                                                .country()
                                                                .zip()
                                                )
                                )
                                .userErrors(
                                        error -> error
                                                .field()
                                                .message()
                                )
                )
        );
        updateAddress(mutationQuery);
    }

    private void updateAddress(Storefront.MutationQuery mutationQuery) {
        GraphClientManager.mClient.mutateGraph(mutationQuery).enqueue(new GraphCall.Callback<Storefront.Mutation>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.Mutation> response) {
                String webUrl = response.data().getCheckoutShippingAddressUpdate().getCheckout().getWebUrl();
                Log.d(TAG, "web url: " + response.data().getCheckoutShippingAddressUpdate().getCheckout().getWebUrl());
                ID checkoutId = response.data().getCheckoutShippingAddressUpdate().getCheckout().getId();

                Log.d(TAG, "id: " + checkoutId.toString());


                Intent Getintent = new Intent(getContext(), PaymentWebViewActivity.class);
                Getintent.putExtra("web_url", webUrl);
                Bundle bundle = new Bundle();
                bundle.putSerializable("checkout_id", (Serializable) checkoutId);
                Getintent.putExtras(bundle);
                startActivity(Getintent);

                Log.d(TAG, "iddd: " + checkoutId.toString());


            }

            @Override
            public void onFailure(@NonNull GraphError error) {

            }
        });
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
        Log.e(TAG, "onProductClick: "+productId );
        Intent intent = new Intent(getContext(), ProductDetailsActivity.class);
        intent.putExtra("productId", productId);
        startActivity(intent);
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

    private void changeBadge(){
        BadgeDrawable cartBadge = ((HomeActivity) getActivity()).getBnvHomeNavigation().getOrCreateBadge(R.id.cart_navigation);
        if (cartProducts.size() >= 1) {
            cartBadge.setVisible(true);
            cartBadge.setNumber(cartProducts.size());
            cartBadge.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
        } else cartBadge.setVisible(false);
    }
}