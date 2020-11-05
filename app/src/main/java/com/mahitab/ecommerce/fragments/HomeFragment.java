package com.mahitab.ecommerce.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asksira.loopingviewpager.LoopingViewPager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.activities.CollectionProductsActivity;
import com.mahitab.ecommerce.activities.HomeActivity;
import com.mahitab.ecommerce.activities.ProductDetailsActivity;
import com.mahitab.ecommerce.activities.SearchResultActivity;
import com.mahitab.ecommerce.adapters.BannerAdapter;
import com.mahitab.ecommerce.adapters.CollectionsAdapter;
import com.mahitab.ecommerce.adapters.SliderBannersAdapter;
import com.mahitab.ecommerce.models.BannerModel;
import com.mahitab.ecommerce.models.CollectionModel;
import com.rd.PageIndicatorView;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements BannerAdapter.BannerClickListener,
        HomeActivity.HomePageLoadedListener, CollectionsAdapter.CollectionClickListener, SliderBannersAdapter.SliderBannerClickListener {

    private static final String TAG = "HomeFragment";

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
        if (item.getItemId() == R.id.action_search)
            startActivity(new Intent(getContext(), SearchResultActivity.class));
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBannerClick(BannerModel banner) {
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
        incrementBannerNoOfClicks(banner.getReference());
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
        incrementBannerNoOfClicks(banner.getReference());
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

    private void incrementBannerNoOfClicks(DatabaseReference bannerReference) {
        bannerReference.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Long currentNumberOfClicks = (Long) currentData.child("numberOfClicks").getValue();
                if (currentNumberOfClicks == null) {
                    currentData.child("numberOfClicks").setValue(1);
                } else {
                    currentData.child("numberOfClicks").setValue(currentNumberOfClicks + 1);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (error != null) {
                    Log.e(TAG, "Firebase counter increment failed.");
                } else {
                    Log.e(TAG, "Firebase counter increment succeeded.");
                }
            }
        });
    }
}