package com.mahitab.ecommerce.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.activities.HomeActivity;
import com.mahitab.ecommerce.activities.LoginActivity;

public class AccountFragment extends Fragment implements View.OnClickListener{
    private Toolbar toolbar;
    private Button btnLogin;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);

        if (getActivity() != null && getActivity() instanceof HomeActivity) {
            ((HomeActivity) getActivity()).setSupportActionBar(toolbar);
            setHasOptionsMenu(true);
        }
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && isResumed()) {
            toolbar.setTitle(getResources().getString(R.string.account));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnLogin_AccountFragment)
            startActivity(new Intent(getContext(), LoginActivity.class));
    }

    private void initView(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        btnLogin = view.findViewById(R.id.btnLogin_AccountFragment);
    }
}