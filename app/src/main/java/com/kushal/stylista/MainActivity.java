package com.kushal.stylista;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import com.kushal.stylista.adapter.SwipeCardStackAdapter;
import com.kushal.stylista.model.ClothModel;
import com.kushal.stylista.utils.DummyData;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;
import com.yuyakaido.android.cardstackview.SwipeableMethod;


public class MainActivity extends AppCompatActivity implements CardStackListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    private CardStackView cardStackView;
    private SwipeCardStackAdapter swipeCardStackAdapter;
    private CardStackLayoutManager cardStackLayoutManager;
    private AppCompatImageButton btnUndo, btnReject, btnInfo, btnFavorite, btnAddToCart;

    private TextView categoryTextView, titleTextView, priceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        categoryTextView = findViewById(R.id.category);
        titleTextView = findViewById(R.id.title);
        priceTextView = findViewById(R.id.tvPrice);

        cardStackView = findViewById(R.id.swipeCardStack);
        Log.e(TAG, "onCreate: view set");
       initializeSwipeCard();
        Log.e(TAG, "onCreate: swipe card set");


//        findViewById(R.id.image).setOnClickListener(v -> showDetailActivity());

        findViewById(R.id.btnSetting).setOnClickListener(v -> showFavoriteList());

        btnUndo = findViewById(R.id.btnUndo);
        btnReject = findViewById(R.id.btnReject);
        btnInfo = findViewById(R.id.btnInfo);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        btnUndo.setOnClickListener(v -> onUndoClicked());
        btnReject.setOnClickListener(v -> onRejectClicked());
        btnFavorite.setOnClickListener(v -> onFavoriteClicked());
        btnInfo.setOnClickListener(v -> showDetailActivity());
        btnAddToCart.setOnClickListener(v -> onAddToCartClicked());
    }

    private void initializeSwipeCard(){
        cardStackLayoutManager = new CardStackLayoutManager(this, this);
        swipeCardStackAdapter = new SwipeCardStackAdapter(DummyData.getDummyClothes());
        cardStackLayoutManager.setStackFrom(StackFrom.Top);
        cardStackLayoutManager.setVisibleCount(3);
        cardStackLayoutManager.setTranslationInterval(8.0f);
        cardStackLayoutManager.setScaleInterval(0.95f);
        cardStackLayoutManager.setSwipeThreshold(0.3f);
        cardStackLayoutManager.setMaxDegree(20.0f);
        cardStackLayoutManager.setDirections(Direction.HORIZONTAL);
        cardStackLayoutManager.setCanScrollHorizontal(true);
        cardStackLayoutManager.setCanScrollVertical(true);
        cardStackLayoutManager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual);
        cardStackLayoutManager.setOverlayInterpolator(new LinearInterpolator());
        cardStackView.setLayoutManager(cardStackLayoutManager);
        cardStackView.setAdapter(swipeCardStackAdapter);

    }

    private void showDetailActivity(){
        Intent intent = new Intent(this, ClothDetailActivity.class);
        startActivity(intent);
    }

    private void showFavoriteList(){
        Intent intent = new Intent(this, FavoriteListActivity.class);
        startActivity(intent);
    }

    private void onUndoClicked() {
        cardStackView.rewind();
    }

    private void onRejectClicked() {
        swipeCard(Direction.Left);
    }

    private void onFavoriteClicked() {
        swipeCard(Direction.Top);

    }

    private void onAddToCartClicked() {
        swipeCard(Direction.Right);
    }

    private void swipeCard(Direction direction){
        SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                .setDirection(direction)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(new AccelerateInterpolator())
                .build();
        cardStackLayoutManager.setSwipeAnimationSetting(setting);
        cardStackView.swipe();
    }

    @Override
    public void onCardAppeared(@Nullable View view, int i) {
        Log.e(TAG, "onCardAppeared: "+ i);
        ClothModel clothModel = swipeCardStackAdapter.getClothModelList().get(i);
        categoryTextView.setText(clothModel.getCategory());
        titleTextView.setText(clothModel.getName());
        priceTextView.setText(getString(R.string.price, clothModel.getPrice()));
    }

    @Override
    public void onCardDragging(@Nullable Direction direction, float v) {

    }

    @Override
    public void onCardSwiped(@Nullable Direction direction) {
        Log.e(TAG, "onCardSwiped: "+ direction);
    }

    @Override
    public void onCardRewound() {

    }

    @Override
    public void onCardCanceled() {

    }

    @Override
    public void onCardDisappeared(@Nullable View view, int i) {
        if(i == swipeCardStackAdapter.getItemCount()-1) {
            categoryTextView.setText("");
            titleTextView.setText("");
            priceTextView.setText("");
        }
    }
}