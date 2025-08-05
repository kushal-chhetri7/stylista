package com.kushal.stylista;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

public class PrivacyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_privacy);

        findViewById(R.id.backButton).setOnClickListener(v -> showMain());
    }
    private void showMain() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
