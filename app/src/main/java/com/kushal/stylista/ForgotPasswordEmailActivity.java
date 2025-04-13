package com.kushal.stylista;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ForgotPasswordEmailActivity extends AppCompatActivity {

    private ImageView backButton;
    private EditText emailEditText;
    private Button sendCodeButton;
    private FirebaseAuth mAuth;
    private TextView rememberPasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgotpassword);

        backButton = findViewById(R.id.backButton);
        rememberPasswordText = findViewById(R.id.rememberPasswordText);
        emailEditText = findViewById(R.id.emailEditText);
        sendCodeButton = findViewById(R.id.sendCodeButton);

        mAuth = FirebaseAuth.getInstance();

        sendCodeButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            if (email.isEmpty()) {
                emailEditText.setError("Email cannot be empty");
                return;
            }

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordEmailActivity.this, "Password reset email sent to " + email, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ForgotPasswordEmailActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ForgotPasswordEmailActivity.this, "Failed to send reset email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordEmailActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        rememberPasswordText.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordEmailActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}