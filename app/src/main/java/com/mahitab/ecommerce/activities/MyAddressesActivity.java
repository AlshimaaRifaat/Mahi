package com.mahitab.ecommerce.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.adapters.AddressAdapter;
import com.mahitab.ecommerce.managers.DataManager;
import com.mahitab.ecommerce.managers.DataManagerHelper;
import com.mahitab.ecommerce.managers.GraphClientManager;
import com.mahitab.ecommerce.managers.interfaces.BaseCallback;
import com.mahitab.ecommerce.models.AddressModel;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.Storefront;

import java.util.ArrayList;
import java.util.List;

import static com.mahitab.ecommerce.utils.CommonUtils.setArDefaultLocale;

public class MyAddressesActivity extends AppCompatActivity {

    private static final String TAG = "MyAddressesActivity";
    private RecyclerView rvAddresses;
    private List<AddressModel> addresses;
    private AddressAdapter addressAdapter;

    private FloatingActionButton fab;
    SharedPreferences sharedPreferences;
    String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setArDefaultLocale(this);
        setContentView(R.layout.activity_my_addresses);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.my_addresses));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initView();
        fab.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), AddEditAddressActivity.class)));
        DataManager.getInstance().setClientManager(MyAddressesActivity.this);
        addresses=new ArrayList<>();
        getSavedAccessToken();




    }
    private void getSavedAccessToken() {
        sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        accessToken = sharedPreferences.getString("token", null);
        Log.d(TAG, "getSavedAccessToken: " + accessToken);
        queryAddresses(accessToken);
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
        rvAddresses = findViewById(R.id.rvAddresses_MyAddressesActivity);
        fab = findViewById(R.id.fab);
    }

    private void queryAddresses(String accessToken) {
        Storefront.QueryRootQuery query = Storefront.query(root -> root
                .customer(accessToken, customer -> customer
                        .addresses(arg -> arg.first(10), connection -> connection
                                .edges(edge -> edge
                                        .node(node -> node
                                                .address1()
                                                .address2()
                                                .city()
                                                .province()
                                                .country()
                                                .phone()

                                        )
                                )
                        )
                )
        );
        getAddressList(query, new BaseCallback() {
            @Override
            public void onResponse(int status) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });

            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    private void getAddressList(Storefront.QueryRootQuery query,BaseCallback callback) {
        GraphClientManager.mClient.queryGraph(query).enqueue(new GraphCall.Callback<Storefront.QueryRoot>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {

                if (!response.hasErrors()) {
                    Storefront.MailingAddressConnection connection = response.data().getCustomer().getAddresses();
                    for (Storefront.MailingAddressEdge edge : connection.getEdges()) {


                        AddressModel newAddressesModel= new AddressModel(edge);
                        Log.d(TAG, "id: "+newAddressesModel.getmID().toString());
                        DataManagerHelper.getInstance().fetchAddresses().put(newAddressesModel.getmID().toString(), newAddressesModel);

                    }
                    for (int i=0;i<DataManager.getInstance().getAddresses().size();i++) {
                        Log.d(TAG, "cities: " +DataManager.getInstance().getAddresses().get(i).getCity().toString());
                    }
                    addresses=DataManager.getInstance().getAddresses();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: "+"success");

                            addressAdapter = new AddressAdapter(MyAddressesActivity.this,addresses);
                          //  addressAdapter.onClickDeleteFromAddressList(AddressesActivity.this);
                            rvAddresses.setLayoutManager(new LinearLayoutManager(MyAddressesActivity.this));
                            rvAddresses.setHasFixedSize(true);
                            rvAddresses.setAdapter(addressAdapter);
                        }
                    });
                    callback.onResponse(BaseCallback.RESULT_OK);
                    return;

                }

                callback.onFailure(response.errors().get(0).message());

            }


            @Override
            public void onFailure(@NonNull GraphError error) {
                Log.d(TAG, "onFailure: "+error.getMessage().toString());

            }
        });
    }

}