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
import com.mahitab.ecommerce.activities.RegisterActivity;

public class AccountFragment extends Fragment {
    private Toolbar toolbar;
    private Button btnLogin;
    private Button btnRegister;

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

        btnLogin.setOnClickListener(v -> startActivity(new Intent(getContext(), LoginActivity.class)));
        btnRegister.setOnClickListener(v -> startActivity(new Intent(getContext(), RegisterActivity.class)));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && isResumed()) {
            toolbar.setTitle(getResources().getString(R.string.account));
        }
    }

    private void initView(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        btnLogin = view.findViewById(R.id.btnLogin_AccountFragment);
        btnRegister = view.findViewById(R.id.btnRegister_AccountFragment);
    }
}