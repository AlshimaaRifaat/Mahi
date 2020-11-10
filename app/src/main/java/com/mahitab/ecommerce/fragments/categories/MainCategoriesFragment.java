package com.mahitab.ecommerce.fragments.categories;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.adapters.MainCategoryAdapter;
import com.mahitab.ecommerce.managers.FirebaseManager;
import com.mahitab.ecommerce.models.CategoryModel;

import java.util.ArrayList;
import java.util.List;

public class MainCategoriesFragment extends Fragment {

    private static final String TAG = "MainCategoriesFragment";
    private RecyclerView rvMainCategories;
    private List<CategoryModel> categories;
    private MainCategoryAdapter mainCategoryAdapter;

    public MainCategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);

        categories=new ArrayList<>();

        getCategories();
    }

    private void getCategories() {
        FirebaseDatabase.getInstance().getReference("Categories")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            CategoryModel category = snapshot.getValue(CategoryModel.class);
                            if (category != null) {
                                category.setBanners(FirebaseManager.getBanners(snapshot));
                                categories.add(category);
                            }
                        }
                        rvMainCategories.setHasFixedSize(true);
                        rvMainCategories.setLayoutManager(new LinearLayoutManager(getContext()));
                        mainCategoryAdapter = new MainCategoryAdapter(categories);
                        rvMainCategories.setAdapter(mainCategoryAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "onCancelled: " + error.getMessage());
                    }
                });
    }

    private void initView(View view) {
        rvMainCategories = view.findViewById(R.id.rvMainCategories_MainCategoriesFragment);
    }
}