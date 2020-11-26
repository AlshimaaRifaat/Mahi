package com.mahitab.ecommerce.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.adapters.ProductAdapter;
import com.mahitab.ecommerce.fragments.HomeFragment;
import com.mahitab.ecommerce.managers.DataManager;
import com.mahitab.ecommerce.managers.DataManagerHelper;
import com.mahitab.ecommerce.managers.interfaces.BaseCallback;
import com.mahitab.ecommerce.models.CollectionModel;
import com.mahitab.ecommerce.models.ProductModel;
import com.mahitab.ecommerce.models.SelectedOptions;
import com.mahitab.ecommerce.search.ColorSuggestion;
import com.mahitab.ecommerce.search.DataHelper;
import com.shopify.graphql.support.ID;

import java.util.ArrayList;
import java.util.List;

import static com.mahitab.ecommerce.utils.CommonUtils.setArDefaultLocale;



public class SearchResultActivity extends AppCompatActivity implements ProductAdapter.ProductClickListener {
    private static final String TAG = "SearchResultActivity";
    SelectedOptions selectedOptions;
    private MenuItem searchMenuItem = null;
    private ImageView searchCloseIcon, imgCart;

    private ArrayList<ProductModel> searchResultList = null;
    private ArrayList<ProductModel> productList = null;
    private ProductAdapter productAdapter;

    private Toolbar toolbar;
    RecyclerView rvProducts;
    public static int x = 0;
    ArrayList<CollectionModel> allCollectionList = null;
    public static ArrayList<ColorSuggestion> recentlySearchedList;
    private SharedPreferences defaultPreferences;
    FloatingSearchView mSearchView;
    public static final long FIND_SUGGESTION_SIMULATED_DELAY = 250;
    private String mLastQuery = "";
    SharedPreferences.Editor shEditor;
    Dialog dialog;
    TextView tNoSearchResult;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        initView();
        setArDefaultLocale(this);
        mSearchView.setSearchFocused(true);
        mSearchView.setSearchFocusable(true);
        //setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        DataManager.getInstance().setClientManager(this);
        dialog=new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.load_dialog);

        shEditor = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE).edit();
        defaultPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        if (defaultPreferences.getString("recentlySearchedList", null) == null)
            recentlySearchedList = new ArrayList<>();
        else
            recentlySearchedList = new Gson().fromJson(defaultPreferences.getString("recentlySearchedList", null), new TypeToken<List<ColorSuggestion>>() {
            }.getType());

        Log.d(TAG, "onCreate: " + recentlySearchedList.toString());


        selectedOptions = new SelectedOptions();

        setupFloatingSearch();


    }

    @Override
    protected void onResume() {
        super.onResume();
        setArDefaultLocale(this);
        overridePendingTransition(0, 0); // remove activity default transition
    }

    private void setupFloatingSearch() {
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery,  String newQuery) {

                //get suggestions based on newQuery

                //pass them on to the search view
                // mSearchView.swapSuggestions(newQuery);

                /*   */

                x = 1;
              /*  if (newQuery != null) {
                   getSearchResult();
                    selectedOptions.setSearchCriteria(newQuery);


                }*/
                Log.d(TAG, "list: " + recentlySearchedList.toString());


                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                } else {

                    //this shows the top left circular progress
                    //you can call it where ever you want, but
                    //it makes sense to do it when loading something in
                    //the background.
                    mSearchView.showProgress();

                    //simulates a query call to a data source
                    //with a new query.
                    DataHelper.findSuggestions(SearchResultActivity.this, newQuery, 5,
                            FIND_SUGGESTION_SIMULATED_DELAY, new DataHelper.OnFindSuggestionsListener() {

                                @Override
                                public void onResults(List<ColorSuggestion> results) {

                                    //this will swap the data and
                                    //render the collapse/expand animations as necessary

                                    mSearchView.swapSuggestions(results);

                                    //let the users know that the background
                                    //process has completed
                                    mSearchView.hideProgress();
                                }
                            });
                }

                Log.d(TAG, "onSearchTextChanged()");
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {

                ColorSuggestion colorSuggestion = (ColorSuggestion) searchSuggestion;
                Log.d(TAG, "onSuggestionClicked: " + colorSuggestion.getBody().toString());
                DataHelper.findColors(SearchResultActivity.this, colorSuggestion.getBody(),
                        new DataHelper.OnFindColorsListener() {

                            @Override
                            public void onResults(ArrayList<ProductModel> searchResultList) {
                                //show search results

                                String query=colorSuggestion.getBody();
                                x = 1;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.show();
                                        HomeFragment.liveData.observe(SearchResultActivity.this, new Observer<ArrayList<ProductModel>>() {
                                            @Override
                                            public void onChanged(ArrayList<ProductModel> productModels) {
                                                if (productModels.size()>0) {
                                                    dialog.dismiss();
                                                    getSearchResult();
                                                    selectedOptions.setSearchCriteria(query);
                                                    mSearchView.clearSuggestions();
                                                    mSearchView.setSearchFocusable(true);
                                                    mSearchView.setCloseSearchOnKeyboardDismiss(false);

                                                }
                                            }
                                        });
                                    }
                                });
                               /* */

                            }

                        });
                Log.d(TAG, "onSuggestionClicked()");

                mLastQuery = searchSuggestion.getBody();
            }

            @Override
            public void onSearchAction(String query) {
                mLastQuery = query;

                DataHelper.findColors(SearchResultActivity.this, query,
                        new DataHelper.OnFindColorsListener() {

                            @Override
                            public void onResults(ArrayList<ProductModel> searchResultList) {
                                //show search results


                                x = 1;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.show();
                                        HomeFragment.liveData.observe(SearchResultActivity.this, new Observer<ArrayList<ProductModel>>() {
                                            @Override
                                            public void onChanged(ArrayList<ProductModel> productModels) {
                                                Log.d(TAG, "onChanged: "+productModels.size());
                                                if (productModels.size()>0) {
                                                    dialog.dismiss();
                                                    getSearchResult();
                                                    selectedOptions.setSearchCriteria(mLastQuery.toString());
                                                }

                                            }
                                        });
                                    }
                                });
                                Log.d(TAG, "onResults: "+mLastQuery);



                                recentlySearchedList.add(new ColorSuggestion(mLastQuery));

                                if (!recentlySearchedList.isEmpty()) {
                                    Gson gson = new Gson();
                                    String json = gson.toJson(recentlySearchedList);
                                    shEditor.remove("recentlySearchedList").apply();
                                    shEditor.putString("recentlySearchedList", json);
                                    shEditor.apply();
                                }
                                Log.d(TAG, "list: " + recentlySearchedList.toString());
                            }

                        });
                Log.d(TAG, "onSearchAction()");
            }
        });


        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {

                //show suggestions when search bar gains focus (typically history suggestions)
                mSearchView.swapSuggestions(DataHelper.getHistory(defaultPreferences, SearchResultActivity.this, 6));

                Log.d(TAG, "onFocus()");

            }

            @Override
            public void onFocusCleared() {

                //set the title of the bar so that when focus is returned a new query begins
                mSearchView.setSearchBarTitle(mLastQuery);

                //you can also set setSearchText(...) to make keep the query there when not focused and when focus returns
                //mSearchView.setSearchText(searchSuggestion.getBody());

                Log.d(TAG, "onFocusCleared()");
            }
        });


        //handle menu clicks the same way as you would
        //in a regular activity

        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                Log.d(TAG, "onActionMenuItemSelected: "+"sucess");
                //just print action
            /*    String query=mSearchView.getQuery();
                x = 1;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.show();
                        HomeFragment.liveData.observe(SearchResultActivity.this, new Observer<ArrayList<ProductModel>>() {
                            @Override
                            public void onChanged(ArrayList<ProductModel> productModels) {
                                if (productModels.size()>0) {
                                    dialog.dismiss();
                                    getSearchResult();
                                    selectedOptions.setSearchCriteria(query);
                                    Log.d(TAG, "onChanged: "+"item selected");
                                    mSearchView.clearSuggestions();

                                }
                            }
                        });
                    }
                });*/






            }
        });

        //use this listener to listen to menu clicks when app:floatingSearch_leftAction="showHome"
        mSearchView.setOnHomeActionClickListener(new FloatingSearchView.OnHomeActionClickListener() {
            @Override
            public void onHomeClicked() {

                Log.d(TAG, "onHomeClicked()");
            }
        });

        /*
         * Here you have access to the left icon and the text of a given suggestion
         * item after as it is bound to the suggestion list. You can utilize this
         * callback to change some properties of the left icon and the text. For example, you
         * can load the left icon images using your favorite image loading library, or change text color.
         *
         *
         * Important:
         * Keep in mind that the suggestion list is a RecyclerView, so views are reused for different
         * items in the list.
         */


    }


    private void initView() {
        rvProducts = findViewById(R.id.rvProducts);
        toolbar = findViewById(R.id.toolbar);
        mSearchView = findViewById(R.id.floating_search_view);
        tNoSearchResult=findViewById(R.id.tNoSearchResult);

    }



    private void getSearchResult() {

        DataManager.getInstance().products("Z2lkOi8vc2hvcGlmeS9Db2xsZWN0aW9uLzIzMDU5MTA3MDM3NQ==",
                new BaseCallback() {
                    @Override
                    public void onResponse(int status) {
                        if(status==RESULT_OK) {
                            productList = DataManager.getInstance().getCollectionByID("Z2lkOi8vc2hvcGlmeS9Db2xsZWN0aW9uLzIzMDU5MTA3MDM3NQ==").getPreviewProducts();

                                Log.d(TAG, "productList: " + productList.size());
                                productAdapter = new ProductAdapter(SearchResultActivity.this, productList);
                            productAdapter.setProductClickListener(SearchResultActivity.this);
                            productAdapter.setSearchFinished(SearchResultActivity.this);
                                selectedOptions.addObserver(productAdapter);

                                rvProducts.setVisibility(View.VISIBLE);
                                tNoSearchResult.setVisibility(View.GONE);
                                rvProducts.setLayoutManager(new GridLayoutManager(SearchResultActivity.this, 2));
                                rvProducts.setHasFixedSize(true);
                                rvProducts.setAdapter(productAdapter);


                        }


                    }

                    @Override
                    public void onFailure(String message) {
                        Log.d(TAG, "onFailure: " + message.toString());
                    }
                });






    }


    @Override
    public void onProductClick(String productId) {
        Intent intent = new Intent(getApplicationContext(), ProductDetailsActivity.class);
        intent.putExtra("productId", productId);
        startActivity(intent);
    }

    @Override
    public void onSearchFinished(int resultSize) {
        dialog.dismiss();
        if (resultSize==0){
            rvProducts.setVisibility(View.GONE);
            tNoSearchResult.setVisibility(View.VISIBLE);
        }else{
            rvProducts.setVisibility(View.VISIBLE);
            tNoSearchResult.setVisibility(View.GONE);
        }
    }
}