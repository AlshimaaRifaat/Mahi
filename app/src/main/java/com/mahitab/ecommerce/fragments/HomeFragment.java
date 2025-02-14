package com.mahitab.ecommerce.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asksira.loopingviewpager.LoopingViewPager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.activities.CollectionProductsActivity;
import com.mahitab.ecommerce.activities.HomeActivity;
import com.mahitab.ecommerce.activities.ProductDetailsActivity;
import com.mahitab.ecommerce.activities.SearchResultActivity;
import com.mahitab.ecommerce.adapters.BannerAdapter;
import com.mahitab.ecommerce.adapters.CollectionsAdapter;
import com.mahitab.ecommerce.adapters.SliderBannersAdapter;
import com.mahitab.ecommerce.managers.DataManager;
import com.mahitab.ecommerce.managers.FirebaseManager;
import com.mahitab.ecommerce.managers.interfaces.BaseCallback;
import com.mahitab.ecommerce.models.BannerModel;
import com.mahitab.ecommerce.models.CartItemQuantity;
import com.mahitab.ecommerce.models.CollectionModel;
import com.mahitab.ecommerce.models.ProductModel;
import com.rd.PageIndicatorView;
import com.shopify.buy3.Storefront;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements BannerAdapter.BannerClickListener,
        HomeActivity.HomePageLoadedListener, CollectionsAdapter.CollectionClickListener, SliderBannersAdapter.SliderBannerClickListener {

    private Toolbar toolbar;
    private RecyclerView rvTopBanners;
    private BannerAdapter topBannerAdapter;
    private RecyclerView rvMidBanners;
    private BannerAdapter midBannerAdapter;
    private RecyclerView rvBottomBanners;
    private BannerAdapter bottomBannerAdapter;

    private LoopingViewPager lvpImageSlider;
    private PageIndicatorView indicatorView;
    private final List<BannerModel> sliderBanners = new ArrayList<>();
    private SliderBannersAdapter sliderAdapter;

    private RecyclerView rvTopCollectionProducts;
    private CollectionsAdapter topCollectionsAdapter;
    private RecyclerView rvMidCollectionProducts;
    private CollectionsAdapter midCollectionsAdapter;
    private RecyclerView rvBottomCollectionProducts;
    private CollectionsAdapter bottomCollectionsAdapter;
    String TAG="HomeFragment";
    ArrayList<ProductModel> allProductList;
    private SharedPreferences defaultPreferences;
    public static MutableLiveData<ArrayList<ProductModel>> liveData = new MutableLiveData<>();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allProductList=new ArrayList<>();
        defaultPreferences = getActivity().getSharedPreferences(requireContext().getPackageName(), Context.MODE_PRIVATE);
        DataManager.getInstance().collectionsAllProducts(new BaseCallback() {
            @Override
            public void onResponse(int status) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        liveData.postValue(DataManager.getInstance().getCollectionByID("Z2lkOi8vc2hvcGlmeS9Db2xsZWN0aW9uLzIzMDU5MTA3MDM3NQ==").getPreviewProducts());


                        // defaultPreferences.edit().putString("allProductList", new Gson().toJson(allProductList)).apply();

                    }
                });

            }

            @Override
            public void onFailure(String message) {
                Log.e(TAG, "onFailure: " );
            }
        });
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        HomeActivity.setHomePageLoadedListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);


        if (getActivity() != null && getActivity() instanceof HomeActivity) {
            ((HomeActivity) getActivity()).setSupportActionBar(toolbar);
            setHasOptionsMenu(true);
        }

        DisplayMetrics displaymetrics = Resources.getSystem().getDisplayMetrics();

        rvTopBanners.setHasFixedSize(true);
        rvTopBanners.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        topBannerAdapter = new BannerAdapter(displaymetrics.widthPixels);
        rvTopBanners.setAdapter(topBannerAdapter);
        topBannerAdapter.setBannerClickListener(HomeFragment.this);


        rvMidBanners.setHasFixedSize(true);
        rvMidBanners.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        midBannerAdapter = new BannerAdapter(displaymetrics.widthPixels);
        rvMidBanners.setAdapter(midBannerAdapter);
        midBannerAdapter.setBannerClickListener(HomeFragment.this);

        rvBottomBanners.setHasFixedSize(true);
        rvBottomBanners.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        bottomBannerAdapter = new BannerAdapter(displaymetrics.widthPixels);
        rvBottomBanners.setAdapter(bottomBannerAdapter);
        bottomBannerAdapter.setBannerClickListener(HomeFragment.this);


        sliderAdapter = new SliderBannersAdapter(getContext(), sliderBanners, true);
        lvpImageSlider.setAdapter(sliderAdapter);
        sliderAdapter.setSliderBannerClickListener(this);

        //Set IndicatorPageChangeListener on LoopingViewPager.
        //When the methods are called, update the Indicator accordingly.
        lvpImageSlider.setIndicatorPageChangeListener(new LoopingViewPager.IndicatorPageChangeListener() {
            @Override
            public void onIndicatorProgress(int selectingPosition, float progress) {
            }

            @Override
            public void onIndicatorPageChange(int newIndicatorPosition) {
                indicatorView.setSelection(newIndicatorPosition);
            }
        });


        rvTopCollectionProducts.setHasFixedSize(true);
        rvTopCollectionProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        topCollectionsAdapter = new CollectionsAdapter(getContext());
        rvTopCollectionProducts.setAdapter(topCollectionsAdapter);
        topCollectionsAdapter.setOnCollectionClickListener(this);

        rvMidCollectionProducts.setHasFixedSize(true);
        rvMidCollectionProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        midCollectionsAdapter = new CollectionsAdapter(getContext());
        rvMidCollectionProducts.setAdapter(midCollectionsAdapter);
        midCollectionsAdapter.setOnCollectionClickListener(this);

        rvBottomCollectionProducts.setHasFixedSize(true);
        rvBottomCollectionProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        bottomCollectionsAdapter = new CollectionsAdapter(getContext());
        rvBottomCollectionProducts.setAdapter(bottomCollectionsAdapter);
        bottomCollectionsAdapter.setOnCollectionClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && isResumed()) {
            toolbar.setTitle(getResources().getString(R.string.home));
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


       /* if (defaultPreferences.getString("allProductList", null) == null)
            allProductList = new ArrayList<>();
        else
            allProductList = new Gson().fromJson(defaultPreferences.getString("allProductList", null), new TypeToken<ArrayList<ProductModel>>() {
            }.getType());*/

        if (item.getItemId() == R.id.action_search) {
            startActivity(new Intent(getContext(), SearchResultActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBannerClick(BannerModel banner) {
        FirebaseManager.incrementBannerNoOfClicks(banner.getReference());
        if(!banner.getId().isEmpty()) {
            String type;
            Intent intent;
            if (banner.getType().startsWith("p")) {
                type = "Product";
                String target = "gid://shopify/" + type + "/" + banner.getId();
                String targetId = Base64.encodeToString(target.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
                targetId = targetId.trim(); //remove spaces from end of string
                intent = new Intent(getContext(), ProductDetailsActivity.class);
                intent.putExtra("productId", targetId);
                startActivity(intent);
            } else if (banner.getType().startsWith("c")) {
                type = "Collection";
                String target = "gid://shopify/" + type + "/" + banner.getId();
                String targetId = Base64.encodeToString(target.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
                targetId = targetId.trim(); //remove spaces from end of string
                intent = new Intent(getContext(), CollectionProductsActivity.class);
                intent.putExtra("collectionId", targetId);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onSliderLoaded(List<BannerModel> sliderList) {
        sliderBanners.clear();
        sliderBanners.addAll(sliderList);
        sliderAdapter.notifyDataSetChanged();
        //Tell the IndicatorView that how many indicators should it display:
        indicatorView.setCount(lvpImageSlider.getIndicatorCount());
    }

    @Override
    public void onTopCollectionLoaded(List<CollectionModel> collections) {
        topCollectionsAdapter.setCollections(collections);
    }

    @Override
    public void onMidCollectionLoaded(List<CollectionModel> collections) {
        midCollectionsAdapter.setCollections(collections);
    }

    @Override
    public void onBottomCollectionLoaded(List<CollectionModel> collections) {
        bottomCollectionsAdapter.setCollections(collections);
    }

    @Override
    public void onTopBannersLoaded(List<BannerModel> banners) {
        topBannerAdapter.setBannerList(banners);
    }

    @Override
    public void onMidBannersLoaded(List<BannerModel> banners) {
        midBannerAdapter.setBannerList(banners);
    }

    @Override
    public void onBottomBannersLoaded(List<BannerModel> banners) {
        bottomBannerAdapter.setBannerList(banners);
    }

    @Override
    public void onSliderBannerClick(BannerModel banner) {
        FirebaseManager.incrementBannerNoOfClicks(banner.getReference());
    }

    private void initView(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        rvTopBanners = view.findViewById(R.id.rvTopBanners_HomeFragment);
        lvpImageSlider = view.findViewById(R.id.viewPager);
        indicatorView = view.findViewById(R.id.pageIndicatorView);
        rvTopCollectionProducts = view.findViewById(R.id.rvTopCollectionProducts_HomeFragment);
        rvMidBanners = view.findViewById(R.id.rvMidBanners_HomeFragment);
        rvMidCollectionProducts = view.findViewById(R.id.rvMidCollectionProducts_HomeFragment);
        rvBottomBanners = view.findViewById(R.id.rvBottomBanners_HomeFragment);
        rvBottomCollectionProducts = view.findViewById(R.id.rvBottomCollectionProducts_HomeFragment);

    }

    @Override
    public void onCollectionClick(CollectionModel collection) {
        Intent intent = new Intent(getContext(), CollectionProductsActivity.class);
        intent.putExtra("collectionId", collection.getID().toString());
        startActivity(intent);
    }

}