package com.kushal.stylista;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PersonalDetailsActivity extends AppCompatActivity {

    private EditText editFirstName, editLastName, editDOB;
    private Spinner editGender;
    private Button updateButton;
    private Calendar calendar;
    private FirebaseFirestore db;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

        editFirstName = findViewById(R.id.firstNameEditText);
        editLastName = findViewById(R.id.lastNameEditText);
        editDOB = findViewById(R.id.dobEditText);
        editGender = findViewById(R.id.genderSpinner);
        updateButton = findViewById(R.id.updateButton);

        calendar = Calendar.getInstance();
        db = FirebaseFirestore.getInstance();


        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Male", "Female"});
        editGender.setAdapter(adapter);


        editDOB.setOnClickListener(v -> showDatePickerDialog());


        loadUserData();


        updateButton.setOnClickListener(v -> updateUserData());

        findViewById(R.id.backButton).setOnClickListener(v -> showMain());
    }

    private void showMain() {
        startActivity(new Intent(this, YourProfileActivity.class));
    }

    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            editDOB.setText(sdf.format(calendar.getTime()));
        };

        new DatePickerDialog(this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void loadUserData() {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        editFirstName.setText(documentSnapshot.getString("firstName"));
                        editLastName.setText(documentSnapshot.getString("lastName"));
                        editDOB.setText(documentSnapshot.getString("dob"));

                        String gender = documentSnapshot.getString("gender");
                        if (gender != null) {
                            int spinnerPosition = ((ArrayAdapter) editGender.getAdapter()).getPosition(gender);
                            editGender.setSelection(spinnerPosition);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Failed to load profile: " + e.getMessage(), e);
                    Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUserData() {
        String updatedFirstName = editFirstName.getText().toString().trim();
        String updatedLastName = editLastName.getText().toString().trim();
        String updatedDOB = editDOB.getText().toString().trim();
        String updatedGender = editGender.getSelectedItem().toString();

        if (updatedFirstName.isEmpty() || updatedLastName.isEmpty() || updatedDOB.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updatedProfile = new HashMap<>();
        updatedProfile.put("firstName", updatedFirstName);
        updatedProfile.put("lastName", updatedLastName);
        updatedProfile.put("dob", updatedDOB);
        updatedProfile.put("gender", updatedGender);

        db.collection("users").document(uid).update(updatedProfile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, YourProfileActivity.class));

                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Failed to update profile: " + e.getMessage(), e);
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                });
    }
}
