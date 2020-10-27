package com.mahitab.ecommerce.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.managers.DataManager;
import com.mahitab.ecommerce.managers.interfaces.BaseCallback;
import com.mahitab.ecommerce.models.CurrentUser;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.mahitab.ecommerce.utils.CommonUtils.setArDefaultLocale;

public class VerifyPhoneActivity extends AppCompatActivity {

    private static final String TAG = "VerifyPhoneActivity";
    private TextView tvPhoneNumber;
    private EditText etCode1;
    private EditText etCode2;
    private EditText etCode3;
    private EditText etCode4;
    private EditText etCode5;
    private EditText etCode6;
    private TextView tvResendCode;
    private Button btnRegister;

    private CurrentUser user;
    private String phoneNumber;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private SharedPreferences defaultPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setArDefaultLocale(this);
        setContentView(R.layout.activity_verify_phone);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.verify_phone));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initView();

        defaultPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                mVerificationId = s;
                mResendToken = forceResendingToken;
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                String code = phoneAuthCredential.getSmsCode();
                if (code != null) {
                    String[] codeNumbers = code.split("(?!^)");
                    etCode1.setText(codeNumbers[0]);
                    etCode2.setText(codeNumbers[1]);
                    etCode3.setText(codeNumbers[2]);
                    etCode4.setText(codeNumbers[3]);
                    etCode5.setText(codeNumbers[4]);
                    etCode6.setText(codeNumbers[5]);
                    signInWithPhoneAuthCredential(phoneAuthCredential);
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.try_again_later), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onVerificationFailed: " + e.getMessage());
            }
        };

        if (getIntent().getExtras() != null) {
            user = (CurrentUser) getIntent().getExtras().getSerializable("user");
            if (user != null) {
                tvPhoneNumber.setText(user.getPhone());
                phoneNumber = "+2" + user.getPhone();
                sendCodeVerification(phoneNumber);
            }
        }

        etCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    etCode2.requestFocus();
                    etCode1.setTransformationMethod(SingleLineTransformationMethod.getInstance());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    etCode3.requestFocus();
                    etCode2.setTransformationMethod(SingleLineTransformationMethod.getInstance());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    etCode4.requestFocus();
                    etCode3.setTransformationMethod(SingleLineTransformationMethod.getInstance());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    etCode5.requestFocus();
                    etCode4.setTransformationMethod(SingleLineTransformationMethod.getInstance());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    etCode6.requestFocus();
                    etCode5.setTransformationMethod(SingleLineTransformationMethod.getInstance());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tvResendCode.setOnClickListener(v -> {
            if (phoneNumber != null && mResendToken != null)
                resendVerificationCode(phoneNumber, mResendToken);
        });

        btnRegister.setOnClickListener(v -> {
            if (isCodePartsCompleted()) {
                String code = etCode1.getText().toString() +
                        etCode2.getText().toString() +
                        etCode3.getText().toString() +
                        etCode4.getText().toString() +
                        etCode5.getText().toString() +
                        etCode6.getText().toString();

                verifyCode(code);
            }else Log.e(TAG, "onClick: "+isCodePartsCompleted()  );
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
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber_VerifyPhoneActivity);
        etCode1 = findViewById(R.id.etCode1_VerifyPhoneActivity);
        etCode2 = findViewById(R.id.etCode2_VerifyPhoneActivity);
        etCode3 = findViewById(R.id.etCode3_VerifyPhoneActivity);
        etCode4 = findViewById(R.id.etCode4_VerifyPhoneActivity);
        etCode5 = findViewById(R.id.etCode5_VerifyPhoneActivity);
        etCode6 = findViewById(R.id.etCode6_VerifyPhoneActivity);
        tvResendCode = findViewById(R.id.tvResendCode_VerifyPhoneActivity);
        btnRegister = findViewById(R.id.btnRegister_VerifyPhoneActivity);
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void sendCodeVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null) {
                            FirebaseUser firebaseUser = task.getResult().getUser();
                            if (firebaseUser != null) {
                                sendRegisterRequest(firebaseUser, user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName());
                            } else Log.e(TAG, "user not signed in: ");
                        }
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                    }
                });
    }

    private boolean isCodePartsCompleted() {
        return !TextUtils.isEmpty(etCode1.getText()) &&
                !TextUtils.isEmpty(etCode2.getText()) &&
                !TextUtils.isEmpty(etCode3.getText()) &&
                !TextUtils.isEmpty(etCode4.getText()) &&
                !TextUtils.isEmpty(etCode5.getText()) &&
                !TextUtils.isEmpty(etCode6.getText());
    }

    private void sendRegisterRequest(FirebaseUser firebaseUser, String email, String password, String firstName, String lastName) {
        DataManager.getInstance().register(
                email,
                password,
                firstName,
                lastName,
                false,
                new BaseCallback() {
                    @Override
                    public void onResponse(int status) {
                        if (status == RESULT_OK) {
                            runOnUiThread(new Thread(() -> FirebaseInstanceId.getInstance()
                                    .getInstanceId().addOnSuccessListener(instanceIdResult -> { //get deviceToken
                                String deviceToken = instanceIdResult.getToken();
                                HashMap<String, Object> userInfo = new HashMap<>();
                                userInfo.put("email", user.getEmail());
                                userInfo.put("phoneNumber", user.getPhone());
                                userInfo.put("token", deviceToken);
                                FirebaseDatabase.getInstance()
                                        .getReference("Users")
                                        .child(firebaseUser.getUid())
                                        .setValue(userInfo).addOnSuccessListener(aVoid -> { //save user data in realtime db
                                    saveEmailAndPassword(email, password);
                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.account_created_successfully), Toast.LENGTH_SHORT).show();
                                });
                            })));
                        } else {
                            onFailure("An unknown error occurred");
                        }
                    }

                    @Override
                    public void onFailure(final String message) {
                        Log.e(TAG, "onFailure: " + message);
                    }
                }
        );
    }

    private void saveEmailAndPassword(String email, String password) {
        defaultPreferences.edit().putString("email", email).apply();
        defaultPreferences.edit().putString("password", password).apply();
    }
}