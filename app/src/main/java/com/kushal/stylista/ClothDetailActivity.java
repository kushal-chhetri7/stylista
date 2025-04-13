package com.kushal.stylista;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.kushal.stylista.model.ClothModel;
import com.kushal.stylista.services.CartRepository;
import com.kushal.stylista.utils.SnackbarHelper;
import com.kushal.stylista.utils.SnackbarType;

public class ClothDetailActivity extends AppCompatActivity {

    public static final String EXTRA_CLOTH = "cloth";
    private final CartRepository cartRepository = new CartRepository();
    int count = 1;
    private TextView tvCategory, tvTitle, tvPrice, tvDescription, tvCartCount;
    private AppCompatImageView imageCloth;
    private AppCompatButton btnAddToCard;
    private AppCompatImageButton btnFavorite, btnMinus, btnAdd;
    private ClothModel clothModel;

    public static void start(Context context, ClothModel clothModel) {
        Intent intent = new Intent(context, ClothDetailActivity.class);
        intent.putExtra(EXTRA_CLOTH, clothModel);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloth_detail);
        if (getIntent().getSerializableExtra(EXTRA_CLOTH) != null) {
            clothModel = (ClothModel) getIntent().getSerializableExtra(EXTRA_CLOTH);
        }
        initViews();

        btnAdd.setOnClickListener(v -> onChangeCartCount(count + 1));
        btnMinus.setOnClickListener(v -> onChangeCartCount(count - 1));
        btnAddToCard.setOnClickListener(v -> onAddToCartPressed());
    }

    private void initViews() {
        tvCategory = findViewById(R.id.category);
        tvTitle = findViewById(R.id.title);
        tvPrice = findViewById(R.id.tvPrice);
        tvDescription = findViewById(R.id.tvDescription);
        tvCartCount = findViewById(R.id.tvItemCount);
        imageCloth = findViewById(R.id.image);
        btnMinus = findViewById(R.id.btnMinus);
        btnAdd = findViewById(R.id.btnAdd);
        btnAddToCard = findViewById(R.id.btnAddToCart);
        btnFavorite = findViewById(R.id.btnFavorite);

        if (clothModel != null) {
            tvTitle.setText(clothModel.getName());
            tvCategory.setText(clothModel.getCategory());
            tvPrice.setText(getString(R.string.price, clothModel.getPrice()));
            tvDescription.setText(clothModel.getDescription());
            btnFavorite.setImageResource(clothModel.isFavorite() ? R.drawable.ic_favorite : R.drawable.ic_favorite_outline);
            Glide.with(this).load(clothModel.getImageUrl())
                    .centerCrop()
                    .placeholder(R.drawable.image_placeholder)
                    .fallback(R.drawable.no_image)
                    .into(imageCloth);
        }
    }

    public void onBackPressed(View view) {
        finish();
    }

    private void onChangeCartCount(int count) {
        if (count < 1) {
            return;
        }
        this.count = count;
        tvCartCount.setText(String.valueOf(count));
    }

    private void onAddToCartPressed() {
        cartRepository.addToCart(clothModel,
                count,
                clothModel.getColors().get(0).getName(),
                clothModel.getColors().get(0).getSizes().get(0).getSize(),
                new ApiSaveResponseCallback() {

                    @Override
                    public void onSuccess() {
                        SnackbarHelper.showSnackbar(findViewById(android.R.id.content), "Added to cart successfully", SnackbarType.SUCCESS);
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        SnackbarHelper.showSnackbar(findViewById(android.R.id.content), exception.getMessage(), SnackbarType.ERROR);
                    }
                });
    }
}