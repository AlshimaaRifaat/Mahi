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
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asksira.loopingviewpager.LoopingViewPager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.activities.CollectionProductsActivity;
import com.mahitab.ecommerce.activities.HomeActivity;
import com.mahitab.ecommerce.activities.ProductDetailsActivity;
import com.mahitab.ecommerce.activities.SearchResultActivity;
import com.mahitab.ecommerce.adapters.BannerAdapter;
import com.mahitab.ecommerce.adapters.CollectionProductsAdapter;
import com.mahitab.ecommerce.adapters.CollectionsAdapter;
import com.mahitab.ecommerce.adapters.ImageSliderAdapter;
import com.mahitab.ecommerce.adapters.ProductAdapter;
import com.mahitab.ecommerce.managers.DataManager;
import com.mahitab.ecommerce.models.BannerModel;
import com.mahitab.ecommerce.models.CollectionModel;
import com.mahitab.ecommerce.models.ImageSliderModel;
import com.mahitab.ecommerce.models.ProductModel;
import com.mahitab.ecommerce.models.SelectedOptions;
import com.rd.PageIndicatorView;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment implements BannerAdapter.BannerClickListener,
        HomeActivity.CollectionLoadListener,CollectionsAdapter.CollectionClickListener {

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
    ImageView icSearch;


    public HomeFragment() {
        // Required empty public constructor
    }
View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);

        icSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           Intent intent =new Intent(requireContext(), SearchResultActivity.class);
           startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



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
        collectionsAdapter.setOnCollectionClickListener(this);


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
        FirebaseDatabase.getInstance().getReference("Banners").child(banner.getId()).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Long currentNumberOfClicks=(Long) currentData.child("numberOfClicks").getValue();
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
                    Log.e(TAG,"Firebase counter increment failed.");
                } else {
                    Log.e(TAG,"Firebase counter increment succeeded.");
                }
            }
        });
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
        icSearch=view.findViewById(R.id.icSearch);

    }

    private void getBanners() {
        FirebaseDatabase.getInstance().getReference("Banners")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<BannerModel> banners = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            BannerModel banner = snapshot.getValue(BannerModel.class);
                            banners.add(banner);
                        }
                        bannerAdapter.setBannerList(banners);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "onCancelled: " + error.getMessage());
                    }
                });
    }

    private void getSliderImages() {
        sliderImages.add(new ImageSliderModel(" خصومات حتي 60%\n" + "علي مستلزمات الخياطة", "https://souqcms.s3-eu-west-1.amazonaws.com/cms/boxes/img/desktop/L_1602409788_GW-MB-BestDeals-ar.png"));
        sliderImages.add(new ImageSliderModel(" خصومات حتي 60%\n" + "علي مستلزمات الخياطة", "https://souqcms.s3-eu-west-1.amazonaws.com/cms/boxes/img/desktop/L_1602409788_GW-MB-Bundles-ar.png"));
        sliderAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCollectionClick(CollectionModel collection) {
        Intent intent = new Intent(getContext(), CollectionProductsActivity.class);
        intent.putExtra("collectionId", collection.getID().toString());
        startActivity(intent);
    }

}