package com.mahitab.ecommerce.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.managers.GraphClientManager;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.Storefront;

public class AddEditAddressActivity extends AppCompatActivity {

    private static final String TAG = "AddEditAddressActivity";

    private EditText etFirstName;
    private EditText etLastName;
    private EditText etAddress;
    private EditText etBuildingNo;
    private EditText etCity;
    private Spinner spProvince;
    private EditText etZipCode;
    private EditText etMobileNumber;
    private Button btnSave;
    private Button btnCancel;

    private SharedPreferences defaultPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_address);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initView();

        defaultPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        if (getIntent().getExtras() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.edit_address));
        } else getSupportActionBar().setTitle(getResources().getString(R.string.add_address));

        btnSave.setOnClickListener(v -> {
            String accessToken = defaultPreferences.getString("token", null);
            if (accessToken != null)
                queryCreateAddress(accessToken);
        });
        btnCancel.setOnClickListener(v -> onBackPressed());
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
        etFirstName = findViewById(R.id.etFirstName_AddEditAddressActivity);
        etLastName = findViewById(R.id.etLastName_AddEditAddressActivity);
        etAddress = findViewById(R.id.etAddress_AddEditAddressActivity);
        etBuildingNo = findViewById(R.id.etBuildingNo_AddEditAddressActivity);
        etCity = findViewById(R.id.etCity_AddEditAddressActivity);
        spProvince = findViewById(R.id.spProvince_);
        etZipCode = findViewById(R.id.etZipCode_AddEditAddressActivity);
        etMobileNumber = findViewById(R.id.etMobileNumber_AddEditAddressActivity);
        btnSave = findViewById(R.id.btnSave_AddEditAddressActivity);
        btnCancel = findViewById(R.id.btnCancel_AddEditAddressActivity);
    }

    private void queryCreateAddress(String accessToken) {
        Log.d(TAG, "token: " + accessToken);
        Storefront.MailingAddressInput input = new Storefront.MailingAddressInput()
                .setFirstName(etFirstName.getText().toString())
                .setLastName(etLastName.getText().toString())
                .setPhone(etMobileNumber.getText().toString())
                .setCity(etCity.getText().toString())
                .setCountry("Egypt")
                .setZip(etZipCode.getText().toString())
                .setProvince(spProvince.getSelectedItem().toString())
                .setAddress1(etAddress.getText().toString())
                .setAddress2(etBuildingNo.getText().toString());
        Storefront.MutationQuery mutationQuery = Storefront.mutation(mutation -> mutation
                .customerAddressCreate(accessToken, input, query -> query
                        .customerAddress(customerAddress -> customerAddress
                                .address1()
                                .address2()
                        )
                        .userErrors(userError -> userError
                                .field()
                                .message()
                        )
                )
        );
        createAddress(mutationQuery);
    }

    private void createAddress(Storefront.MutationQuery mutationQuery) {
        GraphClientManager.mClient.mutateGraph(mutationQuery).enqueue(new GraphCall.Callback<Storefront.Mutation>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.Mutation> response) {
                if (!response.hasErrors()) {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.address_added_successfully), Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                Log.d(TAG, "onFailure: " + error.getMessage());

            }
        });
    }

}