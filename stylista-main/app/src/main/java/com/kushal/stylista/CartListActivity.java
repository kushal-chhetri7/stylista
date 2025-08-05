package com.kushal.stylista;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.kushal.stylista.adapter.CartListAdapter;
import com.kushal.stylista.api.ApiService;
import com.kushal.stylista.api.RetrofitClient;
import com.kushal.stylista.api.request.ConfirmPaymentRequest;
import com.kushal.stylista.api.request.PaymentIntentRequest;
import com.kushal.stylista.api.response.PaymentIntentResponse;
import com.kushal.stylista.listeners.OnCartListClickListener;
import com.kushal.stylista.model.CartModel;
import com.kushal.stylista.services.ApiListResponseCallback;
import com.kushal.stylista.services.CartRepository;
import com.kushal.stylista.utils.SnackbarHelper;
import com.kushal.stylista.utils.SnackbarType;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartListActivity extends AppCompatActivity implements OnCartListClickListener {
    private static final String TAG = "CartListActivity";
    String firebaseToken;
    String paymentId;
    private RecyclerView cartRecyclerView;
    private ProgressBar progressBar;
    private CartListAdapter cartListAdapter;
    private CartRepository cartRepository = new CartRepository();
    private TextView tvSubtotal;
    private Stripe stripe;
    private Button checkoutButton;
    private double total;
    private PaymentSheet paymentSheet;

    private boolean isPaymentProcessing = false;

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
        checkoutButton = findViewById(R.id.btnCheckout);

        cartRecyclerView = findViewById(R.id.listCart);
        cartListAdapter = new CartListAdapter(new ArrayList<>(), this);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(cartListAdapter);
        checkoutButton.setOnClickListener(v -> initPayment());

        fetchCartItems();

        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_51RC4iSPdpSUdIMwcAHSNd12imUvE1w3XEyS1pkmoOdqrttDqMdXTrpydhXKOKGXkJlYKqSaB9gVCo9FvBadYZAjD00Z1P8mcNi" // your Stripe publishable key
        );

        stripe = new Stripe(
                getApplicationContext(),
                PaymentConfiguration.getInstance(getApplicationContext()).getPublishableKey()
        );

        configPaymentSheet();


    }

    private void updateSubtotal() {
        total = calculateSubtotal();
        tvSubtotal.setText(getString(R.string.price, total));
    }

    private double calculateSubtotal() {
        double subtotal = 0.0;
        for (CartModel cartItem : cartListAdapter.getCartModelList()) {
            subtotal += cartItem.getSubtotal();
        }
        return subtotal;
    }

    private void onProcessingError(String error){
        SnackbarHelper.showSnackbar(cartRecyclerView, error, SnackbarType.ERROR);
        isPaymentProcessing = false;
    }

    private void fetchCartItems() {
        progressBar.setVisibility(View.VISIBLE);

        cartRepository.getCart(new ApiListResponseCallback<CartModel>() {
            @Override
            public void onSuccess(List<CartModel> dataList) {
                Log.e(TAG, "onSuccess: " + dataList.size());
                cartListAdapter.setCartModelList(dataList);
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

    private void initPayment() {
        isPaymentProcessing = true;
        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String idToken = task.getResult().getToken();
                        firebaseToken = idToken;
                        // Now call your API with this bearer token
                        createPaymentIntent(total * 100, "Bearer " + idToken);
                    } else {
                        // Handle error
                        isPaymentProcessing = false;
                    }
                });
    }

    private void createPaymentIntent(double amount, String bearerToken) {
        ApiService api = RetrofitClient.getApiService();
        PaymentIntentRequest request = new PaymentIntentRequest(amount);
        api.createPaymentIntent(request, bearerToken).enqueue(new Callback<PaymentIntentResponse>() {
            @Override
            public void onResponse(Call<PaymentIntentResponse> call, Response<PaymentIntentResponse> response) {
                Log.e(TAG, "onResponse: " + response.message());
                if (response.isSuccessful()) {
                    String clientSecret = response.body().clientSecret;
                    paymentId = response.body().paymentIntentId;
                    startPaymentFlow(clientSecret);
                }
            }

            @Override
            public void onFailure(Call<PaymentIntentResponse> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
            }
        });
    }

    private void configPaymentSheet() {
        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {
            if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
                Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show();
                confirmPayment(paymentId);
            } else {
                Toast.makeText(this, "Payment failed!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void startPaymentFlow(String clientSecret) {
        PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("Stylista Store")
                .allowsDelayedPaymentMethods(true)
                .build();

        paymentSheet.presentWithPaymentIntent(clientSecret, configuration);
    }

    private void confirmPayment(String paymentIntentId) {
        ApiService apiService = RetrofitClient.getApiService();
        ConfirmPaymentRequest request = new ConfirmPaymentRequest(paymentIntentId);

        apiService.confirmPayment(request, "Bearer " + firebaseToken).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    updateOrderAfterConfirm(paymentIntentId);
                } else {
                    Log.e(TAG, "Payment confirmation failed: " + response.message());
                    Toast.makeText(CartListActivity.this, "Payment confirmation failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                onProcessingError(t.getMessage());
            }
        });
    }

    private void updateOrderAfterConfirm(String paymentId) {
        cartRepository.confirmOrder(cartListAdapter.getCartModelList(), total, paymentId, new ApiSaveResponseCallback() {
            @Override
            public void onSuccess() {
                SnackbarHelper.showSnackbar(cartRecyclerView, "Order confirmed successfully", SnackbarType.SUCCESS);
                finish();
                startActivity(new Intent(CartListActivity.this, OrderConfirmationActivity.class));
            }

            @Override
            public void onFailure(Exception exception) {
                exception.printStackTrace();
                onProcessingError(exception.getMessage());
            }
        });
    }
}