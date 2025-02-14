package com.mahitab.ecommerce.fragments;

import android.content.Intent;
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

import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.activities.HomeActivity;
import com.mahitab.ecommerce.activities.SearchResultActivity;

public class CategoriesFragment extends Fragment {

    private Toolbar toolbar;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);

        if (getActivity()!=null && getActivity() instanceof HomeActivity){
            ((HomeActivity) getActivity()).setSupportActionBar(toolbar);
            setHasOptionsMenu(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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

    private void initView(View view) {
        toolbar = view.findViewById(R.id.toolbar);
    }
}