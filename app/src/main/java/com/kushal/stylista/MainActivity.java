package com.kushal.stylista;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnInfo).setOnClickListener(v -> showDetailActivity());

        findViewById(R.id.image).setOnClickListener(v -> showDetailActivity());
    }

    private void showDetailActivity(){
        Intent intent = new Intent(this, ClothDetailActivity.class);
        startActivity(intent);
    }
}