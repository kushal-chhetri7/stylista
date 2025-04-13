

package com.kushal.stylista;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ProfileSetupActivity extends AppCompatActivity {

    private EditText dobEditText, firstNameEditText, lastNameEditText;
    private Spinner genderSpinner;
    private Calendar calendar;
    private String email, uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_setup);

        email = getIntent().getStringExtra("email");
        uid = getIntent().getStringExtra("uid");

        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        dobEditText = findViewById(R.id.dobEditText);
        genderSpinner = findViewById(R.id.genderSpinner);
        calendar = Calendar.getInstance();

        String[] genderOptions = {"Male", "Female"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, genderOptions);
        genderSpinner.setAdapter(adapter);

        ImageView spinnerIcon = findViewById(R.id.spinnerIcon);
        spinnerIcon.setOnClickListener(v -> genderSpinner.performClick());

        dobEditText.setOnClickListener(v -> showDatePickerDialog());
        dobEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) showDatePickerDialog();
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
                this,
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
            return;
        }
        if (lastName.isEmpty()) {
            lastNameEditText.setError("Last name is required");
            return;
        }
        if (dob.isEmpty()) {
            dobEditText.setError("Date of birth is required");
            return;
        }

        Intent intent = new Intent(ProfileSetupActivity.this, ClothPreferenceActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("password", getIntent().getStringExtra("password"));
        intent.putExtra("provider", getIntent().getStringExtra("provider"));
        intent.putExtra("idToken", getIntent().getStringExtra("idToken"));
        intent.putExtra("accessToken", getIntent().getStringExtra("accessToken"));
        intent.putExtra("firstName", firstName);
        intent.putExtra("lastName", lastName);
        intent.putExtra("dob", dob);
        intent.putExtra("gender", gender);
        startActivity(intent);

        finish();
    }
}
