package com.mahitab.ecommerce.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.adapters.AddressAdapter;
import com.mahitab.ecommerce.adapters.MyOrdersAdapter;
import com.mahitab.ecommerce.managers.DataManager;
import com.mahitab.ecommerce.managers.DataManagerHelper;
import com.mahitab.ecommerce.managers.GraphClientManager;
import com.mahitab.ecommerce.managers.interfaces.BaseCallback;
import com.mahitab.ecommerce.models.MyOrdersModel;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.ID;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

import static com.mahitab.ecommerce.utils.CommonUtils.setArDefaultLocale;

public class MyOrdersActivity extends AppCompatActivity implements MyOrdersAdapter.MyOrderItemInterface {
    private static final String TAG = "MyOrdersActivity";
    ArrayList<MyOrdersModel> myOrdersModelArrayList=null;
    MyOrdersAdapter myOrdersAdapter;
    RecyclerView rvMyOrders;
    SharedPreferences sharedPreferences;
    String accessToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        init();
        DataManager.getInstance().setClientManager(MyOrdersActivity.this);
        myOrdersModelArrayList=new ArrayList<>();
        getSavedAccessToken();


    }

    @Override
    protected void onResume() {
        super.onResume();
        setArDefaultLocale(this);
        overridePendingTransition(0, 0); // remove activity default transition
    }

    private void getSavedAccessToken() {
        sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        accessToken = sharedPreferences.getString("token", null);
        Log.d(TAG, "getSavedAccessToken: " + accessToken);
        queryMyOrders(accessToken);
    }

    private void queryMyOrders(String accessToken) {
        Storefront.QueryRootQuery query = Storefront.query(root -> root
                .customer(accessToken, customer -> customer
                        .orders(arg -> arg.first(200), connection -> connection
                                .edges(edge -> edge
                                        .node(node -> node
                                                .orderNumber()
                                                .totalPrice()
                                                .statusUrl()
                                                .customerUrl()
                                                .processedAt()

                                        )
                                )
                        )
                )
        );
        getMyOrdersList(query, new BaseCallback() {
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

    private void getMyOrdersList(Storefront.QueryRootQuery query,BaseCallback callback) {
        GraphClientManager.mClient.queryGraph(query).enqueue(new GraphCall.Callback<Storefront.QueryRoot>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {

                if (!response.hasErrors()) {
                    Storefront.OrderConnection connection = response.data().getCustomer().getOrders();
                    for (Storefront.OrderEdge edge : connection.getEdges()) {

                        MyOrdersModel newMyOrdersModel = new MyOrdersModel(edge);
                        Log.d(TAG, "id: "+newMyOrdersModel.getmID().toString());
                        DataManagerHelper.getInstance().fetchMyOrders().put(newMyOrdersModel.getmID().toString(), newMyOrdersModel);



                    }
                    for (int i=0;i<DataManager.getInstance().getMyOrders().size();i++) {
                        Log.d(TAG, "num: " + DataManager.getInstance().getMyOrders().get(i).getOrderNumber().toString() + " date: " + DataManager.getInstance().getMyOrders().get(0).getProcessedAt().toString());
                    }
                    myOrdersModelArrayList=DataManager.getInstance().getMyOrders();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: "+"success");

                            myOrdersModelArrayList.sort((o1, o2) -> o2.getOrderNumber().compareTo(o1.getOrderNumber()));
                            myOrdersAdapter = new MyOrdersAdapter(MyOrdersActivity.this,myOrdersModelArrayList);
                            myOrdersAdapter.onClickItemMyOrder(MyOrdersActivity.this::goToMyOrderDetails);
                            rvMyOrders.setLayoutManager(new LinearLayoutManager(MyOrdersActivity.this));
                            rvMyOrders.setHasFixedSize(true);
                            rvMyOrders.setAdapter(myOrdersAdapter);
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


    private void init() {
        rvMyOrders=findViewById(R.id.rvMyOrders);
    }


    @Override
    public void goToMyOrderDetails(MyOrdersModel myOrdersModel, int Position) {
        Intent intent=new Intent(this,MyOrderDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("myOrdersModel", (Serializable) myOrdersModel);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}