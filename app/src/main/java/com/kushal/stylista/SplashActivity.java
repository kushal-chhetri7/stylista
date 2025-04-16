package com.kushal.stylista;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.kushal.stylista.admin.AdminMainActivity;
import com.kushal.stylista.model.UserModel;
import com.kushal.stylista.services.ApiDataResponseCallback;
import com.kushal.stylista.services.UserRepository;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        UserRepository userRepository = new UserRepository();

        userRepository.getCurrentUser(new ApiDataResponseCallback<UserModel>() {
            @Override
            public void onSuccess(UserModel item) {
                finish();
                if (item == null) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    return;
                }
                startActivity(new Intent(SplashActivity.this, item.isAdmin() ? AdminMainActivity.class : MainActivity.class));

            }

            @Override
            public void onFailure(Exception exception) {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
        });

    }
}