package com.kushal.stylista;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class ResetPassword extends AppCompatActivity {

    private EditText newPasswordEditText, confirmPasswordEditText;
    private TextInputLayout newPasswordLayout, confirmPasswordLayout;
    private Button resetButton;
    private ImageView backButton;
    private String email;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resetpassword);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        String verificationCode = intent.getStringExtra("code");

        newPasswordEditText = findViewById(R.id.newPassword);
        confirmPasswordEditText = findViewById(R.id.confirmNewPassword);
        newPasswordLayout = findViewById(R.id.newPasswordLayout);
        confirmPasswordLayout = findViewById(R.id.confirmNewPasswordLayout);
        resetButton = findViewById(R.id.verify);
        backButton = findViewById(R.id.backButton);

        setupPasswordValidation();

        resetButton.setOnClickListener(v -> resetPassword(verificationCode));

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

    private void resetPassword(String verificationCode) {
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

        // Verify the code from Firestore
        db.collection("verificationCodes").document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String storedCode = documentSnapshot.getString("code");

                        if (storedCode != null && storedCode.equals(verificationCode)) {
                            // Hash the password for security
                            String hashedPassword = hashPassword(newPassword);

                            // Create a new document in a password-reset collection
                            // This approach allows you to store password reset information without
                            // needing to directly query the users collection
                            Map<String, Object> resetData = new HashMap<>();
                            resetData.put("email", email);
                            resetData.put("newPassword", hashedPassword);
                            resetData.put("timestamp", System.currentTimeMillis());
                            resetData.put("completed", false);

                            db.collection("passwordResets").document()
                                    .set(resetData)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(ResetPassword.this,
                                                "Password reset request submitted successfully",
                                                Toast.LENGTH_SHORT).show();

                                        // Delete the verification code
                                        db.collection("verificationCodes").document(email).delete();

                                        Intent successIntent = new Intent(ResetPassword.this,
                                                PasswordChangedSuccess.class);
                                        startActivity(successIntent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(ResetPassword.this,
                                                "Failed to submit password reset: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(ResetPassword.this, "Invalid verification code", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ResetPassword.this, "Verification code not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ResetPassword.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            BigInteger number = new BigInteger(1, hash);
            StringBuilder hexString = new StringBuilder(number.toString(16));

            // Pad with leading zeros
            while (hexString.length() < 64) {
                hexString.insert(0, '0');
            }

            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return password;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}