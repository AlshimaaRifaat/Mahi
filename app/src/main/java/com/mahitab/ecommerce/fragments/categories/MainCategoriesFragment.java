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

import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.adapters.MainCategoryAdapter;
import com.mahitab.ecommerce.models.CategoryModel;
import com.mahitab.ecommerce.utils.OlgorClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        getCategories();
    }

    private void getCategories() {
        OlgorClient.getInstance().getApi().getCategories().enqueue(new Callback<List<CategoryModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<CategoryModel>> call, @NonNull Response<List<CategoryModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories = response.body();
                    rvMainCategories.setHasFixedSize(true);
                    rvMainCategories.setLayoutManager(new LinearLayoutManager(getContext()));
                    mainCategoryAdapter = new MainCategoryAdapter(categories);
                    rvMainCategories.setAdapter(mainCategoryAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CategoryModel>> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void initView(View view) {
        rvMainCategories=view.findViewById(R.id.rvMainCategories_MainCategoriesFragment);
    }
}