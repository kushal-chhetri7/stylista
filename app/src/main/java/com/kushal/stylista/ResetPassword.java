package com.kushal.stylista;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    private EditText newPasswordEditText, confirmPasswordEditText;
    private TextInputLayout newPasswordLayout, confirmPasswordLayout;
    private Button resetButton;
    private ImageView backButton;
    private String email;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resetpassword);

        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        newPasswordEditText = findViewById(R.id.newPassword);
        confirmPasswordEditText = findViewById(R.id.confirmNewPassword);
        newPasswordLayout = findViewById(R.id.newPasswordLayout);
        confirmPasswordLayout = findViewById(R.id.confirmNewPasswordLayout);
        resetButton = findViewById(R.id.verify);
        backButton = findViewById(R.id.backButton);

        setupPasswordValidation();

        resetButton.setOnClickListener(v -> resetPassword());

        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void setupPasswordValidation() {
        newPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();
                if (password.length() < 8) {
                    newPasswordLayout.setError("Password must be at least 8 characters");
                } else if (!password.matches(".*[A-Z].*")) {
                    newPasswordLayout.setError("Password must contain at least one uppercase letter");
                } else if (!password.matches(".*[a-z].*")) {
                    newPasswordLayout.setError("Password must contain at least one lowercase letter");
                } else if (!password.matches(".*\\d.*")) {
                    newPasswordLayout.setError("Password must contain at least one number");
                } else if (!password.matches(".*[!@#$%^&*()_+].*")) {
                    newPasswordLayout.setError("Password must contain at least one special character");
                } else {
                    newPasswordLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(newPasswordEditText.getText().toString())) {
                    confirmPasswordLayout.setError("Passwords do not match");
                } else {
                    confirmPasswordLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void resetPassword() {
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (newPassword.isEmpty()) {
            newPasswordLayout.setError("Password cannot be empty");
            return;
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordLayout.setError("Please confirm your password");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordLayout.setError("Passwords do not match");
            return;
        }

        if (newPasswordLayout.getError() != null) {
            return;
        }

        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mAuth.getCurrentUser().updatePassword(newPassword)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        Toast.makeText(ResetPassword.this, "Password reset successfully", Toast.LENGTH_SHORT).show();

                                        Intent successIntent = new Intent(ResetPassword.this, PasswordChangedSuccess.class);
                                        startActivity(successIntent);
                                        finish();
                                    } else {
                                        Toast.makeText(ResetPassword.this, "Password reset failed: " + updateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(ResetPassword.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}