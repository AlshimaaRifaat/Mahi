package com.mahitab.ecommerce.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.adapters.ProductAdapter;
import com.mahitab.ecommerce.managers.DataManager;
import com.mahitab.ecommerce.managers.interfaces.BaseCallback;
import com.mahitab.ecommerce.models.CollectionModel;
import com.mahitab.ecommerce.models.ProductModel;
import com.mahitab.ecommerce.models.SelectedOptions;
import com.mahitab.ecommerce.search.ColorSuggestion;
import com.mahitab.ecommerce.search.DataHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static com.mahitab.ecommerce.utils.CommonUtils.setArDefaultLocale;


public class SearchResultActivity extends AppCompatActivity implements ProductAdapter.ProductClickListener {
    private static final String TAG ="SearchResultActivity" ;
    SelectedOptions selectedOptions;
    private MenuItem searchMenuItem = null;
    private ImageView searchCloseIcon,imgCart;
    private ArrayList<ProductModel> searchResultList = new ArrayList<>();
    private ArrayList<ProductModel> productList  = new ArrayList<>();
    private ProductAdapter productAdapter;
    private Toolbar toolbar;
    RecyclerView rvProducts;
    public static int x=0;
    ArrayList<CollectionModel> allCollectionList=  new ArrayList<>();
    public static ArrayList<ColorSuggestion> recentlySearchedList;
    private SharedPreferences defaultPreferences;
    FloatingSearchView mSearchView;
    public static final long FIND_SUGGESTION_SIMULATED_DELAY = 250;
    private String mLastQuery = "";
    SharedPreferences.Editor shEditor;

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

        shEditor = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE).edit();
        defaultPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        if (defaultPreferences.getString("recentlySearchedList", null) == null)
            recentlySearchedList = new ArrayList<>();
        else
            recentlySearchedList = new Gson().fromJson(defaultPreferences.getString("recentlySearchedList", null), new TypeToken<List<ColorSuggestion>>() {
            }.getType());

        Log.d(TAG, "onCreate: "+recentlySearchedList.toString());



        selectedOptions = new SelectedOptions();
        setupFloatingSearch();




    }

    private void setupFloatingSearch() {
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                Log.e("Text","Change");
                //get suggestions based on newQuery

                //pass them on to the search view


                if(newQuery!=null) {


                    getSearchResult();


                    selectedOptions.setSearchCriteria(newQuery);


                }

             // will uncommit  Log.d(TAG, "list: " + recentlySearchedList.toString());


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

                // will uncommint Log.d(TAG, "onSearchTextChanged()");
            }

        });


        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {

                ColorSuggestion colorSuggestion = (ColorSuggestion) searchSuggestion;
                Log.e("SuggestionClicked","Clicked");

                // will uncommint  Log.d(TAG, "onSuggestionClicked: "+colorSuggestion.getBody().toString());
                DataHelper.findColors(SearchResultActivity.this, colorSuggestion.getBody(),
                        new DataHelper.OnFindColorsListener() {

                            @Override
                            public void onResults(ArrayList<ProductModel> searchResultList) {
                                //show search results
                                getSearchResult();
                                selectedOptions.setSearchCriteria(colorSuggestion.getBody());
                                mSearchView.clearSuggestions();
                                mSearchView.setSearchFocusable(false);
                            }
                        });
               // will uncommint Log.d(TAG, "onSuggestionClicked()");
                mLastQuery = searchSuggestion.getBody();
            }

            @Override
            public void onSearchAction(String query) {
                mLastQuery = query;
                Log.e("SearchAction","Search");

                DataHelper.findColors(SearchResultActivity.this, query,
                        new DataHelper.OnFindColorsListener() {

                            @Override
                            public void onResults(ArrayList<ProductModel> searchResultList) {
                                //show search results
                                x = 1;
                                getSearchResult();
                                //getSearchResult();
                                selectedOptions.setSearchCriteria(mLastQuery.toString());

                                /*Start Check If Has Same Value*/
                                boolean result = true ;
                                for (int x=0;x<recentlySearchedList.size();x++)
                                {

                                    if(recentlySearchedList.get(x).getBody().equals(query))
                                    {
                                        result=false;
                                    }
                                }

                                if (result==true)
                                {
                                    recentlySearchedList.add(new ColorSuggestion(mLastQuery));
                                }
                                if (!recentlySearchedList.isEmpty())
                                {
                                   if(result==true)
                                   {
                                       Gson gson = new Gson();
                                       String json = gson.toJson(recentlySearchedList);
                                       shEditor.remove("recentlySearchedList").apply();
                                       shEditor.putString("recentlySearchedList", json);
                                       shEditor.apply();
                                   }
                                }
                               /*End Check If Has Same Value*/
                            }
                        });
                Log.d(TAG, "onSearchAction()");
            }
        });

        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {

                //show suggestions when search bar gains focus (typically history suggestions)
                mSearchView.swapSuggestions(DataHelper.getHistory(defaultPreferences,SearchResultActivity.this, 6));

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

/*
// Shymaa Code
        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                //just print action
               if(item.getItemId()==R.id.action_search){
                   x = 1;
                   Log.e("Action Menu","Selected");

                   // wiil un commint   Log.d(TAG, "query: "+mSearchView.getQuery());
                   getSearchResult();
                   selectedOptions.setSearchCriteria(mSearchView.getQuery());
                   mSearchView.clearSuggestions();
                   mSearchView.setSearchFocusable(false);



               }


            }
        });
*/

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
        rvProducts=findViewById( R.id.rvProducts );
        toolbar = findViewById(R.id.toolbar);
        mSearchView= findViewById(R.id.floating_search_view);
        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView.setShowSearchKey(true);
            }
        });
    }

    private void getSearchResult() {

        searchResultList=DataManager.getInstance().getAllProducts();
        productAdapter = new ProductAdapter(this,searchResultList);
        productAdapter.setProductClickListener(this::onProductClick);
        selectedOptions.addObserver(productAdapter);
        rvProducts.setLayoutManager( new GridLayoutManager(this,2 ) );
        rvProducts.setHasFixedSize(true);
        rvProducts.setAdapter( productAdapter );
    }


    @Override
    public void onProductClick(String productId) {
        Intent intent = new Intent(getApplicationContext(), ProductDetailsActivity.class);
        intent.putExtra("productId", productId);
        startActivity(intent);
    }
}