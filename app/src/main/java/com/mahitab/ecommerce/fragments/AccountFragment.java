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
import androidx.fragment.app.Fragment;

import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.activities.HomeActivity;
import com.mahitab.ecommerce.activities.LoginActivity;
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
    private LinearLayout llLoginRegister;
    private Button btnSignOut;

    private String email;
    private String password;
    private String accessToken;

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
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && isResumed()) {
            toolbar.setTitle(getResources().getString(R.string.account));
        }
        email = defaultPreferences.getString("email", null);
        password = defaultPreferences.getString("email", null);

        if (email != null && password != null) {
            createAccessToken();
        }
    }

    private void initView(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        btnLogin = view.findViewById(R.id.btnLogin_AccountFragment);
        btnRegister = view.findViewById(R.id.btnRegister_AccountFragment);
        tvCustomerName = view.findViewById(R.id.tvCustomerName_AccountFragment);
        llLoginRegister = view.findViewById(R.id.llLoginRegister_AccountFragment);
        btnSignOut = view.findViewById(R.id.btnSignOut_AccountFragment);
    }

    private void createAccessToken() {
        Log.d(TAG, "createAccessToken :e " + email);
        Log.d(TAG, "createAccessToken:p " + password);
        Storefront.CustomerAccessTokenCreateInput tokenCreateInput = new Storefront.CustomerAccessTokenCreateInput(email, password);

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

    private void getAccessTokenFromAPI(Storefront.MutationQuery mutationQuery) {
        GraphClientManager.mClient.mutateGraph(mutationQuery).enqueue(new GraphCall.Callback<Storefront.Mutation>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.Mutation> response) {
                if (!response.data().getCustomerAccessTokenCreate().getUserErrors().isEmpty()) {
                    for (Storefront.UserError error : response.data().getCustomerAccessTokenCreate().getUserErrors()) {
                        Log.e(TAG, "error is " + error.getMessage());
                    }
                } else {
                    accessToken = response.data().getCustomerAccessTokenCreate().getCustomerAccessToken().getAccessToken();
                    Log.e(TAG, "login" + response.data().getCustomerAccessTokenCreate().getCustomerAccessToken().getAccessToken());
                    // queryUserDetails(accessToken);
                    fetchCustomerQuery(accessToken);
                }
            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                Log.d(TAG, "Create customer Account API FAIL:" + error.getMessage());
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
                if (response.data() != null) {
                    Storefront.Customer customer = response.data().getCustomer();
                    tvCustomerName.setText(customer.getFirstName());
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