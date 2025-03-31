package com.kushal.stylista;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kushal.stylista.adapter.FavoriteListAdapter;
import com.kushal.stylista.model.FavoriteModel;
import com.kushal.stylista.utils.DummyData;

import java.util.ArrayList;

public class FavoriteListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavoriteListAdapter favoriteListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorite_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        favoriteListAdapter = new FavoriteListAdapter(DummyData.getFavoriteList());
        recyclerView = findViewById(R.id.listFavorite);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(favoriteListAdapter);
    }

    public void onBackPressed(View view) {
        finish();
    }
}