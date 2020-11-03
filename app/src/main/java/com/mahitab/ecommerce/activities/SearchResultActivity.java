package com.mahitab.ecommerce.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.adapters.ProductAdapter;
import com.mahitab.ecommerce.managers.DataManager;
import com.mahitab.ecommerce.managers.interfaces.BaseCallback;
import com.mahitab.ecommerce.models.CollectionModel;
import com.mahitab.ecommerce.models.ProductModel;
import com.mahitab.ecommerce.models.SelectedOptions;

import java.util.ArrayList;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        initView();
        setSupportActionBar(toolbar);

        DataManager.getInstance().setClientManager(this);
        getAllProductsList();

        selectedOptions = new SelectedOptions();


    }




    private void initView() {
        rvProducts=findViewById( R.id.rvProducts );
        toolbar = findViewById(R.id.toolbar);
    }

    private void getAllProductsList() {
        DataManager.getInstance().allCollections(new BaseCallback() {
            @Override
            public void onResponse(int status) {
                if (status == 200) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(x==1){
                                searchResultList=DataManager.getInstance().getAllProducts();
                                Log.d(TAG, "x1: "+searchResultList.get(0).getTitle());
                            }else {
                                allCollectionList = DataManager.getInstance().getAllCollections();
                                productList=new ArrayList<>();
                                for (int i=0;i<allCollectionList.size();i++) {
                                    productList.add(allCollectionList.get(i).getPreviewProducts().get(i));
                                }

                                if(productList!=null)
                                    searchResultList = productList;
                            }
                            productAdapter = new ProductAdapter(SearchResultActivity.this,searchResultList);
                            selectedOptions.addObserver(productAdapter);
                            rvProducts.setLayoutManager( new GridLayoutManager(SearchResultActivity.this,2 ) );
                            rvProducts.setHasFixedSize(true);
                            rvProducts.setAdapter( productAdapter );
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        searchMenuItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) searchMenuItem.getActionView();
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
    }

    private void getSearchResult() {
        searchResultList=DataManager.getInstance().getAllProducts();
        productAdapter = new ProductAdapter(this,searchResultList);
        selectedOptions.addObserver(productAdapter);
        rvProducts.setLayoutManager( new GridLayoutManager(this,2 ) );
        rvProducts.setHasFixedSize(true);
        rvProducts.setAdapter( productAdapter );
    }


}