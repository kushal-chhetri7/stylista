package com.kushal.stylista;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.*;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class ClothPreferenceActivity extends AppCompatActivity {

    private static final int MAX_SELECTIONS = 5;

    private List<String> selectedPreferences = new ArrayList<>();
    private int selectedCount = 0;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private String email, password, provider, idToken, accessToken;
    private String firstName, lastName, dob, gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cloth_preference);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Get values from Intent
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password"); // might be null
        provider = getIntent().getStringExtra("provider"); // email, google, facebook
        idToken = getIntent().getStringExtra("idToken");
        accessToken = getIntent().getStringExtra("accessToken");

        firstName = getIntent().getStringExtra("firstName");
        lastName = getIntent().getStringExtra("lastName");
        dob = getIntent().getStringExtra("dob");
        gender = getIntent().getStringExtra("gender");

        Map<Integer, String> preferenceMap = new HashMap<>();
        preferenceMap.put(R.id.boxTShirt, "T-Shirt");
        preferenceMap.put(R.id.shirt, "Shirt");
        preferenceMap.put(R.id.hoodie, "Hoodie");
        preferenceMap.put(R.id.pants, "Pants");
        preferenceMap.put(R.id.suit, "Suit");
        preferenceMap.put(R.id.jacket, "Jacket");
        preferenceMap.put(R.id.skirt, "Skirt");
        preferenceMap.put(R.id.dress, "Dress");
        preferenceMap.put(R.id.undergarments, "Undergarments");
        preferenceMap.put(R.id.shorts, "Shorts");
        preferenceMap.put(R.id.joggers, "Joggers");
        preferenceMap.put(R.id.scarf, "Scarf");

        List<LinearLayout> selectableBoxes = Arrays.asList(
                findViewById(R.id.boxTShirt), findViewById(R.id.shirt), findViewById(R.id.hoodie),
                findViewById(R.id.pants), findViewById(R.id.suit), findViewById(R.id.jacket),
                findViewById(R.id.skirt), findViewById(R.id.dress), findViewById(R.id.undergarments),
                findViewById(R.id.shorts), findViewById(R.id.joggers), findViewById(R.id.scarf)
        );

        for (LinearLayout box : selectableBoxes) {
            box.setOnClickListener(v -> {
                String preference = preferenceMap.get(box.getId());
                boolean isSelected = "selected".equals(box.getTag());

                if (isSelected) {
                    box.setBackground(ContextCompat.getDrawable(this, R.drawable.box_background));
                    box.setTag(null);
                    selectedCount--;
                    selectedPreferences.remove(preference);
                } else if (selectedCount < MAX_SELECTIONS) {
                    box.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_box_background));
                    box.setTag("selected");
                    selectedCount++;
                    selectedPreferences.add(preference);
                } else {
                    Toast.makeText(this, "Max 5 preferences allowed", Toast.LENGTH_SHORT).show();
                }
            });
        }

        Button startButton = findViewById(R.id.startButton);
        Button skipButton = findViewById(R.id.skipButton);

        startButton.setOnClickListener(v -> {
            if (selectedCount > 0) {
                registerFirebaseUser();
            } else {
                Toast.makeText(this, "Please select at least one preference", Toast.LENGTH_SHORT).show();
            }
        });

        skipButton.setOnClickListener(v -> {
            selectedPreferences.clear();
            registerFirebaseUser();
        });
    }

    private void registerFirebaseUser() {
        if ("google".equals(provider)) {
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            signInWithCredential(credential);
        } else if ("facebook".equals(provider)) {
            AuthCredential credential = FacebookAuthProvider.getCredential(accessToken);
            signInWithCredential(credential);
        } else { // Email & Password
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(result -> saveUserProfile(auth.getCurrentUser().getUid()))
                    .addOnFailureListener(e -> Toast.makeText(this, "Email signup failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void signInWithCredential(AuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnSuccessListener(result -> saveUserProfile(auth.getCurrentUser().getUid()))
                .addOnFailureListener(e -> Toast.makeText(this, "Auth failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveUserProfile(String uid) {
        String preferences = String.join(",", selectedPreferences);

        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("email", email);
        userProfile.put("firstName", firstName);
        userProfile.put("lastName", lastName);
        userProfile.put("dob", dob);
        userProfile.put("gender", gender);
        userProfile.put("preferences", preferences);
        userProfile.put("profileCompleted", true);
        userProfile.put("createdAt", FieldValue.serverTimestamp());

        db.collection("users").document(uid)
                .set(userProfile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Welcome to Stylista!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save profile", Toast.LENGTH_SHORT).show();
                });
    }
}
