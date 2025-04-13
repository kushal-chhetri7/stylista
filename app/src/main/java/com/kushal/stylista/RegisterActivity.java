

package com.kushal.stylista;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

import javax.mail.*;

import javax.mail.internet.*;

public class RegisterActivity extends AppCompatActivity {

    private EditText email, password, confirmPassword;
    private Button registerButton, facebookButton;
    private FrameLayout googleButton;
    private TextView tvLogin;

    private GoogleSignInClient googleSignInClient;
    private CallbackManager callbackManager;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseFirestore db;
    private String verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_register);

        db = FirebaseFirestore.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        registerButton = findViewById(R.id.registerButton);
        facebookButton = findViewById(R.id.facebookButton);
        googleButton = findViewById(R.id.googleButton);
        tvLogin = findViewById(R.id.tvLogin);

        configureGoogleSignIn();
        configureFacebookSignIn();

        googleButton.setOnClickListener(v -> signInWithGoogle());

        facebookButton.setOnClickListener(v -> LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile")));

        registerButton.setOnClickListener(v -> {
            String emailText = email.getText().toString().trim();
            String passwordText = password.getText().toString().trim();
            String confirmPasswordText = confirmPassword.getText().toString().trim();

            if (emailText.isEmpty()) {
                email.setError("Email is required");
                return;
            }
            if (passwordText.isEmpty()) {
                password.setError("Password is required");
                return;
            }
            if (!passwordText.equals(confirmPasswordText)) {
                confirmPassword.setError("Passwords do not match");
                return;
            }

            verificationCode = String.valueOf(1000 + new Random().nextInt(9000));

            Map<String, Object> codeData = new HashMap<>();
            codeData.put("code", verificationCode);
            codeData.put("email", emailText);
            codeData.put("password", passwordText); // only for flow

            db.collection("verificationCodes").document(emailText)
                    .set(codeData)
                    .addOnSuccessListener(aVoid -> {
                        new Thread(() -> {
                            try {
                                sendVerificationEmail(emailText, verificationCode);
                                runOnUiThread(() -> {
                                    Intent intent = new Intent(this, EmailFourDigitCode.class);
                                    intent.putExtra("email", emailText);
                                    intent.putExtra("code", verificationCode);
                                    intent.putExtra("password", passwordText);
                                    startActivity(intent);
                                });
                            } catch (MessagingException e) {
                                runOnUiThread(() -> Toast.makeText(this, "Failed to send email: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            }
                        }).start();
                    });
        });

        String fullText = "Already have an account? Log in";
        SpannableString spannableString = new SpannableString(fullText);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), fullText.indexOf("Log in"), fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ClickableSpan() {
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        }, fullText.indexOf("Log in"), fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvLogin.setText(spannableString);
        tvLogin.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void sendVerificationEmail(String email, String code) throws MessagingException {
        final String username = "chhetrikushal14@gmail.com";
        final String password = "yavqxznvpsqzhnph";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        message.setSubject("Verification Code");
        message.setText("Your verification code is: " + code);
        Transport.send(message);
    }

    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void configureFacebookSignIn() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            public void onSuccess(LoginResult loginResult) {
                Intent intent = new Intent(RegisterActivity.this, ProfileSetupActivity.class);
                intent.putExtra("email", loginResult.getAccessToken().getUserId() + "@facebook.com");
                intent.putExtra("provider", "facebook");
                intent.putExtra("accessToken", loginResult.getAccessToken().getToken());
                startActivity(intent);
            }

            public void onCancel() {
                Toast.makeText(RegisterActivity.this, "Facebook sign-in canceled", Toast.LENGTH_SHORT).show();
            }

            public void onError(FacebookException e) {
                Toast.makeText(RegisterActivity.this, "Facebook sign-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Intent intent = new Intent(this, ProfileSetupActivity.class);
                intent.putExtra("email", account.getEmail());
                intent.putExtra("provider", "google");
                intent.putExtra("idToken", account.getIdToken());
                startActivity(intent);
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
