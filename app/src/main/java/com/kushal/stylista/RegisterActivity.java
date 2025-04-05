package com.kushal.stylista;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
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

public class RegisterActivity extends AppCompatActivity {

    private EditText email;
    private TextInputEditText password, confirmPassword;
    private TextInputLayout passwordLayout, confirmPasswordLayout;
    private Button registerButton, facebookButton;
    private FrameLayout googleButton;
    private TextView tvLogin;

    private GoogleSignInClient googleSignInClient;
    private CallbackManager callbackManager;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        passwordLayout = findViewById(R.id.passwordLayout);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);
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
                passwordLayout.setError("Password is required");
                return;
            }
            if (confirmPasswordText.isEmpty()) {
                confirmPasswordLayout.setError("Confirm Password is required");
                return;
            }
            if (!passwordText.equals(confirmPasswordText)) {
                confirmPasswordLayout.setError("Passwords do not match");
                return;
            }

            // Generate Verification Code
            verificationCode = generateVerificationCode();

            // Store Verification Code in Firestore
            Map<String, Object> codeData = new HashMap<>();
            codeData.put("code", verificationCode);
            codeData.put("email", emailText);

            db.collection("verificationCodes").document(emailText)
                    .set(codeData)
                    .addOnSuccessListener(aVoid -> {
                        mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // Send Verification Email
                                        new Thread(() -> {
                                            try {
                                                sendVerificationEmail(emailText, verificationCode);
                                                runOnUiThread(() -> {
                                                    Toast.makeText(RegisterActivity.this, "Verification code sent!", Toast.LENGTH_SHORT).show();
                                                    // Redirect to Verification Activity
                                                    Intent intent = new Intent(RegisterActivity.this, EmailFourDigitCode.class);
                                                    intent.putExtra("email", emailText);
                                                    intent.putExtra("code", verificationCode);
                                                    startActivity(intent);
                                                    finish();
                                                });

                                            } catch (MessagingException e) {
                                                runOnUiThread(() -> {
                                                    Toast.makeText(RegisterActivity.this, "Failed to send verification email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    Log.e("MyAppError", e.getMessage());
                                                });

                                                // Optionally, consider deleting the user if email fails
                                                mAuth.getCurrentUser().delete();
                                                db.collection("verificationCodes").document(emailText).delete();

                                            }
                                        }).start();

                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        db.collection("verificationCodes").document(emailText).delete();
                                    }
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(RegisterActivity.this, "Failed to store verification code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        String fullText = "Already have an account? Log in";
        SpannableString spannableString = new SpannableString(fullText);
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        int startIndex = fullText.indexOf("Log in");
        int endIndex = startIndex + "Log in".length();
        spannableString.setSpan(boldSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(android.text.TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvLogin.setText(spannableString);
        tvLogin.setMovementMethod(LinkMovementMethod.getInstance());
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
                    "\n\nPlease use this code to verify your email.");

            Transport.send(message);
        } catch (MessagingException e) {
            throw new MessagingException("Email sending failed: " + e.getMessage(), e);
        }
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
            @Override
            public void onSuccess(LoginResult loginResult) {
                firebaseAuthWithFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(RegisterActivity.this, "Facebook login canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(RegisterActivity.this, "Facebook login failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Google sign in successful!", Toast.LENGTH_SHORT).show();
                          String emailText = mAuth.getCurrentUser().getEmail();
                          redirectToProfileSetup(emailText);
                    } else {
                        Toast.makeText(this, "Google sign in failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void firebaseAuthWithFacebook(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Facebook sign in successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, ProfileSetupActivity.class);
                    } else {
                        Toast.makeText(this, "Facebook sign in failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void redirectToProfileSetup(String email) {
        Intent intent = new Intent(RegisterActivity.this, ProfileSetupActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("uid", mAuth.getCurrentUser().getUid());
        startActivity(intent);
        finish();
    }
}