package com.kushal.stylista;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasswordManagerActivity extends AppCompatActivity {

    private EditText currentPassword, newPassword, confirmNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_manager);

        currentPassword = findViewById(R.id.currentPassword);
        newPassword = findViewById(R.id.newPassword);
        confirmNewPassword = findViewById(R.id.confirmNewPassword);

        findViewById(R.id.backButton).setOnClickListener(v -> showMain());

        setupPasswordToggle(currentPassword);
        setupPasswordToggle(newPassword);
        setupPasswordToggle(confirmNewPassword);

        findViewById(R.id.verify).setOnClickListener(v -> handleChangePassword());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupPasswordToggle(EditText passwordField) {
        passwordField.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (passwordField.getTransformationMethod() instanceof PasswordTransformationMethod) {
                    passwordField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                passwordField.setSelection(passwordField.getText().length());
                return true;
            }
            return false;
        });
    }

    private void showMain() {
        Intent intent = new Intent(this, YourProfileActivity.class);
        startActivity(intent);
    }

    private void handleChangePassword() {
        String current = currentPassword.getText().toString().trim();
        String newPass = newPassword.getText().toString().trim();
        String confirmPass = confirmNewPassword.getText().toString().trim();

        if (current.isEmpty()) {
            currentPassword.setError("Enter current password");
            currentPassword.requestFocus();
            return;
        }

        if (newPass.isEmpty() || newPass.length() < 6) {
            newPassword.setError("Password must be at least 6 characters");
            newPassword.requestFocus();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            confirmNewPassword.setError("Passwords do not match");
            confirmNewPassword.requestFocus();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.getEmail() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        user.reauthenticate(EmailAuthProvider.getCredential(user.getEmail(), current))
                .addOnSuccessListener(authResult -> {
                    user.updatePassword(newPass)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Password updated successfully. Please log in again.", Toast.LENGTH_LONG).show();
                                FirebaseAuth.getInstance().signOut();

                                Intent intent = new Intent(PasswordManagerActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to update password: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    currentPassword.setError("Incorrect current password");
                    currentPassword.requestFocus();
                });
    }
}
