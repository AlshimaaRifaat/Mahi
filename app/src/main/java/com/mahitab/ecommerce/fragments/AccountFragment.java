package com.mahitab.ecommerce.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.activities.ChangePasswordActivity;
import com.mahitab.ecommerce.activities.HomeActivity;
import com.mahitab.ecommerce.activities.LoginActivity;
import com.mahitab.ecommerce.activities.MyAddressesActivity;
import com.mahitab.ecommerce.activities.MyOrdersActivity;
import com.mahitab.ecommerce.activities.MyWishListActivity;
import com.mahitab.ecommerce.activities.RegisterActivity;
import com.mahitab.ecommerce.managers.GraphClientManager;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.Storefront;

public class AccountFragment extends Fragment {

    private static final String TAG = "AccountFragment";

    private Toolbar toolbar;
    private Button btnLogin;
    private Button btnRegister;
    private TextView tvCustomerName;
    private LinearLayout llLoginRegister, ll_customer_account_data;
    private Button btnSignOut;
    private TextView changepassword;

    private CardView cvMyOrders;
    private CardView cvMyWishList;
    private CardView cvMyAddresses;
    private CardView cvHelp;


    private SharedPreferences defaultPreferences;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        defaultPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_account, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);

        if (getActivity() != null && getActivity() instanceof HomeActivity) {
            ((HomeActivity) getActivity()).setSupportActionBar(toolbar);
            setHasOptionsMenu(true);
        }

        btnLogin.setOnClickListener(v -> startActivity(new Intent(getContext(), LoginActivity.class)));

        btnRegister.setOnClickListener(v -> startActivity(new Intent(getContext(), RegisterActivity.class)));

        cvMyOrders.setOnClickListener(v -> startActivity(new Intent(getActivity(), MyOrdersActivity.class)));

        cvMyWishList.setOnClickListener(v -> startActivity(new Intent(getContext(), MyWishListActivity.class)));

        cvMyAddresses.setOnClickListener(v -> startActivity(new Intent(getContext(), MyAddressesActivity.class)));
        changepassword.setOnClickListener(v -> startActivity(new Intent(getContext(), ChangePasswordActivity.class)));

        btnSignOut.setOnClickListener(onClickListener -> {
            clearUserSharedPref();
            startActivity(new Intent(getActivity(), HomeActivity.class));
            getActivity().finish();
            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), getResources().getString(R.string.sign_out_message), Toast.LENGTH_SHORT).show());
        });

        cvHelp.setOnClickListener(v -> {
            String url = "https://api.whatsapp.com/send?phone=+201111112426";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && isResumed()) {
            toolbar.setTitle(getResources().getString(R.string.account));
        }

        String accessToken = defaultPreferences.getString("token", null);

        if (accessToken != null) {
            fetchCustomerQuery(accessToken);
            llLoginRegister.setVisibility(View.GONE);
            btnSignOut.setVisibility(View.VISIBLE);
            ll_customer_account_data.setVisibility(View.VISIBLE);
        } else {
            llLoginRegister.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.GONE);
            ll_customer_account_data.setVisibility(View.GONE);
        }
    }

    private void initView(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        btnLogin = view.findViewById(R.id.btnLogin_AccountFragment);
        btnRegister = view.findViewById(R.id.btnRegister_AccountFragment);
        tvCustomerName = view.findViewById(R.id.tvCustomerName_AccountFragment);
        llLoginRegister = view.findViewById(R.id.llLoginRegister_AccountFragment);
        btnSignOut = view.findViewById(R.id.btnSignOut_AccountFragment);
        ll_customer_account_data = view.findViewById(R.id.ll_customer_account_data);
        cvMyAddresses = view.findViewById(R.id.cvMyAddresses_CustomerAccountData);
        cvMyOrders = view.findViewById(R.id.cvMyOrders_CustomerAccountData);
        cvMyWishList = view.findViewById(R.id.cvMyWishList_CustomerAccountData);
        cvHelp = view.findViewById(R.id.cvHelp_CustomerAccountData);
        this.changepassword = view.findViewById(R.id.changePassLogo);
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
                if (response.data() != null) {
                    Storefront.Customer customer = response.data().getCustomer();

                    if (response.data().getCustomer().getFirstName() != null) {
                        requireActivity().runOnUiThread(() -> tvCustomerName.setText(customer.getFirstName()));
                        Log.e("data", "user..." + response.data().getCustomer().getFirstName());
                    }

                    Log.e(TAG, "onResponse: getDisplayName" + customer.getDisplayName() + " getFirstName " + customer.getFirstName() + " getLastName" + customer.getLastName());
                } else Log.e(TAG, "onResponse: here");
            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                Log.e(TAG, "Failed to execute query", error);
            }
        });
    }

    private void clearUserSharedPref() {
        defaultPreferences.edit().putString("email",null).apply();
        defaultPreferences.edit().putString("password",null).apply();
        defaultPreferences.edit().putString("token",null).apply();
    }
}