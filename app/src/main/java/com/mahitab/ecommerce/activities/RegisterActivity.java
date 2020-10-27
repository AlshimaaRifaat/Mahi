package com.mahitab.ecommerce.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.models.CurrentUser;
import com.mahitab.ecommerce.utils.CommonUtils;

import static com.mahitab.ecommerce.utils.CommonUtils.setArDefaultLocale;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilFirstName;
    private EditText etFirstName;
    private TextInputLayout tilLastName;
    private EditText etLastName;
    private TextInputLayout tilEmail;
    private EditText etEmail;
    private TextInputLayout tilPassword;
    private EditText etPassword;
    private TextInputLayout tilMobileNumber;
    private EditText etPhoneNumber;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setArDefaultLocale(this);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.register));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initView();

        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String firstName = etFirstName.getText().toString();
            String lastName = etLastName.getText().toString();
            String password = etPassword.getText().toString();
            String phoneNumber = etPhoneNumber.getText().toString();
            CurrentUser user = new CurrentUser(
                    firstName,
                    lastName,
                    email,
                    password,
                    phoneNumber
            );
            if (validateFields(email, password, firstName, lastName, phoneNumber)) {
                Intent intent = new Intent(getApplicationContext(), VerifyPhoneActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
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

    private void initView() {
        tilFirstName = findViewById(R.id.tilFirstName_RegisterActivity);
        etFirstName = findViewById(R.id.etFirstName_RegisterActivity);
        tilLastName = findViewById(R.id.tilLastName_RegisterActivity);
        etLastName = findViewById(R.id.etLastName_RegisterActivity);
        tilEmail = findViewById(R.id.tilEmail_RegisterActivity);
        etEmail = findViewById(R.id.etEmail_RegisterActivity);
        tilPassword = findViewById(R.id.tilPassword_RegisterActivity);
        etPassword = findViewById(R.id.etPassword_RegisterActivity);
        tilMobileNumber = findViewById(R.id.tilMobileNumber_RegisterActivity);
        etPhoneNumber = findViewById(R.id.etMobileNumber_RegisterActivity);
        btnRegister = findViewById(R.id.btnRegister_VerifyPhoneActivity);
    }

    private boolean validateFields(String email, String password, String firstName, String lastName, String mobileNumber) {
        boolean isValid = true;
        String emptyErrorMsg = getResources().getString(R.string.empty_field_error_message);
        if (email.isEmpty() || !CommonUtils.isEmailValid(email)) {
            if (email.isEmpty()) {
                tilEmail.setError(emptyErrorMsg);
            } else {
                tilEmail.setError("Invalid email address");
            }
            isValid = false;
        }

        if (firstName.isEmpty() || !CommonUtils.isNameValid(firstName)) {
            if (firstName.isEmpty()) {
                tilFirstName.setError(emptyErrorMsg);
            } else {
                tilFirstName.setError("Invalid first name");
            }
            isValid = false;
        }

        if (lastName.isEmpty() || !CommonUtils.isNameValid(lastName)) {
            if (lastName.isEmpty()) {
                tilLastName.setError(emptyErrorMsg);
            } else {
                tilLastName.setError("Invalid first name");
            }
            isValid = false;
        }

        boolean isPassValid = CommonUtils.isPasswordValid(password);
        if (password.isEmpty() || !isPassValid) {
            if (password.isEmpty()) {
                tilPassword.setError(emptyErrorMsg);
            } else {
                tilPassword.setError("Password must contain at least 8 characters, 1 uppercase character, and 1 number");
            }
            isValid = false;
        }
        boolean isMobileValid = CommonUtils.isPhone(mobileNumber);

        if (mobileNumber.isEmpty() || !isMobileValid) {
            if (mobileNumber.isEmpty()) {
                tilMobileNumber.setError(emptyErrorMsg);
            } else {
                tilMobileNumber.setError("Invalid mobile number");
            }
            isValid = false;
        }
        return isValid;
    }
}