package com.kushal.stylista;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.location.Location;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ManageAddressActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marker;
    private EditText latInput, lngInput, nameInput, phoneInput, addressInput;
    private Button saveButton;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_address);

        // UI references
        nameInput = findViewById(R.id.nameInput);
        phoneInput = findViewById(R.id.phoneInput);
        addressInput = findViewById(R.id.addressInput);
        latInput = findViewById(R.id.latInput);
        lngInput = findViewById(R.id.lngInput);
        saveButton = findViewById(R.id.saveAddressBtn);
        ImageButton backButton = findViewById(R.id.backButton);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Setup Map Fragment
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        // Back button
        backButton.setOnClickListener(v -> finish());

        // Save address
        saveButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();
            String address = addressInput.getText().toString().trim();
            String latStr = latInput.getText().toString().trim();
            String lngStr = lngInput.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (auth.getCurrentUser() == null) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            String uid = auth.getCurrentUser().getUid();
            Map<String, Object> addressData = new HashMap<>();
            addressData.put("name", name);
            addressData.put("phone", phone);
            addressData.put("address", address);
            addressData.put("latitude", latStr);
            addressData.put("longitude", lngStr);

            firestore.collection("users")
                    .document(uid)
                    .collection("addresses")
                    .add(addressData)
                    .addOnSuccessListener(docRef ->
                            Toast.makeText(this, "Address saved", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lng = location.getLongitude();

                        // Update lat/lng EditTexts
                        latInput.setText(String.valueOf(lat));
                        lngInput.setText(String.valueOf(lng));

                        // Move map marker
                        if (mMap != null) {
                            LatLng currentLoc = new LatLng(lat, lng);
                            if (marker != null) {
                                marker.setPosition(currentLoc);
                            } else {
                                marker = mMap.addMarker(new MarkerOptions().position(currentLoc).title("Delivery Location"));
                            }
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 15));
                        }

                        // Reverse geocode to get address
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                String fullAddress = addresses.get(0).getAddressLine(0);
                                addressInput.setText(fullAddress);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Hardcoded Canada Location (Toronto)
        LatLng toronto = new LatLng(43.65107, -79.347015);
        marker = mMap.addMarker(new MarkerOptions().position(toronto).title("Delivery Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(toronto, 14));

        // Update lat/lng on map tap
        mMap.setOnMapClickListener(latLng -> {
            marker.setPosition(latLng);
            latInput.setText(String.valueOf(latLng.latitude));
            lngInput.setText(String.valueOf(latLng.longitude));
        });

        // Pre-fill initial lat/lng
        latInput.setText(String.valueOf(toronto.latitude));
        lngInput.setText(String.valueOf(toronto.longitude));
    }
}
