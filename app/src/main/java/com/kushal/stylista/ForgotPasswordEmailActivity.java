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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ForgotPasswordEmailActivity extends AppCompatActivity {

    private ImageView backButton;
    private EditText emailEditText;
    private Button sendCodeButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String verificationCode;
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
        db = FirebaseFirestore.getInstance();

        sendCodeButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            if (email.isEmpty()) {
                emailEditText.setError("Email cannot be empty");
                return;
            }

            verificationCode = generateVerificationCode();

            Map<String, Object> codeData = new HashMap<>();
            codeData.put("code", verificationCode);
            codeData.put("email", email);

            db.collection("verificationCodes").document(email)
                    .set(codeData)
                    .addOnSuccessListener(aVoid -> {
                        new Thread(() -> {
                            try {
                                sendVerificationEmail(email, verificationCode);
                                runOnUiThread(() -> {
                                    Toast.makeText(this, "Verification code sent to " + email, Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(ForgotPasswordEmailActivity.this, EmailFourDigitCode.class);
                                    intent.putExtra("email", email);
                                    intent.putExtra("code", verificationCode);
                                    startActivity(intent);
                                });
                            } catch (Exception e) {
                                runOnUiThread(() -> {
                                    Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.e("MyAppError", e.getMessage());

                                });
                            }
                        }).start();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to store code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 1000 + random.nextInt(9000);
        return String.valueOf(code);
    }

    private void sendVerificationEmail(String email, String code) throws MessagingException {

        final String username = "chhetrikushal14@gmail.com";
        final String password = "yavqxznvpsqzhnph";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Your Verification Code");
            message.setText("Your verification code is: " + code +
                    "\n\nPlease use this code to reset your password.");

            Transport.send(message);
        } catch (MessagingException e) {
            throw new MessagingException("Email sending failed: " + e.getMessage(), e);
        }
    }
}