package com.kushal.stylista;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ProfileSetupActivity extends AppCompatActivity {

    private EditText dobEditText;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_setup);

        dobEditText = findViewById(R.id.dobEditText);
        calendar = Calendar.getInstance();


        Spinner genderSpinner = findViewById(R.id.genderSpinner);
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
        EditText firstNameEditText = findViewById(R.id.firstNameEditText);
        EditText lastNameEditText = findViewById(R.id.lastNameEditText);
        EditText dobEditText = findViewById(R.id.dobEditText);
        Spinner genderSpinner = findViewById(R.id.genderSpinner);

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


        proceedToNextStep();
    }

    private void proceedToNextStep() {

        Toast.makeText(this, "All fields are valid!", Toast.LENGTH_SHORT).show();
    }
}