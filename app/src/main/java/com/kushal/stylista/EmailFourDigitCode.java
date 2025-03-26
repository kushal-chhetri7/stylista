package com.kushal.stylista;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailFourDigitCode extends AppCompatActivity {

    private TextView timerTextView;
    private EditText digit1, digit2, digit3, digit4;
    private Button verifyButton;
    private TextView resendTextView;
    private String email, originalCode;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_emailfourdigitcode);



        timerTextView = findViewById(R.id.textView2);
        digit1 = findViewById(R.id.digit1);
        digit2 = findViewById(R.id.digit2);
        digit3 = findViewById(R.id.digit3);
        digit4 = findViewById(R.id.digit4);
        verifyButton = findViewById(R.id.verify);
        resendTextView = findViewById(R.id.textView1);
        db = FirebaseFirestore.getInstance();


        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        originalCode = intent.getStringExtra("code");




        startCountdownTimer();


        setupCodeInput();


        verifyButton.setOnClickListener(v -> verifyCode());


        resendTextView.setOnClickListener(v -> {
            if (resendTextView.getText().toString().equals("Resend")) {
                resendVerificationCode();
            }
        });
    }

    private void startCountdownTimer() {
        new CountDownTimer(59000, 1000) { // 59 seconds
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                timerTextView.setText(String.format("00:%02d", seconds));
            }

            public void onFinish() {
                timerTextView.setText("");
                resendTextView.setText("Resend");
                resendTextView.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                resendTextView.setClickable(true);
            }
        }.start();
    }

    private void setupCodeInput() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    if (digit1.isFocused()) digit2.requestFocus();
                    else if (digit2.isFocused()) digit3.requestFocus();
                    else if (digit3.isFocused()) digit4.requestFocus();
                }
            }
        };

        digit1.addTextChangedListener(textWatcher);
        digit2.addTextChangedListener(textWatcher);
        digit3.addTextChangedListener(textWatcher);
        digit4.addTextChangedListener(textWatcher);
    }

    private void verifyCode() {
        String enteredCode = digit1.getText().toString() +
                digit2.getText().toString() +
                digit3.getText().toString() +
                digit4.getText().toString();

        if (enteredCode.length() != 4) {
            Toast.makeText(this, "Please enter a 4-digit code", Toast.LENGTH_SHORT).show();
            return;
        }

        if (enteredCode.equals(originalCode)) {
            Toast.makeText(this, "Code verified successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EmailFourDigitCode.this, ResetPassword.class);
            intent.putExtra("email", email);
            intent.putExtra("code", originalCode);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid code", Toast.LENGTH_SHORT).show();
        }
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

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        message.setSubject("Your Verification Code");
        message.setText("Your new verification code is: " + code +
                "\n\nPlease use this code to reset your password.");
        Transport.send(message);
    }
}