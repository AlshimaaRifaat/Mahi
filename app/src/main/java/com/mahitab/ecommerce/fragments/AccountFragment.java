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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.activities.HomeActivity;
import com.mahitab.ecommerce.activities.LoginActivity;
import com.mahitab.ecommerce.activities.RegisterActivity;
import com.mahitab.ecommerce.managers.GraphClientManager;
import com.mahitab.ecommerce.models.CurrentUser;
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
    private LinearLayout llLoginRegister,ll_customer_account_data;
    private Button btnSignOut;

    private String email;
    private String password;
    private String accessToken;

    private SharedPreferences defaultPreferences;
    SharedPreferences sharedPreferences;

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
        View view= inflater.inflate(R.layout.fragment_account, container, false);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        getSavedAccessToken();
        if (getActivity() != null && getActivity() instanceof HomeActivity) {
            ((HomeActivity) getActivity()).setSupportActionBar(toolbar);
            setHasOptionsMenu(true);
        }

        btnLogin.setOnClickListener(v -> startActivity(new Intent(getContext(), LoginActivity.class)));
        btnRegister.setOnClickListener(v -> startActivity(new Intent(getContext(), RegisterActivity.class)));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && isResumed()) {
            toolbar.setTitle(getResources().getString(R.string.account));
        }
        email = defaultPreferences.getString("email", null);
        password = defaultPreferences.getString("password", null);


    }
    private void getSavedAccessToken() {

             sharedPreferences = getActivity().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);
             accessToken = sharedPreferences.getString("token", null);
             Log.d(TAG, "getSavedAccessToken: " + accessToken);
        if(accessToken!=null) {
             fetchCustomerQuery(accessToken);

            llLoginRegister.setVisibility(View.GONE);
            btnSignOut.setVisibility(View.VISIBLE);
            ll_customer_account_data.setVisibility(View.VISIBLE);
            btnSignOut.setOnClickListener(onClickListener -> {
                defaultPreferences.edit().putString("token", null).apply();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "You are Logged Out Successfuly!", Toast.LENGTH_SHORT).show();
                });
            });

        }else{
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
        ll_customer_account_data= view.findViewById(R.id.ll_customer_account_data);

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

                    if(response.data().getCustomer().getFirstName()!=null)
                    {
                        tvCustomerName.setText(customer.getFirstName());
                        Log.e("data", "user..." + response.data().getCustomer().getFirstName().toString());
                    }


                    Log.e(TAG, "onResponse: getDisplayName" + customer.getDisplayName() + " getFirstName " + customer.getFirstName() + " getLastName" + customer.getLastName());
                }else Log.e(TAG, "onResponse: here" );
            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                Log.e(TAG, "Failed to execute query", error);
            }
        });
    }

}