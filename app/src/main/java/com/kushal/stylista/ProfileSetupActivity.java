package com.kushal.stylista;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProfileSetupActivity extends AppCompatActivity {

    private EditText dobEditText, firstNameEditText, lastNameEditText;
    private Spinner genderSpinner;
    private Calendar calendar;
    private FirebaseFirestore db;
    private String email, uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_setup);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get email and UID from intent
        email = getIntent().getStringExtra("email");
        uid = getIntent().getStringExtra("uid");

        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        dobEditText = findViewById(R.id.dobEditText);
        genderSpinner = findViewById(R.id.genderSpinner);
        calendar = Calendar.getInstance();

        String[] genderOptions = {"Male", "Female"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                genderOptions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        ImageView spinnerIcon = findViewById(R.id.spinnerIcon);
        spinnerIcon.setOnClickListener(v -> genderSpinner.performClick());

        dobEditText.setOnClickListener(v -> showDatePickerDialog());
        dobEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showDatePickerDialog();
            }
        });

        findViewById(R.id.nextButton).setOnClickListener(v -> validateFields());
    }

    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateInView();
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                ProfileSetupActivity.this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void updateDateInView() {
        String dateFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        dobEditText.setText(sdf.format(calendar.getTime()));
    }

    private void validateFields() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String dob = dobEditText.getText().toString().trim();
        String gender = genderSpinner.getSelectedItem().toString();

        if (firstName.isEmpty()) {
            firstNameEditText.setError("First name is required");
            firstNameEditText.requestFocus();
            return;
        }
        if (lastName.isEmpty()) {
            lastNameEditText.setError("Last name is required");
            lastNameEditText.requestFocus();
            return;
        }
        if (dob.isEmpty()) {
            dobEditText.setError("Date of birth is required");
            dobEditText.requestFocus();
            return;
        }
        if (gender.isEmpty()) {
            Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show();
            genderSpinner.requestFocus();
            return;
        }

        saveProfileToFirestore(firstName, lastName, dob, gender);
    }

    private void saveProfileToFirestore(String firstName, String lastName, String dob, String gender) {
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("email", email);
        userProfile.put("firstName", firstName);
        userProfile.put("lastName", lastName);
        userProfile.put("dob", dob);
        userProfile.put("gender", gender);
        userProfile.put("profileCompleted", true);

        db.collection("users").document(uid)
                .set(userProfile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ProfileSetupActivity.this, ClothPreferenceActivity.class).putExtra("uid", uid));
                    finish();

                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Failed to save profile: " + e.getMessage(), e); // Log error message
                    Toast.makeText(this, "Failed to save profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}