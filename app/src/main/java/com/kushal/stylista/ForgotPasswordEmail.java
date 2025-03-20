package com.kushal.stylista;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class ForgotPasswordEmail extends AppCompatActivity {

    private ImageView backButton;

    private TextView rememberPasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgotpassword);


        backButton = findViewById(R.id.backButton);
        rememberPasswordText = findViewById(R.id.rememberPasswordText);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordEmail.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        rememberPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordEmail.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
