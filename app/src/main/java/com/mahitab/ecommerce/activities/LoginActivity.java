package com.mahitab.ecommerce.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.managers.GraphClientManager;
import com.mahitab.ecommerce.utils.CommonUtils;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.Storefront;

import static com.mahitab.ecommerce.utils.CommonUtils.setArDefaultLocale;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private TextInputLayout tilEmail;
    private EditText etEmail;
    private TextInputLayout tilPassword;
    private EditText etPassword;
    private TextView tvForgetPassword;
    private Button btnLogin;

    private String email;
    private String password;
    private String accessToken;

    private SharedPreferences defaultPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setArDefaultLocale(this);
        setContentView(R.layout.activity_login);

        initView();

        defaultPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        tvForgetPassword.setOnClickListener(v ->
                startActivity(new Intent(this,ResetPasswordActivity.class))
                );

        btnLogin.setOnClickListener(v -> {
            email = etEmail.getText().toString();
            password = etPassword.getText().toString();

            if (validateFields(email, password)) {
                queryForLoginUser();
                saveEmailAndPassword(email, password);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0); // remove activity default transition
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        tilEmail = findViewById(R.id.tilEmail_LoginActivity);
        etEmail = findViewById(R.id.etEmail_LoginActivity);
        tilPassword = findViewById(R.id.tilPassword_LoginActivity);
        etPassword = findViewById(R.id.etPassword_LoginActivity);
        tvForgetPassword = findViewById(R.id.tvForgetPassword_LoginActivity);
        btnLogin = findViewById(R.id.btnLogin_LoginActivity);
    }

    private boolean validateFields(String email, String pass) {
        boolean valid = true;
        if (!CommonUtils.isNotEmpty(email)) {
            tilEmail.setError("Field cannot be empty");
            valid = false;
        } else if (!CommonUtils.isEmailValid(email)) {
            tilEmail.setError("Invalid email");
            valid = false;
        }

        if (!CommonUtils.isNotEmpty(pass)) {
            tilPassword.setError("Field cannot be empty");
            valid = false;
        }

        return valid;
    }

    private void queryForLoginUser() {
        Log.d(TAG, "email: " + email);
        Log.d(TAG, "pass: " + password);
        Storefront.CustomerAccessTokenCreateInput input = new Storefront.CustomerAccessTokenCreateInput(email, password);

        Storefront.MutationQuery mutationQuery = Storefront.mutation(mutation -> mutation
                .customerAccessTokenCreate(input,
                        accessTokenQuery -> accessTokenQuery
                                .customerAccessToken(
                                        tokenQuery -> tokenQuery
                                                .accessToken()
                                                .expiresAt()
                                )
                                .userErrors(
                                        errorsQuery -> errorsQuery
                                                .field()
                                                .message()
                                )
                )
        );
        requestLogin(mutationQuery);
    }

    private void requestLogin(Storefront.MutationQuery mutationQuery) {
        GraphClientManager.mClient.mutateGraph(mutationQuery).enqueue(new GraphCall.Callback<Storefront.Mutation>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.Mutation> response) {
                runOnUiThread(() -> {
                    Log.d(TAG, "state: " + "success");
                    if (response.data() != null) {
                        accessToken = response.data().getCustomerAccessTokenCreate().getCustomerAccessToken().getAccessToken();
                        Log.d(TAG, "token: " + accessToken);
                        saveAccessToken(accessToken);
                        onBackPressed();
                    }
                });
            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                LoginActivity.this.runOnUiThread(() -> {
                    if (error.getMessage() != null)
                        showErrors(error.getMessage());
                    Log.d(TAG, "fail: " + error.getMessage());
                });
            }
        });
    }

    private void saveEmailAndPassword(String email, String password) {
        defaultPreferences.edit().putString("email", email).apply();
        defaultPreferences.edit().putString("password", password).apply();
    }

    private void saveAccessToken(String accessToken) {
        defaultPreferences.edit().putString("token", accessToken).apply();
    }

    private void showErrors(String msg) {
        if (msg.toLowerCase().contains("customer")) {
            tilEmail.setError(msg);
            return;
        }

        showOnUiThread(msg);
    }

    public void showOnUiThread(String msg) {
        super.runOnUiThread(() -> Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show());
    }
}