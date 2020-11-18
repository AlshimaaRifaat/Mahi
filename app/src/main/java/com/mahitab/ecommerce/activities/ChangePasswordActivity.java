package com.mahitab.ecommerce.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.managers.DataManager;
import com.mahitab.ecommerce.managers.interfaces.BaseCallback;
import com.mahitab.ecommerce.utils.CommonUtils;

import static com.mahitab.ecommerce.utils.CommonUtils.setArDefaultLocale;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputLayout tilEmail;
    private EditText etEmail;
    private Button btnSubmit;
    private SharedPreferences defaultPreferences;


    private static final long MIN_CLICK_INTERVAL = 1000; //in millis
    private long lastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setArDefaultLocale(this);
        setContentView(R.layout.activity_reset_password);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.reset_password));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initView();

        defaultPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        btnSubmit.setOnClickListener(view -> {
            long currentTime = SystemClock.elapsedRealtime();
            if (currentTime - lastClickTime > MIN_CLICK_INTERVAL) {
                lastClickTime = currentTime;

            String email1 = defaultPreferences.getString("email", null);
            if(email1!=null)
            {
                this.etEmail.setText(email1);
            }
            toggleButton();
            String email = etEmail.getText().toString();
            if (!CommonUtils.isNotEmpty(email)) {
                tilEmail.setError(getResources().getString(R.string.field_cannot_be_empty));
            } else if (!CommonUtils.isEmailValid(email)) {
                tilEmail.setError(getResources().getString(R.string.invalid_email));
            } else {
                DataManager.getInstance().resetPassword(email, new BaseCallback() {
                    @Override
                    public void onResponse(int status) {
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_your_email), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.putExtra("userEmail", email);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        });
                    }

                    @Override
                    public void onFailure(final String message) {
                        String msg = message.toLowerCase();
                        if (msg.contains("customer") || msg.contains("invalid")) {
                            runOnUiThread(() -> tilEmail.setError(message));
                        } else {
                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());
                        }
                        runOnUiThread(ChangePasswordActivity.this::toggleButton);
                    }
                });
                return;
            }

            toggleButton();
        }});
    }

    private void initView() {
        tilEmail = findViewById(R.id.tilEmail_ResetPasswordActivity);
        etEmail = findViewById(R.id.etEmail_ResetPasswordActivity);
        btnSubmit = findViewById(R.id.btnSubmit_ResetPasswordActivity);

    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0); // remove activity default transition
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void toggleButton() {
        btnSubmit.setEnabled(!btnSubmit.isEnabled());
    }
}



