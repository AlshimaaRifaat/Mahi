package com.mahitab.ecommerce.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.FirebaseDatabase;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.managers.DataManager;
import com.mahitab.ecommerce.managers.GraphClientManager;
import com.mahitab.ecommerce.models.ProductModel;
import com.mahitab.ecommerce.models.ProductReviewModel;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.Storefront;

import static com.mahitab.ecommerce.utils.CommonUtils.setArDefaultLocale;

public class ReviewActivity extends AppCompatActivity {

    private static final String TAG = "ReviewActivity";

    private RatingBar rbRating;
    private TextInputLayout tilMessage;
    private EditText etMessage;
    private Button btnSave;

    private String productId;

    private Storefront.Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setArDefaultLocale(this);
        setContentView(R.layout.activity_review);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.add_review));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initView();
        SharedPreferences defaultPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        if (getIntent().getExtras() != null) {
            productId = getIntent().getExtras().getString("productId");
            byte[] decodedBytes = android.util.Base64.decode(productId, android.util.Base64.DEFAULT);
            String decodeProductId = new String(decodedBytes).split("/")[4];

            String accessToken = defaultPreferences.getString("token", null);
            if (accessToken != null)
                fetchCustomerQuery(accessToken);

            ProductModel product = DataManager.getInstance().getProductByID(productId);

            btnSave.setOnClickListener(v -> saveProductReview(decodeProductId, customer.getEmail(), new ProductReviewModel(productId, customer.getFirstName(), customer.getLastName(), customer.getEmail().toLowerCase(),product.getTitle(), etMessage.getText().toString(), rbRating.getRating())));
        }
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
        tilMessage = findViewById(R.id.tilMessage_ReviewActivity);
        etMessage = findViewById(R.id.etMessage_ReviewActivity);
        btnSave = findViewById(R.id.btnSave_ReviewActivity);
        rbRating = findViewById(R.id.rbRating_ReviewActivity);
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
                    customer = response.data().getCustomer();
                    Log.e(TAG, "onResponse: getDisplayName" + customer.getDisplayName() + " getFirstName " + customer.getFirstName() + " getLastName" + customer.getLastName());
                } else Log.e(TAG, "onResponse: here");
            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                Log.e(TAG, "Failed to execute query", error);
            }
        });
    }


    private void saveProductReview(String productId, String userEmail, ProductReviewModel productReview) {
        String email = userEmail
                .replace(".", "*")
                .replace("#", "^")
                .replace("$", "?")
                .replace("[", "!")
                .replace("]", "%");
        FirebaseDatabase.getInstance()
                .getReference("ProductReviews")
                .child(productId)
                .child(email)
                .setValue(productReview)
                .addOnSuccessListener(aVoid -> onBackPressed());
    }
}