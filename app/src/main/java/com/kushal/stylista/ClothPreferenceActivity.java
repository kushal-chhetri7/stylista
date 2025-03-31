package com.kushal.stylista;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClothPreferenceActivity extends AppCompatActivity {

    private int selectedCount = 0;
    private final int MAX_SELECTIONS = 5;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String uid;
    private List<String> selectedPreferences = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cloth_preference);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        uid = getIntent().getStringExtra("uid");

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Log.e("AuthError", "No authenticated user found");
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (uid == null || !uid.equals(currentUser.getUid())) {
            Log.e("AuthError", "UID mismatch: passed=" + uid + ", auth=" + currentUser.getUid());
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("Firestore", "Starting ClothPreferenceActivity for UID: " + uid);

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
                findViewById(R.id.boxTShirt),
                findViewById(R.id.shirt),
                findViewById(R.id.hoodie),
                findViewById(R.id.pants),
                findViewById(R.id.suit),
                findViewById(R.id.jacket),
                findViewById(R.id.skirt),
                findViewById(R.id.dress),
                findViewById(R.id.undergarments),
                findViewById(R.id.shorts),
                findViewById(R.id.joggers),
                findViewById(R.id.scarf)
        );

        for (LinearLayout box : selectableBoxes) {
            box.setOnClickListener(v -> {
                boolean isSelected = box.getTag() != null && box.getTag().equals("selected");
                String preference = preferenceMap.get(box.getId());

                if (isSelected) {
                    box.setBackground(ContextCompat.getDrawable(this, R.drawable.box_background));
                    box.setTag(null);
                    selectedCount--;
                    selectedPreferences.remove(preference);
                } else {
                    if (selectedCount < MAX_SELECTIONS) {
                        box.setBackground(ContextCompat.getDrawable(this, R.drawable.selected_box_background));
                        box.setTag("selected");
                        selectedCount++;
                        selectedPreferences.add(preference);
                    } else {
                        Toast.makeText(this, "You can select up to " + MAX_SELECTIONS + " preferences", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(v -> {
            if (selectedCount > 0) {
                savePreferencesToFirestore();
            } else {
                Toast.makeText(this, "Please select at least one preference or click Skip", Toast.LENGTH_SHORT).show();
            }
        });

        Button skipButton = findViewById(R.id.skipButton);
        skipButton.setOnClickListener(v -> navigateToMainActivity());
    }

    private void savePreferencesToFirestore() {
        String preferencesString = String.join(",", selectedPreferences);

        Map<String, Object> updates = new HashMap<>();
        updates.put("preferences", preferencesString);
        updates.put("updatedAt", FieldValue.serverTimestamp());

        Log.d("Firestore", "Attempting to update preferences for UID: " + uid + " with: " + preferencesString);
        db.collection("users").document(uid)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Preferences updated successfully for UID: " + uid);
                    Toast.makeText(this, "Preferences saved successfully!", Toast.LENGTH_SHORT).show();
                    navigateToMainActivity();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Failed to save preferences: " + e.getMessage(), e);
                    Toast.makeText(this, "Failed to save preferences: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void navigateToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}