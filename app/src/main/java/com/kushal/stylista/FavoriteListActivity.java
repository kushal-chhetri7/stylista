package com.kushal.stylista;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kushal.stylista.adapter.FavoriteListAdapter;
import com.kushal.stylista.listeners.OnFavoriteClickListener;
import com.kushal.stylista.model.ClothModel;
import com.kushal.stylista.services.ApiListResponseCallback;
import com.kushal.stylista.services.ClothRepository;
import com.kushal.stylista.utils.SnackbarHelper;
import com.kushal.stylista.utils.SnackbarType;

import java.util.ArrayList;
import java.util.List;

public class FavoriteListActivity extends AppCompatActivity {
    private static final String TAG = "FavoriteListActivity";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private FavoriteListAdapter favoriteListAdapter;
    private ClothRepository repository = new ClothRepository();

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

        favoriteListAdapter = new FavoriteListAdapter(new ArrayList<>(), new OnFavoriteClickListener() {
            @Override
            public void onClothClicked(ClothModel clothModel) {
                ClothDetailActivity.start(FavoriteListActivity.this, clothModel);
            }

            @Override
            public void onFavoriteClicked(ClothModel clothModel) {
                onRemoveFavoriteClicked(clothModel);
            }
        });
        recyclerView = findViewById(R.id.listFavorite);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(favoriteListAdapter);

        progressBar = findViewById(R.id.progressBar);

        fetchFavorite();
    }

    public void onBackPressed(View view) {
        finish();
    }

    private void fetchFavorite() {
        progressBar.setVisibility(View.VISIBLE);
        repository.getFavoriteClothes(new ApiListResponseCallback<ClothModel>() {
            @Override
            public void onSuccess(List<ClothModel> dataList) {
                progressBar.setVisibility(View.GONE);
                favoriteListAdapter.setFavoriteModelList(dataList);
            }

            @Override
            public void onFailure(Exception exception) {
                progressBar.setVisibility(View.GONE);
                SnackbarHelper.showSnackbar(findViewById(android.R.id.content), exception.getMessage(), SnackbarType.ERROR);
            }
        });
    }

    public void onRemoveFavoriteClicked(ClothModel clothModel) {
        Log.e(TAG, "onRemoveFavoriteClicked: =======  "+clothModel.getId() );
        repository.deleteFavoriteCloth(clothModel.getId(), new ApiSaveResponseCallback() {

            @Override
            public void onSuccess() {
                favoriteListAdapter.removeCloth(clothModel);
            }

            @Override
            public void onFailure(Exception exception) {
                SnackbarHelper.showSnackbar(findViewById(android.R.id.content), exception.getMessage(), SnackbarType.ERROR);
            }
        });
    }
}