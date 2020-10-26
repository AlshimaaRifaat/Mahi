package com.mahitab.ecommerce.fragments;

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
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.activities.CollectionProductsActivity;
import com.mahitab.ecommerce.activities.HomeActivity;
import com.mahitab.ecommerce.activities.ProductDetailsActivity;
import com.mahitab.ecommerce.adapters.BannerAdapter;
import com.mahitab.ecommerce.adapters.CollectionsAdapter;
import com.mahitab.ecommerce.adapters.ImageSliderAdapter;
import com.mahitab.ecommerce.models.BannerList;
import com.mahitab.ecommerce.models.BannerModel;
import com.mahitab.ecommerce.models.CollectionModel;
import com.mahitab.ecommerce.models.ImageSliderModel;
import com.mahitab.ecommerce.utils.OlgorClient;
import com.rd.PageIndicatorView;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements BannerAdapter.BannerClickListener, HomeActivity.CollectionLoadListener {

    private static final String TAG = "HomeFragment";
    private Toolbar toolbar;

    private RecyclerView rvBanners;
    private BannerAdapter bannerAdapter;

    private LoopingViewPager lvpImageSlider;
    private PageIndicatorView indicatorView;
    private List<ImageSliderModel> sliderImages;
    private ImageSliderAdapter sliderAdapter;

    private RecyclerView rvCollectionProducts;
    private CollectionsAdapter collectionsAdapter;

    public HomeFragment() {
        // Required empty public constructor
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
        ((HomeActivity) requireActivity()).setSupportActionBar(toolbar);
        Objects.requireNonNull(((HomeActivity) requireActivity()).getSupportActionBar()).setTitle(getResources().getString(R.string.home));
        setHasOptionsMenu(true);

        sliderImages = new ArrayList<>();

        getBanners();

        DisplayMetrics displaymetrics = Resources.getSystem().getDisplayMetrics();

        rvBanners.setHasFixedSize(true);
        rvBanners.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        bannerAdapter = new BannerAdapter(displaymetrics.widthPixels);
        rvBanners.setAdapter(bannerAdapter);
        bannerAdapter.setBannerClickListener(HomeFragment.this);

        sliderAdapter = new ImageSliderAdapter(getContext(), sliderImages, true);
        getSliderImages();
        lvpImageSlider.setAdapter(sliderAdapter);
        //Tell the IndicatorView that how many indicators should it display:
        indicatorView.setCount(lvpImageSlider.getIndicatorCount());
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

        HomeActivity.setCollectionLoadListener(this);
        
        rvCollectionProducts.setHasFixedSize(true);
        rvCollectionProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        collectionsAdapter = new CollectionsAdapter(getContext());
        rvCollectionProducts.setAdapter(collectionsAdapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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
    }

    @Override
    public void onCollectionLoaded(List<CollectionModel> collections) {
        collectionsAdapter.setCollections(collections);
    }

    private void initView(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        rvBanners = view.findViewById(R.id.rvBanners);
        lvpImageSlider = view.findViewById(R.id.viewPager);
        indicatorView = view.findViewById(R.id.pageIndicatorView);
        rvCollectionProducts = view.findViewById(R.id.rvCollectionProducts);
    }

    private void getBanners() {
        OlgorClient.getInstance().getApi().getBanners().enqueue(new Callback<BannerList>() {
            @Override
            public void onResponse(@NonNull Call<BannerList> call, @NonNull Response<BannerList> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BannerModel> banners = response.body().getBanners();
                    bannerAdapter.setBannerList(banners);
                }
            }

            @Override
            public void onFailure(@NonNull Call<BannerList> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void getSliderImages() {
        sliderImages.add(new ImageSliderModel(" خصومات حتي 60%\n" + "علي مستلزمات الخياطة", "https://souqcms.s3-eu-west-1.amazonaws.com/cms/boxes/img/desktop/L_1602409788_GW-MB-BestDeals-ar.png"));
        sliderImages.add(new ImageSliderModel(" خصومات حتي 60%\n" + "علي مستلزمات الخياطة", "https://souqcms.s3-eu-west-1.amazonaws.com/cms/boxes/img/desktop/L_1602409788_GW-MB-Bundles-ar.png"));
        sliderAdapter.notifyDataSetChanged();
    }
}