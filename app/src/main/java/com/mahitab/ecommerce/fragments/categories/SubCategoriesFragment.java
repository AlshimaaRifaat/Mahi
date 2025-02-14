package com.mahitab.ecommerce.fragments.categories;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.activities.CollectionProductsActivity;
import com.mahitab.ecommerce.activities.HomeActivity;
import com.mahitab.ecommerce.activities.ProductDetailsActivity;
import com.mahitab.ecommerce.adapters.BannerAdapter;
import com.mahitab.ecommerce.adapters.ColorAdapter;
import com.mahitab.ecommerce.adapters.ProductAdapter;
import com.mahitab.ecommerce.adapters.ShapeAdapter;
import com.mahitab.ecommerce.managers.DataManager;
import com.mahitab.ecommerce.managers.FirebaseManager;
import com.mahitab.ecommerce.models.BannerModel;
import com.mahitab.ecommerce.models.CategoryModel;
import com.mahitab.ecommerce.models.CollectionModel;
import com.mahitab.ecommerce.models.ProductModel;
import com.mahitab.ecommerce.models.ShapeModel;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SubCategoriesFragment extends Fragment implements BannerAdapter.BannerClickListener, ProductAdapter.ProductClickListener {
    private static final String TAG = "SubCategoriesFragment";

    private CardView cvColors;
    private RecyclerView rvColors;

    private RecyclerView rvBanners;
    private BannerAdapter bannerAdapter;

    private CardView cvShape;
    private RecyclerView rvShapes;

    private RecyclerView rvCollectionProducts;
    private ArrayList<ProductModel> collectionProducts;
    private ProductAdapter productAdapter;
    private TextView tSeeAll;
    private String targetId;

    public SubCategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sub_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        DataManager.getInstance().setClientManager(requireContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getContext() != null && isAdded()) {
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getExtras() != null) {
                        CategoryModel selectedCategory = intent.getExtras().getParcelable("category");

                        if (isAdded() && selectedCategory != null) {

                            if (selectedCategory.getId() != null) {
                                String target = "gid://shopify/Collection/" + selectedCategory.getId();
                                targetId = Base64.encodeToString(target.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
                                targetId = targetId.trim(); //remove spaces from end of string
                                CollectionModel collection = DataManager.getInstance().getCollectionByID(targetId);


                                if (collection == null) {
                                    if (collectionProducts != null)
                                        collectionProducts.clear();
                                    if (productAdapter != null)
                                        productAdapter.notifyDataSetChanged();
                                } else
                                    Log.e(TAG, "onReceive: " + collection.getTitle());
                                if (collection != null) {
                                    if (((HomeActivity) requireActivity()).getSupportActionBar() != null) {
                                        ActionBar actionBar = ((HomeActivity) requireActivity()).getSupportActionBar();
                                        if (actionBar != null)
                                            actionBar.setTitle(collection.getTitle());
                                    }

                                    collectionProducts = collection.getPreviewProducts();
                                    rvCollectionProducts.setHasFixedSize(true);
                                    rvCollectionProducts.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
                                    productAdapter = new ProductAdapter(getContext(), collectionProducts);
                                    rvCollectionProducts.setAdapter(productAdapter);
                                    productAdapter.setProductClickListener(SubCategoriesFragment.this);
                                }


                                tSeeAll.setOnClickListener(v -> {
                                    Log.d(TAG, "onClick: " + "morre");
                                    if (targetId != null) {
                                        Intent intent1 = new Intent(getContext(), CollectionProductsActivity.class);
                                        Log.d(TAG, "onClick: " + targetId);
                                        intent1.putExtra("collectionId", targetId);
                                        startActivity(intent1);
                                    }
                                });
                            }

                            if (selectedCategory.getBanners() != null) {
                                DisplayMetrics displaymetrics = Resources.getSystem().getDisplayMetrics();

                                int subFragmentWidthPixels = (int) (displaymetrics.widthPixels / 1.2);

                                rvBanners.setHasFixedSize(true);
                                rvBanners.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                                bannerAdapter = new BannerAdapter(subFragmentWidthPixels);
                                rvBanners.setAdapter(bannerAdapter);
                                bannerAdapter.setBannerClickListener(SubCategoriesFragment.this);
                                bannerAdapter.setBannerList(selectedCategory.getBanners());
                            }

                            if (selectedCategory.isHasColor())
                                displayColors();
                            else {
                                cvColors.setVisibility(View.GONE);
                                rvColors.setVisibility(View.GONE);
                            }
                            if (selectedCategory.isHasShape())
                                displayShapes();
                            else {
                                cvShape.setVisibility(View.GONE);
                                rvShapes.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }, new IntentFilter("mainCategoryAdapter"));


        }
    }

    @Override
    public void onBannerClick(BannerModel banner) {
        FirebaseManager.incrementBannerNoOfClicks(banner.getReference());
        if (!banner.getId().isEmpty()) {
            String type;
            Intent intent;
            if (banner.getType().startsWith("p")) {
                type = "Product";
                String target = "gid://shopify/" + type + "/" + banner.getId();
                String targetId = Base64.encodeToString(target.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
                targetId = targetId.trim(); //remove spaces from end of string
                intent = new Intent(getContext(), ProductDetailsActivity.class);
                intent.putExtra("productId", targetId);
                Log.e(TAG, "onBannerClick: " + targetId);
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

    private void initView(View view) {
        cvColors = view.findViewById(R.id.cvColors_SubCategoriesFragment);
        rvColors = view.findViewById(R.id.rvColors_SubCategoriesFragment);
        rvBanners = view.findViewById(R.id.rvBanners_SubCategoriesFragment);
        cvShape = view.findViewById(R.id.cvShape_SubCategoriesFragment);
        rvShapes = view.findViewById(R.id.rvShapes_SubCategoriesFragment);
        rvCollectionProducts = view.findViewById(R.id.rvCollectionProducts_SubCategoriesFragment);
        tSeeAll = view.findViewById(R.id.tSeeAll);
    }

    private void displayColors() {
        cvColors.setVisibility(View.VISIBLE);
        rvColors.setVisibility(View.VISIBLE);
        List<String> colors = Arrays.asList(getResources().getStringArray(R.array.colors));
        rvColors.setHasFixedSize(true);
        rvColors.setLayoutManager(new GridLayoutManager(getContext(), 5, LinearLayoutManager.VERTICAL, false));
        ColorAdapter colorAdapter = new ColorAdapter(colors);
        rvColors.setAdapter(colorAdapter);
    }

    private void displayShapes() {
        cvShape.setVisibility(View.VISIBLE);
        rvShapes.setVisibility(View.VISIBLE);
        List<ShapeModel> shapes = new ArrayList<>();
        shapes.add(new ShapeModel("1", "", "سادة"));
        shapes.add(new ShapeModel("2", "", "مقلم"));
        shapes.add(new ShapeModel("3", "", "منقط"));
        shapes.add(new ShapeModel("4", "", "كاروه"));
        shapes.add(new ShapeModel("5", "", "مشجر"));
        shapes.add(new ShapeModel("7", "", "اشكال اخري"));
        rvShapes.setHasFixedSize(true);
        rvShapes.setLayoutManager(new GridLayoutManager(getContext(), 4, LinearLayoutManager.VERTICAL, false));
        ShapeAdapter shapeAdapter = new ShapeAdapter(shapes);
        rvShapes.setAdapter(shapeAdapter);
    }

    @Override
    public void onProductClick(String productId) {
        Intent intent = new Intent(getContext(), ProductDetailsActivity.class);
        intent.putExtra("productId", productId);
        startActivity(intent);
    }

    @Override
    public void onSearchFinished(int resultSize) {

    }
}