package com.kushal.stylista;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class YourProfileActivity extends AppCompatActivity {

    private String uid;
    private LinearLayout deleteAccountMenuItem;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_profile);

        uid = getIntent().getStringExtra("uid");
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        LinearLayout profileMenuItem = findViewById(R.id.profileMenuItem);
        LinearLayout passwordMenuItem = findViewById(R.id.passwordMenuItem);
        deleteAccountMenuItem = findViewById(R.id.DeleteAccountMenuItem);

        passwordMenuItem.setOnClickListener(v -> {
            Intent intent = new Intent(YourProfileActivity.this, PasswordManagerActivity.class).putExtra("uid", uid);
            startActivity(intent);
        });

        profileMenuItem.setOnClickListener(v -> {
            Intent intent = new Intent(YourProfileActivity.this, PersonalDetailsActivity.class).putExtra("uid", uid);
            startActivity(intent);
        });

        findViewById(R.id.backButton).setOnClickListener(v -> showMain());

        deleteAccountMenuItem.setOnClickListener(v -> showDeleteAccountBottomSheet());
    }

    private void showMain() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void showDeleteAccountBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.account_delete_bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        TextView titleTextView = bottomSheetView.findViewById(R.id.titleTextView);
        Spinner reasonSpinner = bottomSheetView.findViewById(R.id.reasonSpinner);
        CheckBox confirmationCheckBox = bottomSheetView.findViewById(R.id.confirmationCheckBox);
        Button deleteAccountButton = bottomSheetView.findViewById(R.id.deleteAccountButton);
        TextView legalDisclaimerTextView = bottomSheetView.findViewById(R.id.legalDisclaimerTextView);
        ImageView spinnerIcon = bottomSheetView.findViewById(R.id.spinnerIcon);

        // Setup the spinner
        String[] deleteReasons = getResources().getStringArray(R.array.delete_reasons);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                deleteReasons
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reasonSpinner.setAdapter(adapter);

        spinnerIcon.setOnClickListener(v -> reasonSpinner.performClick());

        deleteAccountButton.setOnClickListener(v -> {
            if (confirmationCheckBox.isChecked()) {
                String selectedReason = reasonSpinner.getSelectedItem().toString();
                deleteUserAccount();
                bottomSheetDialog.dismiss();
            } else {
                Toast.makeText(YourProfileActivity.this, "Please confirm that you want to delete your account.", Toast.LENGTH_SHORT).show();
            }
        });

        bottomSheetDialog.show();
    }

    private void deleteUserAccount() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            firestore.collection("users").document(user.getUid())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        deleteFirebaseAuthAccount(user);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(YourProfileActivity.this, "Error deleting user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        } else {
            Toast.makeText(YourProfileActivity.this, "No user logged in.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteFirebaseAuthAccount(FirebaseUser user) {
        user.delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(YourProfileActivity.this, "Account deleted successfully.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(YourProfileActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(YourProfileActivity.this, "Error deleting account: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}