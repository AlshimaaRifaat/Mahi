package com.mahitab.ecommerce.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mahitab.ecommerce.R;
import com.mahitab.ecommerce.managers.DataManager;
import com.mahitab.ecommerce.managers.interfaces.BaseCallback;
import com.mahitab.ecommerce.utils.CommonUtils;

public class ResetPasswordActivity extends AbstractActivity {

    private Button mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        ((TextView) mToolbar.findViewById(R.id.title)).setText("");
        ImageButton backBtn = mToolbar.findViewById(R.id.backButton);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetPasswordActivity.this.setResult(Activity.RESULT_CANCELED);
                ResetPasswordActivity.this.finish();
            }
        });

        EditText emailInput = findViewById(R.id.reset_email_input);
        mSubmitButton = findViewById(R.id.reset_submit_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleButton();

                String email = emailInput.getText().toString();
                if(!CommonUtils.isNotEmpty(email)) {
                    emailInput.setError("Field cannot be empty");
                } else if(!CommonUtils.isEmailValid(email)) {
                    emailInput.setError("Invalid email");
                } else {
                    DataManager.getInstance().resetPassword(email, new BaseCallback() {
                        @Override
                        public void onResponse(int status) {
                            showOnUiThread("A reset password email is on your way");
                            runOnUiThread(() -> {
                                Intent intent = new Intent();
                                intent.putExtra("userEmail", email);
                                ResetPasswordActivity.this.setResult(Activity.RESULT_OK, intent);
                                ResetPasswordActivity.this.finish();
                            });
                        }

                        @Override
                        public void onFailure(final String message) {
                            String msg = message.toLowerCase();
                            if(msg.contains("customer") || msg.contains("invalid")) {
                                ResetPasswordActivity.this.runOnUiThread(() -> {
                                    emailInput.setError(message);
                                });
                            } else {
                                showOnUiThread(message);
                            }
                            runOnUiThread(ResetPasswordActivity.this::toggleButton);
                        }
                    });
                    return;
                }

                toggleButton();
            }
        });
    }

    private void toggleButton() {
        mSubmitButton.setEnabled(!mSubmitButton.isEnabled());
    }
}
