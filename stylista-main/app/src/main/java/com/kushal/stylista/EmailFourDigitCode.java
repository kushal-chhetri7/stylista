package com.kushal.stylista;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailFourDigitCode extends AppCompatActivity {

    private TextView timerTextView, resendTextView;
    private EditText digit1, digit2, digit3, digit4;
    private Button verifyButton;
    private String email, originalCode, password;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailfourdigitcode);

        db = FirebaseFirestore.getInstance();

        timerTextView = findViewById(R.id.textView2);
        digit1 = findViewById(R.id.digit1);
        digit2 = findViewById(R.id.digit2);
        digit3 = findViewById(R.id.digit3);
        digit4 = findViewById(R.id.digit4);
        verifyButton = findViewById(R.id.verify);
        resendTextView = findViewById(R.id.textView1);

        // Get values from intent
        email = getIntent().getStringExtra("email");
        originalCode = getIntent().getStringExtra("code");
        password = getIntent().getStringExtra("password");

        startCountdownTimer();

        verifyButton.setOnClickListener(v -> verifyCode());

        resendTextView.setOnClickListener(v -> {
            if (resendTextView.getText().toString().equals("Resend")) {
                resendVerificationCode();
            }
        });
    }

    private void verifyCode() {
        String enteredCode = digit1.getText().toString()
                + digit2.getText().toString()
                + digit3.getText().toString()
                + digit4.getText().toString();

        if (enteredCode.length() != 4) {
            Toast.makeText(this, "Please enter a 4-digit code", Toast.LENGTH_SHORT).show();
            return;
        }

        if (enteredCode.equals(originalCode)) {
            Toast.makeText(this, "Code verified successfully", Toast.LENGTH_SHORT).show();

            // ➡️ Move to Profile Setup, passing email and password
            Intent intent = new Intent(this, ProfileSetupActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("password", password);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid code", Toast.LENGTH_SHORT).show();
        }
    }

    private void startCountdownTimer() {
        new CountDownTimer(59000, 1000) {
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                timerTextView.setText(String.format("00:%02d", seconds));
            }

            public void onFinish() {
                timerTextView.setText("");
                resendTextView.setText("Resend");
                resendTextView.setClickable(true);
            }
        }.start();
    }

    private void resendVerificationCode() {
        originalCode = String.valueOf(1000 + new java.util.Random().nextInt(9000));

        db.collection("verificationCodes").document(email)
                .update("code", originalCode)
                .addOnSuccessListener(aVoid -> {
                    new Thread(() -> {
                        try {
                            sendVerificationEmail(email, originalCode);
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Code resent to " + email, Toast.LENGTH_LONG).show();
                                startCountdownTimer();
                                resendTextView.setText("Send code again");
                            });
                        } catch (Exception e) {
                            runOnUiThread(() -> Toast.makeText(this, "Failed to resend: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        }
                    }).start();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update code: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void sendVerificationEmail(String email, String code) throws MessagingException {
        final String username = "chhetrikushal14@gmail.com";
        final String password = "yavqxznvpsqzhnph"; // Replace with your real app password or use Firebase Functions instead

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        message.setSubject("Your New Verification Code");
        message.setText("Your new verification code is: " + code);
        Transport.send(message);
    }
}
