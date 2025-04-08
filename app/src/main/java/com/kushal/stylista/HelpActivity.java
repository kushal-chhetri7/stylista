package com.kushal.stylista;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.FirebaseApp;
public class HelpActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TextView faqTab, contactUsTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_help);

        findViewById(R.id.backButton).setOnClickListener(v -> showMain());

        faqTab = findViewById(R.id.faqTab);
        contactUsTab = findViewById(R.id.contactUsTab);
        viewPager = findViewById(R.id.viewPager);

        viewPager.setAdapter(new HelpPagerAdapter(this));

        faqTab.setOnClickListener(v -> viewPager.setCurrentItem(0));
        contactUsTab.setOnClickListener(v -> viewPager.setCurrentItem(1));

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                highlightTab(position);
            }
        });

        highlightTab(0);
    }

    private void highlightTab(int position) {
        if (position == 0) {
            faqTab.setTextColor(getResources().getColor(R.color.black));
            contactUsTab.setTextColor(getResources().getColor(R.color.gray));
        } else {
            contactUsTab.setTextColor(getResources().getColor(R.color.black));
            faqTab.setTextColor(getResources().getColor(R.color.gray));
        }
    }

    private void showMain() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
