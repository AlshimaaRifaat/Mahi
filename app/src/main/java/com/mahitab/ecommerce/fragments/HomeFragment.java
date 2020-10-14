package com.mahitab.ecommerce.fragments;

import android.os.Bundle;
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
import com.mahitab.ecommerce.activities.HomeActivity;
import com.mahitab.ecommerce.adapters.CollectionProductsAdapter;
import com.mahitab.ecommerce.adapters.ImageSliderAdapter;
import com.mahitab.ecommerce.managers.DataManager;
import com.mahitab.ecommerce.managers.interfaces.BaseCallback;
import com.mahitab.ecommerce.models.CollectionModel;
import com.mahitab.ecommerce.models.ImageSliderModel;
import com.rd.PageIndicatorView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private Toolbar toolbar;
    private LoopingViewPager lvpImageSlider;
    private PageIndicatorView indicatorView;
    private List<ImageSliderModel> sliderImages;
    private ImageSliderAdapter sliderAdapter;

    private RecyclerView rvCollectionProducts;
    private List<CollectionModel> collections;
    private CollectionProductsAdapter collectionProductsAdapter;

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

        DataManager.getInstance().setClientManager(getContext()); // init shopify sdk

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


        geCollectionsWithProducts();

        rvCollectionProducts.setHasFixedSize(true);
        rvCollectionProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        collectionProductsAdapter = new CollectionProductsAdapter();
        rvCollectionProducts.setAdapter(collectionProductsAdapter);
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

    private void initView(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        lvpImageSlider = view.findViewById(R.id.viewPager);
        indicatorView = view.findViewById(R.id.pageIndicatorView);
        rvCollectionProducts = view.findViewById(R.id.rvCollectionProducts);
    }

    private void getSliderImages() {
        sliderImages.add(new ImageSliderModel(" خصومات حتي 60%\n" + "علي مستلزمات الخياطة", "https://souqcms.s3-eu-west-1.amazonaws.com/cms/boxes/img/desktop/L_1602409788_GW-MB-BestDeals-ar.png"));
        sliderImages.add(new ImageSliderModel(" خصومات حتي 60%\n" + "علي مستلزمات الخياطة", "https://souqcms.s3-eu-west-1.amazonaws.com/cms/boxes/img/desktop/L_1602409788_GW-MB-Bundles-ar.png"));
        sliderAdapter.notifyDataSetChanged();
    }

    private void geCollectionsWithProducts() {
        DataManager.getInstance().collections(new BaseCallback() {
            @Override
            public void onResponse(int status) {
                if (status == 200) {
                    requireActivity().runOnUiThread(() -> {
                        collections = DataManager.getInstance().getCollections();
                        collections.removeIf(collection -> (collection.getPreviewProducts().size() == 0)); //remove collection if has no products
                        collectionProductsAdapter.setCollections(collections);
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

}