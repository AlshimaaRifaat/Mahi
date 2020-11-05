package com.mahitab.ecommerce.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
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
import android.widget.ImageView;
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



public class SearchResultActivity extends AppCompatActivity {
    private static final String TAG ="SearchResultActivity" ;
    SelectedOptions selectedOptions;
    private MenuItem searchMenuItem = null;
    private ImageView searchCloseIcon,imgCart;

    private ArrayList<ProductModel> searchResultList = null;
    private ArrayList<ProductModel> productList = null;
    private ProductAdapter productAdapter;

    private Toolbar toolbar;
    RecyclerView rvProducts;
    public static int x=0;
    ArrayList<CollectionModel> allCollectionList=null;
    public static ArrayList<ColorSuggestion> recentlySearchedList;
    SharedPreferences.Editor shEditor;
    private SharedPreferences defaultPreferences;
    FloatingSearchView mSearchView;
    public static final long FIND_SUGGESTION_SIMULATED_DELAY = 250;
    private String mLastQuery = "";
    String newQuery;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        initView();

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

        getAllProductsList();

        selectedOptions = new SelectedOptions();
        setupFloatingSearch();






    }

    private void setupFloatingSearch() {
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                //get suggestions based on newQuery

                //pass them on to the search view
                // mSearchView.swapSuggestions(newQuery);

                /*   */
                if(newQuery!=null) {
                    getSearchResult(searchResultList);
                    selectedOptions.setSearchCriteria(newQuery);


               /* recentlySearchedList.add(new ColorSuggestion(newQuery));

                if (!recentlySearchedList.isEmpty()) {
                    Gson gson = new Gson();
                    String json = gson.toJson(recentlySearchedList);
                    shEditor.remove("recentlySearchedList").apply();
                    shEditor.putString("recentlySearchedList", json);
                    shEditor.apply();
                }*/
                }
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
                DataHelper.findColors(SearchResultActivity.this, colorSuggestion.getBody(),
                        new DataHelper.OnFindColorsListener() {

                            @Override
                            public void onResults(ArrayList<ProductModel> results) {
                                //show search results


                            }

                        });
                Log.d(TAG, "onSuggestionClicked()");

                mLastQuery = searchSuggestion.getBody();
            }

            @Override
            public void onSearchAction(String query) {
                mLastQuery = query;
                x = 1;
                DataHelper.findColors(SearchResultActivity.this, query,
                        new DataHelper.OnFindColorsListener() {

                            @Override
                            public void onResults(ArrayList<ProductModel> searchResultList) {
                                //show search results
                                Toast.makeText(SearchResultActivity.this, "ok", Toast.LENGTH_SHORT).show();



                                //  Log.d(TAG, "size list: "+searchResultList.size()+"");
                                getSearchResult(searchResultList);
                                selectedOptions.setSearchCriteria(mLastQuery.toString());


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
                mSearchView.swapSuggestions(DataHelper.getHistory(SearchResultActivity.this, 6));

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

          /*      if (item.getItemId() == R.id.action_change_colors) {

                    mIsDarkSearchTheme = true;

                    //demonstrate setting colors for items
                    mSearchView.setBackgroundColor(Color.parseColor("#787878"));
                    mSearchView.setViewTextColor(Color.parseColor("#e9e9e9"));
                    mSearchView.setHintTextColor(Color.parseColor("#e9e9e9"));
                    mSearchView.setActionMenuOverflowColor(Color.parseColor("#e9e9e9"));
                    mSearchView.setMenuItemIconColor(Color.parseColor("#e9e9e9"));
                    mSearchView.setLeftActionIconColor(Color.parseColor("#e9e9e9"));
                    mSearchView.setClearBtnColor(Color.parseColor("#e9e9e9"));
                    mSearchView.setDividerColor(Color.parseColor("#BEBEBE"));
                    mSearchView.setLeftActionIconColor(Color.parseColor("#e9e9e9"));
                } else {

                    //just print action
                    Toast.makeText(getActivity().getApplicationContext(), item.getTitle(),
                            Toast.LENGTH_SHORT).show();
                }*/

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
        rvProducts=findViewById( R.id.rvProducts );
        toolbar = findViewById(R.id.toolbar);
        mSearchView= findViewById(R.id.floating_search_view);
    }

    private void getAllProductsList() {
        DataManager.getInstance().allCollections(new BaseCallback() {
            @Override
            public void onResponse(int status) {
                if (status == 200) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (x == 1) {
                                searchResultList = DataManager.getInstance().getAllProducts();
                                Log.d(TAG, "x1: " + searchResultList.get(0).getTitle());
                            } else {
                                allCollectionList = DataManager.getInstance().getAllCollections();
                                productList = new ArrayList<>();
                                for (int i = 0; i < allCollectionList.size(); i++) {
                                    productList.add(allCollectionList.get(i).getPreviewProducts().get(i));
                                }

                                if (productList != null)
                                    searchResultList = productList;
                            }
                            productAdapter = new ProductAdapter(SearchResultActivity.this, searchResultList);
                            selectedOptions.addObserver(productAdapter);
                            rvProducts.setLayoutManager(new GridLayoutManager(SearchResultActivity.this, 2));
                            rvProducts.setHasFixedSize(true);
                            rvProducts.setAdapter(productAdapter);
                        }

                    });
                } else {
                    this.onFailure("An unknown error has occurred");
                }
            }

            @Override
            public void onFailure(String message) {

            }
        });


    }



    @Override
    protected void onPause() {
        super.onPause();
        if (recentlySearchedList!=null) {
            Gson gson = new Gson();
            String json = gson.toJson(recentlySearchedList);
            shEditor.remove("recentlySearchedList").apply();
            shEditor.putString("recentlySearchedList", json);
            shEditor.apply();
        }else{
            recentlySearchedList = new ArrayList<>();
        }

    }


/* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        searchMenuItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setIconified(false);


        EditText searchEditText = (EditText) searchView.findViewById(R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.primary_color_light));
        searchEditText.setHintTextColor(getResources().getColor(R.color.primary_color_light));

        searchCloseIcon = searchView.findViewById(R.id.search_close_btn);
        searchCloseIcon.setImageResource(R.drawable.ic_clear_black_24dp);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                x=1;
                getSearchResult();
                Log.d(TAG, "onQueryTextSubmit: "+x);
                selectedOptions.setSearchCriteria(query);


                recentlySearchedList.add(query);

                if (!recentlySearchedList.isEmpty()) {
                    Gson gson = new Gson();
                    String json = gson.toJson(recentlySearchedList);
                    shEditor.remove("recentlySearchedList").apply();
                    shEditor.putString("recentlySearchedList", json);
                    shEditor.apply();
                }





                return false;


            }

            @Override
            public boolean onQueryTextChange(String newText) {
                x=1;
                getSearchResult();
                Log.d(TAG, "onQueryTextChange: "+x);
                selectedOptions.setSearchCriteria(newText);
                return true;
            }
        });
        return true;

    }*/






    private void getSearchResult(ArrayList<ProductModel> searchResultList) {

        searchResultList=DataManager.getInstance().getAllProducts();
        Log.d(TAG, "getSearchResult: "+searchResultList.size()+"");
        productAdapter = new ProductAdapter(this,searchResultList);
        selectedOptions.addObserver(productAdapter);
        rvProducts.setLayoutManager( new GridLayoutManager(this,2 ) );
        rvProducts.setHasFixedSize(true);
        rvProducts.setAdapter( productAdapter );
    }


}