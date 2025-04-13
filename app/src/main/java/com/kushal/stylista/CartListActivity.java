package com.kushal.stylista;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kushal.stylista.adapter.CartListAdapter;
import com.kushal.stylista.listeners.OnCartListClickListener;
import com.kushal.stylista.model.CartModel;
import com.kushal.stylista.services.ApiListResponseCallback;
import com.kushal.stylista.services.CartRepository;
import com.kushal.stylista.utils.SnackbarHelper;
import com.kushal.stylista.utils.SnackbarType;

import java.util.ArrayList;
import java.util.List;

public class CartListActivity extends AppCompatActivity implements OnCartListClickListener {
    private static final String TAG = "CartListActivity";

    private RecyclerView cartRecyclerView;
    private ProgressBar progressBar;
    private CartListAdapter cartListAdapter;
    private CartRepository cartRepository = new CartRepository();
    private TextView tvSubtotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressBar = findViewById(R.id.progressBar);

        tvSubtotal = findViewById(R.id.tvSubtotalPrice);

        cartRecyclerView = findViewById(R.id.listCart);
        cartListAdapter = new CartListAdapter(new ArrayList<>(), this);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(cartListAdapter);


        fetchCartItems();
    }

    private void updateSubtotal() {
        double subtotal = calculateSubtotal();
        tvSubtotal.setText(getString(R.string.price, subtotal));
    }

    private double calculateSubtotal() {
        double subtotal = 0.0;
        for (CartModel cartItem : cartListAdapter.getCartModelList()) {
            subtotal += cartItem.getSubtotal();
        }
        return subtotal;
    }

    private void fetchCartItems() {
        progressBar.setVisibility(View.VISIBLE);

        cartRepository.getCart(new ApiListResponseCallback<CartModel>() {
            @Override
            public void onSuccess(List<CartModel> clothes) {
                Log.e(TAG, "onSuccess: " + clothes.size());
                cartListAdapter.setCartModelList(clothes);
                progressBar.setVisibility(View.GONE);
                updateSubtotal();
            }

            @Override
            public void onFailure(Exception exception) {
                SnackbarHelper.showSnackbar(cartRecyclerView, exception.getMessage(), SnackbarType.ERROR);
                progressBar.setVisibility(View.GONE);

            }
        });
    }

    public void onBackPressed(View view) {
        finish();
    }

    @Override
    public void onDeletePressed(int position, CartModel cartModel) {
        cartRepository.deleteCartItem(cartModel.getId(), new ApiSaveResponseCallback() {

            @Override
            public void onSuccess() {
                SnackbarHelper.showSnackbar(cartRecyclerView, "Item deleted successfully", SnackbarType.SUCCESS);
                cartListAdapter.removeItem(position);
                updateSubtotal();
            }

            @Override
            public void onFailure(Exception exception) {
                SnackbarHelper.showSnackbar(cartRecyclerView, exception.getMessage(), SnackbarType.ERROR);
            }
        });
    }
}