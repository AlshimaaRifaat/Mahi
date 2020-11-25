package com.mahitab.ecommerce.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.adapters.AddressAdapter;
import com.mahitab.ecommerce.adapters.SelectAddressAdapter;
import com.mahitab.ecommerce.managers.DataManager;
import com.mahitab.ecommerce.managers.DataManagerHelper;
import com.mahitab.ecommerce.managers.GraphClientManager;
import com.mahitab.ecommerce.managers.interfaces.BaseCallback;
import com.mahitab.ecommerce.models.AddressModel;
import com.mahitab.ecommerce.models.CartItemQuantity;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.ID;
import com.shopify.graphql.support.Input;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.mahitab.ecommerce.utils.CommonUtils.setArDefaultLocale;

public class SelectAddressActivity extends AppCompatActivity implements SelectAddressAdapter.SelectAddressItemInterface {
    private static final String TAG = "SelectAddressActivity";
    private RecyclerView rvAddresses;
    public static List<AddressModel> addresses;
    private SelectAddressAdapter selectAddressAdapter;

    private    Dialog dialog ;
    SharedPreferences sharedPreferences;
    String accessToken;
    public static List<CartItemQuantity> cartProducts;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Dialog dialog = new Dialog(this);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        setContentView(R.layout.activity_select_address);
        initView();
        sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        DataManager.getInstance().setClientManager(SelectAddressActivity.this);

        accessToken = sharedPreferences.getString("token", null);
        Log.d(TAG, "getSavedAccessToken: " + accessToken);
        queryAddressList(accessToken);
        addresses = new ArrayList<>();
    }


    private void initView() {
        rvAddresses = findViewById(R.id.rvAddresses_MyAddressesActivity);
        this.dialog = new Dialog(this);
        dialog.setContentView(R.layout.load_dialog);
    }


    private void queryAddressList(String accessToken) {
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

    private void getAddressList(Storefront.QueryRootQuery query, BaseCallback callback) {
        GraphClientManager.mClient.queryGraph(query).enqueue(new GraphCall.Callback<Storefront.QueryRoot>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {

                if (!response.hasErrors()) {
                    DataManagerHelper.getInstance().fetchAddresses().clear();
                    Storefront.MailingAddressConnection connection = response.data().getCustomer().getAddresses();
                    for (Storefront.MailingAddressEdge edge : connection.getEdges()) {


                        AddressModel newAddressesModel = new AddressModel(edge);
                        Log.d(TAG, "zip: " + edge.getNode().getFirstName());
                        DataManagerHelper.getInstance().fetchAddresses().put(newAddressesModel.getmID().toString(), newAddressesModel);
                    }

                    for (int i = 0; i < DataManager.getInstance().getAddresses().size(); i++) {
                        Log.d(TAG, "cities: " + DataManager.getInstance().getAddresses().get(i).getCity().toString());
                        if (DataManager.getInstance().getAddresses().get(i).getZipCode() != null)
                            Log.d(TAG, "g: " + DataManager.getInstance().getAddresses().get(i).getZipCode().toString());
                    }
                    addresses = DataManager.getInstance().getAddresses();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: " + "success");
                            addresses.sort((o1, o2) -> o2.getmID().toString().compareTo(o1.getmID().toString()));

                            selectAddressAdapter = new SelectAddressAdapter(SelectAddressActivity.this, addresses,dialog);
                            selectAddressAdapter.onClickItemSelectAddress(SelectAddressActivity.this::navigateToPaymentCashOnDelivery);
                            rvAddresses.setLayoutManager(new LinearLayoutManager(SelectAddressActivity.this));
                            rvAddresses.setHasFixedSize(true);
                            rvAddresses.setAdapter(selectAddressAdapter);
                        }
                    });
                    callback.onResponse(BaseCallback.RESULT_OK);
                    return;

                }

                callback.onFailure(response.errors().get(0).message());

            }


            @Override
            public void onFailure(@NonNull GraphError error) {
                Log.d(TAG, "onFailure: " + error.getMessage().toString());

            }
        });
    }

    @Override
    public void navigateToPaymentCashOnDelivery(AddressModel addressModel, int Position) {

        if (sharedPreferences.getString("cartProducts", null) == null)
            cartProducts = new ArrayList<>();
        else
            cartProducts = new Gson().fromJson(sharedPreferences.getString("cartProducts", null), new TypeToken<List<CartItemQuantity>>() {
            }.getType());
        Log.d(TAG, "getSavedCartProducts: " + cartProducts.toString());



        getCustomerAddress(addressModel, Position);

        ArrayList<Storefront.CheckoutLineItemInput> inputArrayList = new ArrayList<>();
        for (int i = 0; i < cartProducts.size(); i++) {
            inputArrayList.add(new Storefront.CheckoutLineItemInput(cartProducts.get(i).getQuantity(), cartProducts.get(i).getVariantId()));
        }
        Storefront.CheckoutCreateInput input = new Storefront.CheckoutCreateInput()
                .setLineItemsInput(Input.value(inputArrayList));
        if(addressModel!=null) {
            createCashOnDeliveryCheckOut(input);
        }
    }

    private void getCustomerAddress(AddressModel addressModel, int position) {
        createAccessToken();
        firstName = addressModel.getFirstName();
        lastName = addressModel.getLastName();

        phone = addressModel.getPhone();
        city = addressModel.getCity();
        country = addressModel.getCountry();
        zip = "12345";
        province = addressModel.getProvince();
        address1 = addressModel.getAddress1();
        address2 = addressModel.getAddress2();



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
                    Log.d(TAG, "accessToken: "+accessToken.toString());
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
    private void getSavedEmailAndPassword() {
        strEmail = sharedPreferences.getString("email", null);
        strPassword = sharedPreferences.getString("password", null);
    }
    private void queryUpdateEmail(ID checkoutId) {


        Storefront.MutationQuery mutationQuery = Storefront.mutation(mutation -> mutation
                .checkoutEmailUpdate(checkoutId,
                        strEmail,
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
        Log.d(TAG, "lastName: " + lastName);
        Log.d(TAG, "phone: " + phone);
        Log.d(TAG, "city: " + city);
        Log.d(TAG, "country: " + country);
        Log.d(TAG, "zip: " + zip);
        Log.d(TAG, "province: " + province);
        Log.d(TAG, "address1: " + address1);
        Log.d(TAG, "address2: " + address2);

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


                Intent Getintent = new Intent(SelectAddressActivity.this, PaymentWebViewActivity.class);
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
    protected void onResume() {
        super.onResume();
        setArDefaultLocale(this);
        overridePendingTransition(0, 0); // remove activity default transition
    }

    @Override
    protected void onPause() {
        super.onPause();
         dialog.dismiss();

    }
}