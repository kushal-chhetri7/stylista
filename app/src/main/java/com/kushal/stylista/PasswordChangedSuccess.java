package com.kushal.stylista;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class PasswordChangedSuccess  extends AppCompatActivity {

    private Button backToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_changedpasswordsuccess);

        backToLogin = findViewById(R.id.backToLoginButton);

        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PasswordChangedSuccess.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}
