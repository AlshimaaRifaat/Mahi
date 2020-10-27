package com.mahitab.ecommerce.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.adapters.ProvinceSpinnerAdapter;
import com.mahitab.ecommerce.managers.GraphClientManager;
import com.mahitab.ecommerce.models.AddressModel;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.ID;

public class AddEditAddressActivity extends AppCompatActivity {

    private static final String TAG = "AddEditAddressActivity";

    private EditText etFirstName;
    private EditText etLastName;
    private EditText etAddress1,etAddress2,etCity,etZipCode,etPhone;
    private Button btnSave;
    private Button btnCancel;

    private SharedPreferences defaultPreferences;
    String addressId,firstName,lastName,phone,zipCode,city,address2,address1,accessToken;
    Intent intent;
    AddressModel addressModel;

   String[] spinnerProvinceValue = {
            "Aswan",
            "6th of October"
    };


    public static String provinceSelectedItemSpinner;
    String provinceId;

    Context context;
    Spinner spinnerProvince;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_address);

        defaultPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        accessToken = defaultPreferences.getString("token", null);
        Log.d(TAG, "onCreate: "+accessToken);
        if (getIntent().getExtras() != null) {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            initView();
            getSupportActionBar().setTitle(getResources().getString(R.string.edit_address));
            getAddressData();

        } else {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            initView();

            getSupportActionBar().setTitle(getResources().getString(R.string.add_address));
            getProvinceSpinnerItems();

        }

        btnCancel.setOnClickListener(v -> onBackPressed());
    }

    private void getProvinceSpinnerItems() {
        ProvinceSpinnerAdapter genderSpinnerAdapter = new ProvinceSpinnerAdapter(this, R.layout.spinner_item);

        genderSpinnerAdapter.addAll(spinnerProvinceValue);
        genderSpinnerAdapter.add("Province");
        spinnerProvince.setAdapter(genderSpinnerAdapter);
        spinnerProvince.setPrompt("Aswan");

        spinnerProvince.setSelection(genderSpinnerAdapter.getCount());

        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerProvince.getSelectedItem() == "Province") {

                } else {
                    provinceSelectedItemSpinner = spinnerProvince.getSelectedItem().toString();
                    btnSave.setOnClickListener(v -> {
                        Log.d(TAG, "provinceSelectedItemSpinner: " + provinceSelectedItemSpinner);
                        Log.d(TAG, "token: " + accessToken);
                        queryCreateAddress(accessToken,provinceSelectedItemSpinner);

                    });



                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void getAddressData() {

        addressModel = (AddressModel) getIntent().getExtras().getSerializable("addressModel");

        addressId = addressModel.getmID().toString();
        firstName = addressModel.getFirstName();
        lastName = addressModel.getLastName();
        phone = addressModel.getPhone();
        zipCode = addressModel.getZipCode();
        city = addressModel.getCity();
        address2 = addressModel.getAddress2();
        address1 = addressModel.getAddress1();
        Log.d(TAG, "edit int addressID: "+addressId);
        setAddressData(addressId,firstName,lastName,phone,zipCode,city,address2,address1);



    }

    private void setAddressData(String addressId,String firstName, String lastName, String phone, String zipCode,
                                String city, String address2, String address1) {
    etFirstName.setText(firstName);
    etLastName.setText(lastName);
    etPhone.setText(phone);
    etZipCode.setText(zipCode);
    etCity.setText(city);
   // etProvince.setText(province);
    etAddress1.setText(address1);
    etAddress2.setText(address2);

        ProvinceSpinnerAdapter genderSpinnerAdapter = new ProvinceSpinnerAdapter(this, R.layout.spinner_item);

        genderSpinnerAdapter.addAll(spinnerProvinceValue);
        genderSpinnerAdapter.add("Province");
        spinnerProvince.setAdapter(genderSpinnerAdapter);
        spinnerProvince.setPrompt("Aswan");

        spinnerProvince.setSelection(genderSpinnerAdapter.getCount());

        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerProvince.getSelectedItem() == "Province") {

                } else {
                    provinceSelectedItemSpinner = spinnerProvince.getSelectedItem().toString();


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnSave.setOnClickListener(v -> {
            queryEditAddress(accessToken,addressId,provinceSelectedItemSpinner);


        });
    }

    private void queryEditAddress(String accessToken, String addressId,String province) {
        Log.d(TAG, "queryEditAddress: "+province);
        Storefront.MailingAddressInput inputAddress = new Storefront.MailingAddressInput()
                .setFirstName(etFirstName.getText().toString())
                .setLastName(etLastName.getText().toString())
                .setPhone(etPhone.getText().toString())
                .setCity(etCity.getText().toString())
                .setCountry("Egypt")
                .setZip(etZipCode.getText().toString())
                .setProvince(province)
                .setAddress1(etAddress1.getText().toString())
                .setAddress2(etAddress2.getText().toString());
        Storefront.MutationQuery mutationQuery = Storefront.mutation(mutation -> mutation
                .customerAddressUpdate(accessToken,new ID(addressId), inputAddress, query -> query
                        .customerAddress(customerAddress -> customerAddress
                                .address1()
                                .address2()
                        )
                        .userErrors(userErrorQuery -> userErrorQuery
                                .field()
                                .message()
                        )
                )
        );
       editAddress(mutationQuery);
    }

    private void editAddress( Storefront.MutationQuery mutationQuery) {
        GraphClientManager.mClient.mutateGraph(mutationQuery).enqueue(new GraphCall.Callback<Storefront.Mutation>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.Mutation> response) {

                Log.d("edit ", "onResponse: "+"Address updated successfuly ");
                if (!response.hasErrors()) {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.address_updated_successfully), Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    });
                }

            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                Log.d("edit ", "onFailure: "+error.getMessage());

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
        etFirstName = findViewById(R.id.etFirstName_AddEditAddressActivity);
        etLastName = findViewById(R.id.etLastName_AddEditAddressActivity);
        etAddress1 = findViewById(R.id.etAddress1_AddEditAddressActivity);
        etAddress2 = findViewById(R.id.etAddress2_AddEditAddressActivity);
        etCity = findViewById(R.id.etCity_AddEditAddressActivity);
        etZipCode = findViewById(R.id.etZipCode_AddEditAddressActivity);
        etPhone = findViewById(R.id.etPhone_AddEditAddressActivity);
        spinnerProvince = findViewById(R.id.spinnerProvince);
        btnSave = findViewById(R.id.btnSave_AddEditAddressActivity);
        btnCancel = findViewById(R.id.btnCancel_AddEditAddressActivity);
    }

    private void queryCreateAddress(String accessToken,String province) {

        Log.d(TAG, "queryCreateAddress : "+province);
        Storefront.MailingAddressInput input = new Storefront.MailingAddressInput()
                .setFirstName(etFirstName.getText().toString())
                .setLastName(etLastName.getText().toString())
                .setPhone(etPhone.getText().toString())
                .setCity(etCity.getText().toString())
                .setCountry("Egypt")
                .setZip(etZipCode.getText().toString())
                .setProvince(province)
                .setAddress1(etAddress1.getText().toString())
                .setAddress2(etAddress2.getText().toString());

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