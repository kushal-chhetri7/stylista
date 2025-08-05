package com.kushal.stylista;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import com.kushal.stylista.adapter.SwipeCardStackAdapter;
import com.kushal.stylista.model.ClothModel;
import com.kushal.stylista.services.ApiListResponseCallback;
import com.kushal.stylista.services.CartRepository;
import com.kushal.stylista.services.ClothRepository;
import com.kushal.stylista.utils.SnackbarHelper;
import com.kushal.stylista.utils.SnackbarType;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements CardStackListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    private final ClothRepository repository = new ClothRepository();
    private final CartRepository cartRepository = new CartRepository();
    List<ClothModel> clothModelList = new ArrayList<>();
    private CardStackView cardStackView;
    private SwipeCardStackAdapter swipeCardStackAdapter;
    private CardStackLayoutManager cardStackLayoutManager;
    private AppCompatImageButton btnUndo, btnReject, btnInfo, btnFavorite, btnAddToCart, btnCart;
    private TextView categoryTextView, titleTextView, priceTextView;
    private ProgressBar progressBar;
    private List<String> favorites = new ArrayList<>();
    private ClothModel currentCloth;

    private String uid;

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

        findViewById(R.id.btnSetting).setOnClickListener(v -> showSettings());

        uid = getIntent().getStringExtra("uid");


        btnUndo = findViewById(R.id.btnUndo);
        btnReject = findViewById(R.id.btnReject);
        btnInfo = findViewById(R.id.btnInfo);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnCart = findViewById(R.id.btnCart);

        progressBar = findViewById(R.id.progressBar);

        btnUndo.setOnClickListener(v -> onUndoClicked());
        btnReject.setOnClickListener(v -> onRejectClicked());
        btnFavorite.setOnClickListener(v -> onFavoriteClicked());
        btnInfo.setOnClickListener(v -> showDetailActivity());
        btnAddToCart.setOnClickListener(v -> onAddToCartClicked());
        btnCart.setOnClickListener(v -> showCartList());

        progressBar.setVisibility(VISIBLE);
        fetchFavoriteList();
        fetchData();
    }


    private void initializeSwipeCard() {
        cardStackLayoutManager = new CardStackLayoutManager(this, this);
        swipeCardStackAdapter = new SwipeCardStackAdapter(clothModelList);
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

    private void fetchFavoriteList() {
        repository.getFavoriteClothesId(new ApiListResponseCallback<String>() {
            @Override
            public void onSuccess(List<String> dataList) {
                favorites = dataList;
            }

            @Override
            public void onFailure(Exception exception) {

            }
        });
    }

    private void fetchData() {

        repository.getClothes(new ApiListResponseCallback<ClothModel>() {
            @Override
            public void onSuccess(List<ClothModel> dataList) {
                for (ClothModel cloth : dataList) {
                    cloth.setFavorite(favorites.contains(cloth.getId()));
                }
                progressBar.setVisibility(GONE);
                swipeCardStackAdapter.setClothModelList(dataList);
            }

            @Override
            public void onFailure(Exception exception) {
                progressBar.setVisibility(GONE);
            }
        });
    }

    private void showDetailActivity() {
        ClothDetailActivity.start(this, currentCloth);
    }

    private void showFavoriteList() {
        Intent intent = new Intent(this, FavoriteListActivity.class);
        startActivity(intent);
    }

    private void showSettings() {
        Intent intent = (new Intent(this, SettingsActivity.class).putExtra("uid", uid));
        startActivity(intent);
    }

    private void showCartList() {
        Intent intent = new Intent(this, CartListActivity.class);
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
        addToFavorite();
    }

    private void onAddToCartClicked() {
        swipeCard(Direction.Right);
    }


    private void swipeCard(Direction direction) {
        SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder().setDirection(direction).setDuration(Duration.Normal.duration).setInterpolator(new AccelerateInterpolator()).build();
        cardStackLayoutManager.setSwipeAnimationSetting(setting);
        cardStackView.swipe();
    }

    @Override
    public void onCardAppeared(@Nullable View view, int i) {
        Log.e(TAG, "onCardAppeared: " + i);
        currentCloth = swipeCardStackAdapter.getClothModelList().get(i);

        categoryTextView.setText(currentCloth.getCategory());
        titleTextView.setText(currentCloth.getName());
        priceTextView.setText(getString(R.string.price, currentCloth.getPrice()));

        btnFavorite.setImageResource(favorites.contains(currentCloth.getId()) ? R.drawable.ic_favorite : R.drawable.ic_favorite_outline);
    }

    @Override
    public void onCardDragging(@Nullable Direction direction, float v) {

    }

    @Override
    public void onCardSwiped(@Nullable Direction direction) {
        Log.e(TAG, "onCardSwiped: " + direction);
        if (direction == Direction.Right) {
            addToCart();
        }
    }

    @Override
    public void onCardRewound() {

    }

    @Override
    public void onCardCanceled() {

    }

    @Override
    public void onCardDisappeared(@Nullable View view, int i) {
        if (i == swipeCardStackAdapter.getItemCount() - 1) {
            categoryTextView.setText("");
            titleTextView.setText("");
            priceTextView.setText("");
            currentCloth = null;
        }
    }

    private void addToFavorite() {
        if (currentCloth != null) {
            repository.updateFavoriteCloth(currentCloth.getId(), new ApiSaveResponseCallback() {
                @Override
                public void onSuccess() {
                    favorites.add(currentCloth.getId());
                }

                @Override
                public void onFailure(Exception exception) {
                    exception.printStackTrace();
                    SnackbarHelper.showSnackbar(findViewById(android.R.id.content), exception.getMessage(), SnackbarType.ERROR);
                    onUndoClicked();
                }
            });
        }
    }

    private void addToCart() {
        if (currentCloth != null) {
            cartRepository.addToCart(
                    currentCloth,
                    1,
                    currentCloth.getColors().get(0).getName(),
                    currentCloth.getColors().get(0).getSizes().get(0).getSize(),
                    new ApiSaveResponseCallback() {
                        @Override
                        public void onSuccess() {
                            SnackbarHelper.showSnackbar(findViewById(android.R.id.content), "Added to cart successfully", SnackbarType.SUCCESS);
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            exception.printStackTrace();
                            cardStackView.rewind();
                            SnackbarHelper.showSnackbar(findViewById(android.R.id.content), exception.getMessage(), SnackbarType.ERROR);
                        }
                    });
        }
    }

}