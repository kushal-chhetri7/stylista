package com.kushal.stylista;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private ImageView editProfileImageButton;
    private TextView nameTextView;
    private static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseAuth auth;
    private FirebaseDatabase realtimeDatabase;
    private FirebaseFirestore firestore;

    private ImageView backButton;

    private String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_settings);

        auth = FirebaseAuth.getInstance();
        realtimeDatabase = FirebaseDatabase.getInstance();
        firestore = FirebaseFirestore.getInstance();

        profileImageView = findViewById(R.id.profileImageView);
        editProfileImageButton = findViewById(R.id.editProfileImageButton);
        nameTextView = findViewById(R.id.nameTextView);
        uid = getIntent().getStringExtra("uid");


        LinearLayout profileMenuItem = findViewById(R.id.profileMenuItem);

        profileMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, YourProfileActivity.class).putExtra("uid", uid);
                startActivity(intent);
            }
        });

        LinearLayout privacyMenuItem = findViewById(R.id.privacyMenuItem);

        privacyMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, PrivacyActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout helpMenuItem = findViewById(R.id.helpMenuItem);

        helpMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });



        loadFullName();
        loadProfileImageFromRealtimeDatabase();

        editProfileImageButton.setOnClickListener(v -> openImagePicker());

        findViewById(R.id.logoutMenuItem).setOnClickListener(v -> showLogoutConfirmationDialog());

        findViewById(R.id.backButton).setOnClickListener(v -> showMain());

        findViewById(R.id.ordersMenuItem).setOnClickListener(v -> {
            startActivity(new Intent(SettingsActivity.this, OrderListActivity.class));
        });
    }

    private void loadFullName() {
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();

            firestore.collection("users").document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String firstName = documentSnapshot.getString("firstName");
                            String lastName = documentSnapshot.getString("lastName");

                            if (firstName != null && lastName != null) {
                                String fullName = firstName + " " + lastName;
                                nameTextView.setText(fullName);
                            } else {
                                nameTextView.setText("Name not available");
                            }
                        } else {
                            nameTextView.setText("Guest");
                        }
                    })
                    .addOnFailureListener(e -> {
                        nameTextView.setText("Failed to load name: " + e.getMessage());
                        Log.e("FirestoreError", "Error getting document", e);
                    });
        } else {
            nameTextView.setText("User not authenticated");
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    profileImageView.setImageBitmap(bitmap);
                    uploadImageToRealtimeDatabase(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Image loading failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void uploadImageToRealtimeDatabase(Bitmap bitmap) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) return;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        byte[] imageBytes = baos.toByteArray();
        String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        realtimeDatabase.getReference("users")
                .child(currentUser.getUid())
                .child("profileImageBase64")
                .setValue(base64Image)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Profile picture updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadProfileImageFromRealtimeDatabase() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) return;

        realtimeDatabase.getReference("users")
                .child(currentUser.getUid())
                .child("profileImageBase64")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String base64Image = snapshot.getValue(String.class);
                        if (base64Image != null && !base64Image.isEmpty()) {
                            byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                            profileImageView.setImageBitmap(bitmap);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SettingsActivity.this, "Failed to load profile image", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showLogoutConfirmationDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(SettingsActivity.this);
        View sheetView = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.logout_confirmation_bottom_sheet, null);

        Button cancelButton = sheetView.findViewById(R.id.cancelButton);
        Button confirmButton = sheetView.findViewById(R.id.confirmButton);

        cancelButton.setOnClickListener(v -> bottomSheetDialog.dismiss());

        confirmButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(SettingsActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }
    private void showMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
